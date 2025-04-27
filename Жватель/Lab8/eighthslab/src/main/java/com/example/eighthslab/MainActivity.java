package com.example.eighthslab;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvOut;
    private LocationManager mlocManager;
    private DatabaseHelper databaseHelper;
    private MapView mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GPS_ENABLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация osmdroid
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        setContentView(R.layout.activity_main);

        tvOut = findViewById(R.id.textView1);
        mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        databaseHelper = new DatabaseHelper(this);

        // Инициализация карты
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(12.0);

        // Проверка включения GPS
        checkGpsStatus();

        // Проверка разрешений
        checkLocationPermissions();
    }

    private void checkGpsStatus() {
        if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            tvOut.setText("GPS is not turned on...");
            Toast.makeText(this, "Please enable GPS to track location", Toast.LENGTH_LONG).show();
            // Запрос включения GPS
            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(gpsIntent, REQUEST_GPS_ENABLE);
        } else {
            tvOut.setText("GPS is turned on...");
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Для MIUI: направляем пользователя в настройки приложения для проверки фонового доступа
            if (isMIUI()) {
                Toast.makeText(this, "Please allow location access in app settings", Toast.LENGTH_LONG).show();
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", getPackageName());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            startLocationService();
            displayLocations();
        }
    }

    // Метод для проверки, является ли устройство MIUI
    private boolean isMIUI() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    private String getSystemProperty(String key) {
        try {
            return (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, key);
        } catch (Exception e) {
            return null;
        }
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
    }

    private void displayLocations() {
        List<LocationData> locations = databaseHelper.getAllLocations();
        Log.d("MainActivity", "Locations in database: " + locations.size());
        Toast.makeText(this, "Locations in database: " + locations.size(), Toast.LENGTH_LONG).show();

        if (locations.size() < 30) {
            Toast.makeText(this, "Not enough location data (< 30 fixes). Current: " + locations.size(), Toast.LENGTH_SHORT).show();
            // Временно отображаем данные, даже если их меньше 30
            if (!locations.isEmpty()) {
                Polyline polyline = new Polyline();
                for (LocationData location : locations) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Marker marker = new Marker(mapView);
                    marker.setPosition(geoPoint);
                    marker.setTitle("Visited at " + location.getTimestamp());
                    mapView.getOverlays().add(marker);
                    polyline.addPoint(geoPoint);
                }
                mapView.getOverlays().add(polyline);
                GeoPoint firstPoint = new GeoPoint(locations.get(0).getLatitude(), locations.get(0).getLongitude());
                mapView.getController().setCenter(firstPoint);
                mapView.invalidate();
            }
            return;
        }

        // Оригинальный код для отображения маршрута
        Polyline polyline = new Polyline();
        for (LocationData location : locations) {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            Marker marker = new Marker(mapView);
            marker.setPosition(geoPoint);
            marker.setTitle("Visited at " + location.getTimestamp());
            mapView.getOverlays().add(marker);
            polyline.addPoint(geoPoint);
        }
        mapView.getOverlays().add(polyline);
        if (!locations.isEmpty()) {
            GeoPoint firstPoint = new GeoPoint(locations.get(0).getLatitude(), locations.get(0).getLongitude());
            mapView.getController().setCenter(firstPoint);
        }
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Проверяем, включен ли GPS после получения разрешений
                checkGpsStatus();
                startLocationService();
                displayLocations();
            } else {
                Toast.makeText(this, "Permission denied. Location updates cannot be started.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS_ENABLE) {
            // Проверяем статус GPS после возвращения из настроек
            checkGpsStatus();
            // Если разрешения уже есть, запускаем сервис
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
                displayLocations();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        checkGpsStatus(); // Проверяем статус GPS при возвращении в активность
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
    }
}
