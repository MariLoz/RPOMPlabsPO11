package com.example.lab8;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "RoutePrefs";
    private static final String ROUTE_KEY = "saved_route";

    // Компоненты карты
    private MapView mapView;
    private IMapController mapController;
    private Polyline polyline;
    private CompassOverlay compassOverlay;
    private ScaleBarOverlay scaleBarOverlay;

    // Геолокация
    private FusedLocationProviderClient fusedLocationClient;
    private List<GeoPoint> geoPoints = new ArrayList<>();

    // UI элементы
    private Button buttonShowRoute, buttonClearData;

    // Флаги состояния
    private boolean isUserInteractingWithMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Конфигурация osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        initMapView();
        initOverlays();
        initButtons();
        initLocationTracking();
    }

    private void initMapView() {
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Настройка жестов масштабирования
        mapView.setMultiTouchControls(true);
        mapView.getZoomController().setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);

        mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapView.setMinZoomLevel(5.0);
        mapView.setMaxZoomLevel(19.0);

        // Обработчик касаний для определения ручного перемещения
        mapView.setOnTouchListener(new View.OnTouchListener() {
            private final float SCROLL_THRESHOLD = 10f;
            private float startX, startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getX() - startX) > SCROLL_THRESHOLD ||
                                Math.abs(event.getY() - startY) > SCROLL_THRESHOLD) {
                            isUserInteractingWithMap = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        new Handler().postDelayed(() -> {
                            isUserInteractingWithMap = false;
                        }, 5000);
                        break;
                }
                return false;
            }
        });
    }

    private void initOverlays() {
        // Компас
        compassOverlay = new CompassOverlay(
                this,
                new InternalCompassOrientationProvider(this),
                mapView
        );
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Шкала масштаба
        scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(
                getResources().getDisplayMetrics().widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void initButtons() {
        buttonShowRoute = findViewById(R.id.buttonShowRoute);
        buttonClearData = findViewById(R.id.buttonClearData);

        buttonShowRoute.setOnClickListener(v -> {
            restoreRoute();
            Toast.makeText(this, "Маршрут восстановлен", Toast.LENGTH_SHORT).show();
        });

        buttonClearData.setOnClickListener(v -> {
            clearData();
            Toast.makeText(this, "Данные очищены", Toast.LENGTH_SHORT).show();
        });
    }

    private void initLocationTracking() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Для работы приложения нужны разрешения геолокации",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                GeoPoint currentPoint = new GeoPoint(
                        location.getLatitude(),
                        location.getLongitude()
                );

                geoPoints.add(currentPoint);
                updateMap(currentPoint);
            }
            saveRoute();
        }
    };

    private void updateMap(GeoPoint currentPoint) {
        // Центрирование только если пользователь не взаимодействует с картой
        if (!isUserInteractingWithMap) {
            mapController.setCenter(currentPoint);
        }

        // Обновление линии маршрута
        if (polyline == null) {
            polyline = new Polyline();
            polyline.setWidth(15f);
            polyline.setColor(0xAAFF0000);
            mapView.getOverlays().add(polyline);
        }
        polyline.setPoints(geoPoints);

        // Обновление маркера текущей позиции
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);
        Marker marker = new Marker(mapView);
        marker.setPosition(currentPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Текущее местоположение");
        mapView.getOverlays().add(marker);

        mapView.invalidate();
    }

    private void saveRoute() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        StringBuilder sb = new StringBuilder();
        for (GeoPoint point : geoPoints) {
            sb.append(point.getLatitude())
                    .append(",")
                    .append(point.getLongitude())
                    .append(";");
        }
        editor.putString(ROUTE_KEY, sb.toString());
        editor.apply();
    }

    private void restoreRoute() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedRoute = preferences.getString(ROUTE_KEY, "");

        if (!savedRoute.isEmpty()) {
            geoPoints.clear();
            String[] points = savedRoute.split(";");
            for (String point : points) {
                String[] latLng = point.split(",");
                if (latLng.length == 2) {
                    try {
                        double lat = Double.parseDouble(latLng[0]);
                        double lon = Double.parseDouble(latLng[1]);
                        geoPoints.add(new GeoPoint(lat, lon));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!geoPoints.isEmpty()) {
                updateMap(geoPoints.get(geoPoints.size() - 1));
            }
        }
    }

    private void clearData() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        geoPoints.clear();
        mapView.getOverlays().clear();
        initOverlays(); // Восстанавливаем компас и шкалу
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        compassOverlay.enableCompass();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        compassOverlay.disableCompass();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        compassOverlay.disableCompass();    }
}