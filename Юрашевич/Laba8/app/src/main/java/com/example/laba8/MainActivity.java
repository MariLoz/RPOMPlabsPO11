package com.example.laba8;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "RoutePrefs";
    private static final String ROUTE_KEY = "saved_route";
    private static final String TAG = "MainActivity";

    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private IMapController mapController;
    private List<GeoPoint> geoPoints = new ArrayList<>();
    private Polyline polyline;
    private Button buttonStats;
    private long routeStartTime;
    private GeoPoint currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        setContentView(R.layout.activity_main);

        try {
            mapView = findViewById(R.id.map);
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapController = mapView.getController();
            mapController.setZoom(15.0);
            mapView.setMultiTouchControls(true);

            polyline = new Polyline();
            polyline.setWidth(15);
            polyline.setColor(ContextCompat.getColor(this, R.color.blue_primary));
            mapView.getOverlayManager().add(polyline);

            buttonStats = findViewById(R.id.buttonStats);
            buttonStats.setOnClickListener(v -> showStatsDialog());

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            requestLocationPermission();

        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
            showAlertDialog("Ошибка", "Не удалось инициализировать приложение");
        }
    }

    private void showStatsDialog() {
        if (geoPoints == null || geoPoints.isEmpty()) {
            showAlertDialog("Статистика", "Нет данных о маршруте.");
            return;
        }

        try {
            String statsText = String.format(
                    "• Точек маршрута: %d\n" +
                            "• Дистанция: %.2f км\n" +
                            "• Средняя скорость: %.2f км/ч\n" +
                            "• Время в пути: %s",
                    geoPoints.size(),
                    calculateTotalDistance() / 1000,
                    calculateAvgSpeed(),
                    formatDuration()
            );
            showAlertDialog("📊 Статистика", statsText);
        } catch (Exception e) {
            Log.e(TAG, "Stats calculation error", e);
            showAlertDialog("Ошибка", "Не удалось рассчитать статистику");
        }
    }

    private double calculateTotalDistance() {
        double distance = 0;
        for (int i = 1; i < geoPoints.size(); i++) {
            distance += geoPoints.get(i).distanceToAsDouble(geoPoints.get(i - 1));
        }
        return distance;
    }

    private double calculateAvgSpeed() {
        if (geoPoints.size() < 2) return 0;
        double timeHours = (geoPoints.size() * 5.0) / 3600;
        return (calculateTotalDistance() / 1000) / timeHours;
    }

    private String formatDuration() {
        if (geoPoints.size() < 2) return "0 мин";
        int totalSeconds = geoPoints.size() * 5;
        return String.format("%d мин %d сек", totalSeconds / 60, totalSeconds % 60);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
            restoreRoute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                restoreRoute();
            } else {
                showAlertDialog("Внимание", "Для работы приложения необходимы разрешения на доступ к местоположению");
            }
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                try {
                    currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                    if (geoPoints.isEmpty()) {
                        routeStartTime = System.currentTimeMillis();
                    }

                    geoPoints.add(currentLocation);
                    updateMap();
                    saveRoute();
                } catch (Exception e) {
                    Log.e(TAG, "Location processing error", e);
                }
            }
        }
    };

    private void updateMap() {
        if (currentLocation == null || mapController == null) return;

        try {
            mapController.setCenter(currentLocation);
            polyline.setPoints(geoPoints);

            mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);
            Marker marker = new Marker(mapView);
            marker.setPosition(currentLocation);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(this, R.drawable.map_pin));
            marker.setTitle("Текущее местоположение");
            mapView.getOverlays().add(marker);
            mapView.invalidate();
        } catch (Exception e) {
            Log.e(TAG, "Map update error", e);
        }
    }

    private void startLocationUpdates() {
        try {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        } catch (Exception e) {
            Log.e(TAG, "Location updates error", e);
        }
    }

    private void saveRoute() {
        try {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            StringBuilder sb = new StringBuilder();
            for (GeoPoint point : geoPoints) {
                sb.append(point.getLatitude()).append(",").append(point.getLongitude()).append(";");
            }
            editor.putString(ROUTE_KEY, sb.toString());
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Save route error", e);
        }
    }

    private void restoreRoute() {
        try {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String savedRoute = preferences.getString(ROUTE_KEY, "");

            if (!savedRoute.isEmpty()) {
                String[] points = savedRoute.split(";");
                for (String point : points) {
                    try {
                        String[] latLng = point.split(",");
                        if (latLng.length == 2) {
                            GeoPoint geoPoint = new GeoPoint(
                                    Double.parseDouble(latLng[0]),
                                    Double.parseDouble(latLng[1])
                            );
                            geoPoints.add(geoPoint);
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Coordinate parsing error", e);
                    }
                }

                if (!geoPoints.isEmpty()) {
                    currentLocation = geoPoints.get(geoPoints.size() - 1);
                    updateMap();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Route restore error", e);
        }
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}