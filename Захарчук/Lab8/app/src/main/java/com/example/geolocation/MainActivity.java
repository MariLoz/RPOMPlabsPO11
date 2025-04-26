package com.example.geolocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView tvOut;
    private MapView map;
    private RecyclerView recyclerView;
    private LocationDatabase db;
    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);

        tvOut = findViewById(R.id.textView1);
        map = findViewById(R.id.map);
        recyclerView = findViewById(R.id.locationRecyclerView);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = new LocationDatabase(this);

        findViewById(R.id.refreshButton).setOnClickListener(v -> loadData());

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        checkPermissions();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvOut.setText("GPS is turned on...");
        } else {
            tvOut.setText("GPS is not turned on...");
        }
    }

    private void checkPermissions() {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
            loadData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startLocationService();
                loadData();
            } else {
                Toast.makeText(this, "Требуются все разрешения для работы приложения", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationService() {
        startService(new Intent(this, LocationService.class));
    }

    private void loadData() {
        Cursor cursor = db.getLocations();
        ArrayList<GeoPoint> routePoints = new ArrayList<>();
        ArrayList<LocationItem> locationItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));

                GeoPoint point = new GeoPoint(lat, lon);
                Marker marker = new Marker(map);
                marker.setPosition(point);
                marker.setTitle("Посещенное место");
                map.getOverlays().add(marker);
                routePoints.add(point);

                locationItems.add(new LocationItem(lat, lon, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (locationItems.size() < 30) {
            Toast.makeText(this, "Недостаточно данных: собрано " + locationItems.size() + " из 30 требуемых", Toast.LENGTH_LONG).show();
            tvOut.setText("Недостаточно данных: " + locationItems.size() + " точек");
        } else {
            tvOut.setText("Данные за неделю: " + locationItems.size() + " точек");
        }

        if (routePoints.size() >= 2) {
            Polyline route = new Polyline();
            route.setPoints(routePoints);
            route.setColor(0xFF0000FF);
            map.getOverlays().add(route);
        }

        IMapController mapController = map.getController();
        if (!routePoints.isEmpty()) {
            mapController.setZoom(15.0);
            mapController.setCenter(routePoints.get(0));
        } else {
            mapController.setZoom(1.0);
            mapController.setCenter(new GeoPoint(0.0, 0.0));
        }
        map.invalidate();

        adapter = new LocationAdapter(locationItems);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, LocationService.class));
        db.close();
    }

    private static class LocationItem {
        double latitude;
        double longitude;
        long timestamp;

        LocationItem(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
        private ArrayList<LocationItem> locations;

        LocationAdapter(ArrayList<LocationItem> locations) {
            this.locations = locations;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_location, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LocationItem item = locations.get(position);
            holder.latText.setText("Latitude: " + item.latitude);
            holder.lonText.setText("Longitude: " + item.longitude);
            String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(new Date(item.timestamp));
            holder.timeText.setText("Time: " + time);
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView latText, lonText, timeText;

            ViewHolder(View itemView) {
                super(itemView);
                latText = itemView.findViewById(R.id.latText);
                lonText = itemView.findViewById(R.id.lonText);
                timeText = itemView.findViewById(R.id.timeText);
            }
        }
    }
}