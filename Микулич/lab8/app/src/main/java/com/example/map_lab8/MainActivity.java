package com.example.map_lab8;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "RoutePrefs";
    private static final String ROUTE_KEY = "saved_route";

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private List<LatLng> routePoints = new ArrayList<>();
    private Polyline polyline;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button resetButton = findViewById(R.id.btn_reset);
        resetButton.setOnClickListener(v -> resetRoute());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        requestLocationPermission();
        restoreRoute();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult.getLastLocation() == null) return;

            Location location = locationResult.getLastLocation();
            LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());

            routePoints.add(newPoint);

            if (currentMarker != null) {
                currentMarker.remove();
            }

            currentMarker = mMap.addMarker(new MarkerOptions().position(newPoint).title("Ваше местоположение"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 15));

            if (polyline == null) {
                polyline = mMap.addPolyline(new PolylineOptions().width(5).color(0xFF000000)); // Черный цвет
            }
            polyline.setPoints(routePoints);
            saveRoute();
        }
    };

    private void saveRoute() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        StringBuilder sb = new StringBuilder();

        for (LatLng point : routePoints) {
            sb.append(point.latitude).append(",").append(point.longitude).append(";");
        }
        editor.putString(ROUTE_KEY, sb.toString());
        editor.apply();
    }

    private void restoreRoute() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedRoute = preferences.getString(ROUTE_KEY, "");

        if (!savedRoute.isEmpty()) {
            String[] points = savedRoute.split(";");
            for (String point : points) {
                String[] latLng = point.split(",");
                if (latLng.length == 2) {
                    double latitude = Double.parseDouble(latLng[0]);
                    double longitude = Double.parseDouble(latLng[1]);
                    routePoints.add(new LatLng(latitude, longitude));
                }
            }

            if (!routePoints.isEmpty()) {
                polyline = mMap.addPolyline(new PolylineOptions()
                        .addAll(routePoints)
                        .width(5)
                        .color(0xFF000000)); // Черный цвет маршрута

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(0), 15));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    private void resetRoute() {



        // Очищаем маршрут на карте
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
        routePoints.clear();

        // Удаляем сохраненный маршрут из SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(ROUTE_KEY).apply();

        // Удаляем маркер с карты
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
        }

        // Перемещаем камеру на текущее местоположение (если доступно)
        if (mMap != null && fusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                });
            }
        }
    }


}
