package com.example.rpomp_l8;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String LOCATIONS_KEY = "saved_locations";
    private static final String MARKERS_KEY = "saved_markers";

    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private static final int LOCATION_FASTEST_INTERVAL = 3000;
    private static final float LOCATION_MIN_DISTANCE = 5;

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private List<LocationData> locationDataList = new ArrayList<>();
    private List<CustomMarker> customMarkers = new ArrayList<>();
    private Polyline routePolyline;
    private MyLocationNewOverlay myLocationOverlay;
    private Marker startMarker, endMarker;
    private long customStartTime = 0;
    private long customEndTime = 0;
    private TextView distanceTextView;
    private double totalDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        initViews();
        setupMap();
        checkLocationPermissions();
        loadMarkers();
    }

    private void initViews() {
        mapView = findViewById(R.id.map);
        distanceTextView = findViewById(R.id.distanceTextView);

        Button btnAddMarker = findViewById(R.id.addMarkerButton);
        btnAddMarker.setOnClickListener(v -> showAddMarkerDialog());

        Button btnShowMarkers = findViewById(R.id.btnShowMarkersList);
        btnShowMarkers.setOnClickListener(v -> showMarkersListDialog());
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(18.0);

        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        loadLocations();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
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
        }
    }

    private void startLocationUpdates() {
        try {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
            locationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setSmallestDisplacement(LOCATION_MIN_DISTANCE);

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        for (android.location.Location location : locationResult.getLocations()) {
                            if (location != null) {
                                addNewLocation(location);
                                updateRoute();
                            }
                        }
                    }
                }
            };

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                );
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка отслеживания: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addNewLocation(android.location.Location location) {
        LocationData newLocation = new LocationData(
                location.getLatitude(),
                location.getLongitude(),
                System.currentTimeMillis()
        );
        locationDataList.add(newLocation);
        saveLocations();
    }

    private void updateRoute() {
        mapView.getOverlays().remove(routePolyline);
        if (startMarker != null) mapView.getOverlays().remove(startMarker);
        if (endMarker != null) mapView.getOverlays().remove(endMarker);

        List<GeoPoint> points = new ArrayList<>();
        totalDistance = 0;
        LocationData prevLocation = null;

        for (LocationData location : locationDataList) {
            if ((customStartTime == 0 || location.getTimestamp() >= customStartTime) &&
                    (customEndTime == 0 || location.getTimestamp() <= customEndTime)) {
                points.add(location.getGeoPoint());

                if (prevLocation != null) {
                    totalDistance += location.getGeoPoint().distanceToAsDouble(prevLocation.getGeoPoint());
                }
                prevLocation = location;
            }
        }

        if (points.isEmpty()) {
            distanceTextView.setText("Дистанция: 0 м");
            return;
        }

        if (totalDistance > 1000) {
            distanceTextView.setText(String.format("Дистанция: %.2f км", totalDistance / 1000));
        } else {
            distanceTextView.setText(String.format("Дистанция: %.0f м", totalDistance));
        }

        routePolyline = new Polyline();
        routePolyline.setPoints(points);
        routePolyline.setColor(Color.BLUE);
        routePolyline.setWidth(10f);
        mapView.getOverlays().add(routePolyline);

        startMarker = new Marker(mapView);
        startMarker.setPosition(points.get(0));
        startMarker.setTitle("Начало");
        mapView.getOverlays().add(startMarker);

        endMarker = new Marker(mapView);
        endMarker.setPosition(points.get(points.size() - 1));
        endMarker.setTitle("Конец");
        mapView.getOverlays().add(endMarker);

        mapView.getController().animateTo(points.get(points.size() - 1));
        mapView.invalidate();
    }

    private void showDateTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            new TimePickerDialog(this, (timeView, hour, minute) -> {
                selectedDate.set(Calendar.HOUR_OF_DAY, hour);
                selectedDate.set(Calendar.MINUTE, minute);

                if (isStartTime) {
                    customStartTime = selectedDate.getTimeInMillis();
                    showDateTimePicker(false);
                } else {
                    customEndTime = selectedDate.getTimeInMillis();
                    if (customEndTime > customStartTime) {
                        updateRoute();
                    } else {
                        Toast.makeText(this, "Конечное время должно быть позже начального", Toast.LENGTH_SHORT).show();
                    }
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveLocations() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString(LOCATIONS_KEY, gson.toJson(locationDataList));
        editor.apply();
    }

    private void loadLocations() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(LOCATIONS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<LocationData>>(){}.getType();
            locationDataList = gson.fromJson(json, type);
            updateRoute();
        }
    }

    private void saveMarkers() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        // Фильтруем некорректные маркеры перед сохранением
        List<CustomMarker> validMarkers = new ArrayList<>();
        for (CustomMarker marker : customMarkers) {
            if (marker != null && marker.getPosition() != null) {
                validMarkers.add(marker);
            }
        }

        editor.putString(MARKERS_KEY, gson.toJson(validMarkers));
        editor.apply();
    }

    private void loadMarkers() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(MARKERS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CustomMarker>>(){}.getType();
            List<CustomMarker> loadedMarkers = gson.fromJson(json, type);

            // Фильтруем некорректные маркеры
            customMarkers.clear();
            for (CustomMarker marker : loadedMarkers) {
                if (marker != null && marker.getPosition() != null) {
                    customMarkers.add(marker);
                }
            }

            updateAllMarkers();
        }
    }

    private void showAddMarkerDialog() {
        if (myLocationOverlay.getLastFix() == null) {
            Toast.makeText(this, "Не удалось определить текущее местоположение", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить метку");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_marker, null);
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descInput = dialogView.findViewById(R.id.descInput);

        builder.setView(dialogView);
        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String description = descInput.getText().toString();

            if (!title.isEmpty()) {
                GeoPoint currentPos = new GeoPoint(
                        myLocationOverlay.getLastFix().getLatitude(),
                        myLocationOverlay.getLastFix().getLongitude()
                );
                addCustomMarker(currentPos, title, description);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void addCustomMarker(GeoPoint position, String title, String description) {
        CustomMarker customMarker = new CustomMarker(position, title, description);
        customMarkers.add(customMarker);
        saveMarkers();
        showMarkerOnMap(customMarker);
    }

    private void showMarkerOnMap(CustomMarker customMarker) {
        if (customMarker == null || customMarker.getPosition() == null) {
            return;
        }

        Marker marker = new Marker(mapView);
        marker.setPosition(customMarker.getPosition());
        marker.setTitle(customMarker.getTitle());
        marker.setSnippet(customMarker.getDescription());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setOnMarkerClickListener((m, mapView) -> {
            showMarkerOptionsDialog(customMarker);
            return true;
        });
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void showMarkerOptionsDialog(CustomMarker customMarker) {
        String[] options = {"Редактировать", "Удалить", "Отмена"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(customMarker.getTitle());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Редактировать
                    showEditMarkerDialog(customMarker);
                    break;
                case 1: // Удалить
                    removeMarker(customMarker);
                    break;
            }
        });
        builder.show();
    }

    private void showEditMarkerDialog(CustomMarker customMarker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать метку");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_marker, null);
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descInput = dialogView.findViewById(R.id.descInput);

        titleInput.setText(customMarker.getTitle());
        descInput.setText(customMarker.getDescription());

        builder.setView(dialogView);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            customMarker.setTitle(titleInput.getText().toString());
            customMarker.setDescription(descInput.getText().toString());
            saveMarkers();
            updateAllMarkers();
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void removeMarker(CustomMarker customMarker) {
        customMarkers.remove(customMarker);
        saveMarkers();
        updateAllMarkers();
    }

    private void updateAllMarkers() {
        // Удаляем только пользовательские маркеры (не начальный и конечный)
        List<Marker> markersToRemove = new ArrayList<>();
        for (Object overlay : mapView.getOverlays()) {
            if (overlay instanceof Marker && overlay != startMarker && overlay != endMarker) {
                markersToRemove.add((Marker) overlay);
            }
        }
        mapView.getOverlays().removeAll(markersToRemove);

        // Добавляем все корректные маркеры
        for (CustomMarker customMarker : customMarkers) {
            if (customMarker != null && customMarker.getPosition() != null) {
                showMarkerOnMap(customMarker);
            }
        }

        mapView.invalidate();
    }

    private void showMarkersListDialog() {
        if (customMarkers.isEmpty()) {
            Toast.makeText(this, "Нет сохраненных меток", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Список меток");

        List<String> markerTitles = new ArrayList<>();
        for (CustomMarker marker : customMarkers) {
            markerTitles.add(marker.getTitle());
        }

        builder.setItems(markerTitles.toArray(new String[0]), (dialog, which) -> {
            CustomMarker selectedMarker = customMarkers.get(which);
            mapView.getController().animateTo(selectedMarker.getPosition());
            Toast.makeText(this, selectedMarker.getTitle(), Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Закрыть", null);
        builder.show();
    }

    public void onShowAllClick(View view) {
        customStartTime = 0;
        customEndTime = 0;
        updateRoute();
    }

    public void onShowCustomClick(View view) {
        showDateTimePicker(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        myLocationOverlay.disableMyLocation();
    }
}