package com.example.rpomp_20_8;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Polyline routePolyline;
    private final List<LatLng> locations = new ArrayList<>();
    private TextView tvStatus;
    private boolean isTracking = false;


    private AppDatabase db;


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Загружаем сохранённые точки из базы данных
        new Thread(() -> {
            List<LocationPoint> savedPoints = db.locationDao().getAllPoints();
            for (LocationPoint point : savedPoints) {
                LatLng latLng = new LatLng(point.latitude, point.longitude);
                locations.add(latLng);
            }

            runOnUiThread(() -> {
                if (!locations.isEmpty()) {
                    routePolyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(locations)
                            .color(Color.BLUE)
                            .width(10));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 15));
                }
            });
        }).start();
    }


    private void updateMap(LatLng newLocation) {
        locations.add(newLocation);

        // Сохраняем координату в базу данных
        new Thread(() -> {
            LocationPoint point = new LocationPoint(
                    newLocation.latitude,
                    newLocation.longitude,
                    System.currentTimeMillis()
            );
            db.locationDao().insert(point);
        }).start();

        if (isTracking) {
            mMap.addMarker(new MarkerOptions()
                    .position(newLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Current position"));
        }

        // Обновление маршрута
        if (routePolyline != null) routePolyline.remove();
        routePolyline = mMap.addPolyline(new PolylineOptions()
                .addAll(locations)
                .color(Color.BLUE)
                .width(10));

        // Добавление маркеров
        if (locations.size() == 1) {
            mMap.addMarker(new MarkerOptions()
                    .position(newLocation)
                    .title("Start"));
        } else if (locations.size() > 1) {
            mMap.addMarker(new MarkerOptions()
                    .position(newLocation)
                    .title("Point " + locations.size()));
        }

        // Обновление камеры
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));

        showStats();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = AppDatabase.getInstance(this);



        tvStatus = findViewById(R.id.tvStatus);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupButtons();
        createLocationCallback();
    }

    private void setupButtons() {
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnClear = findViewById(R.id.btnClear);

        btnStart.setOnClickListener(v -> startTracking());
        btnStop.setOnClickListener(v -> stopTracking());
        btnClear.setOnClickListener(v -> clearMap());
    }

    private void clearMap() {
        if (routePolyline != null) routePolyline.remove();
        locations.clear();
        mMap.clear();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (android.location.Location location : locationResult.getLocations()) {
                    LatLng newLocation = new LatLng(
                            location.getLatitude(),
                            location.getLongitude()
                    );
                    updateMap(newLocation);
                }
            }
        };
    }

    private void startTracking() {
        if (checkPermissions()) {
            isTracking = true;
            updateUI();
            requestLocationUpdates();
            showTrackingStartedNotification();
        }
    }

    private void stopTracking() {
        isTracking = false;
        updateUI();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        runOnUiThread(() -> {
            if (isTracking) {
                tvStatus.setText("Status: Tracking active");
                tvStatus.setTextColor(Color.GREEN);
                findViewById(R.id.btnStart).setEnabled(false);
                findViewById(R.id.btnStop).setEnabled(true);
            } else {
                tvStatus.setText("Status: Not tracking");
                tvStatus.setTextColor(Color.RED);
                findViewById(R.id.btnStart).setEnabled(true);
                findViewById(R.id.btnStop).setEnabled(false);
            }
        });
    }

    private void showTrackingStartedNotification() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Tracking started!", Toast.LENGTH_SHORT).show();

            // Анимация пульсации статуса
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
            tvStatus.startAnimation(pulse);
        });
    }



    private void showStats() {
        String stats = "Points tracked: " + locations.size();
        Toast.makeText(this, stats, Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
        return false;
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            }
        }
    }


}