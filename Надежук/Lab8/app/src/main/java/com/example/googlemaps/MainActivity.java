package com.example.googlemaps;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private FloatingActionButton locationButton;
//    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<Intent> routeListLauncher;
    private Polyline currentPolyline = null;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private List<LatLng> routePoints = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String ROUTE_LIST_KEY = "routeList";
    private Button startButton;
    private Button stopButton;
    private boolean isRecording = false;
    private List<Route> routeList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationButton = findViewById(R.id.locationButton);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        loadRouteListFromPreferences();

//        filePickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri uri = result.getData().getData();
//                        if (uri != null) {
//                            loadGeoJsonFromFile(uri);
//                        } else {
//                            Toast.makeText(this, "URI файла отсутствует", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(this, "Выбор файла отменен", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );

        routeListLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String routeJsonString = result.getData().getStringExtra("selectedRoute");
                        String updatedRouteListJson = result.getData().getStringExtra("updatedRouteList");

                        if (routeJsonString != null) {
                            try {
                                JSONObject jsonRoute = new JSONObject(routeJsonString);
                                Route selectedRoute = Route.fromJson(jsonRoute);
                                displayRoute(selectedRoute.getPoints());
                                if (selectedRoute.getPoints() != null && !selectedRoute.getPoints().isEmpty()) {
                                    LatLng firstPoint = selectedRoute.getPoints().get(0);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15));
                                }
                            } catch (JSONException e) {
                                Toast.makeText(this, "Ошибка отображения маршрута", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (updatedRouteListJson != null) {
                            try {
                                routeList.clear();
                                JSONArray jsonArray = new JSONArray(updatedRouteListJson);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonRoute = jsonArray.getJSONObject(i);
                                    Route route = Route.fromJson(jsonRoute);
                                    routeList.add(route);
                                }
                                saveRouteListToPreferences();
                                Toast.makeText(this, "Список маршрутов обновлен", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(this, "Ошибка обновления списка маршрутов", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Выбор файла отменен", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (isRecording) {
                    for (Location location : locationResult.getLocations()) {
                        LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
                        updateRoute(newPoint);
                    }
                }
            }
        };

        startButton.setOnClickListener(v -> startRecording());
        stopButton.setOnClickListener(v -> stopRecordingAndSave());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.open_file) {
//            openFilePicker();
//        } else if (id == R.id.clear_map) {
        if (id == R.id.clear_map) {
            clearMapRoute();
        } else if (id == R.id.saved_routes) {
            openRouteListActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openRouteListActivity() {
        Intent intent = new Intent(this, RouteListActivity.class);
        JSONArray jsonArray = new JSONArray();
        for (Route route : routeList) {
            try {
                jsonArray.put(route.toJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(ROUTE_LIST_KEY, jsonArray.toString());
        routeListLauncher.launch(intent);
    }

//    private void openFilePicker() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        filePickerLauncher.launch(intent);
//    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Разрешение на местоположение отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
            startLocationUpdates();
            getDeviceLocation();
        } else {
            requestLocationPermission();
        }

        locationButton.setOnClickListener(v -> goToCurrentLocation());

        googleMap.setBuildingsEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        String message = "Latitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        googleMap.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                            } else {
                                Toast.makeText(this, "Не удалось получить местоположение.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void goToCurrentLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16), 1000, null);
                            } else {
                                Toast.makeText(this, "Не удалось получить местоположение.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

//    private void loadGeoJsonFromFile(Uri uri) {
//        clearMapRoute();
//        StringBuilder stringBuilder = new StringBuilder();
//        try (InputStream inputStream = getContentResolver().openInputStream(uri);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//            parseGeoJson(stringBuilder.toString());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    private void parseGeoJson(String geoJsonString) {
//        try {
//            JSONObject geoJson = new JSONObject(geoJsonString);
//            String type = geoJson.getString("type");
//
//            if (type.equals("FeatureCollection")) {
//                JSONArray features = geoJson.getJSONArray("features");
//                for (int i = 0; i < features.length(); i++) {
//                    JSONObject feature = features.getJSONObject(i);
//                    JSONObject geometry = feature.getJSONObject("geometry");
//                    String geometryType = geometry.getString("type");
//
//                    if (geometryType.equals("LineString")) {
//                        JSONArray coordinates = geometry.getJSONArray("coordinates");
//                        List<LatLng> points = new ArrayList<>();
//                        for (int j = 0; j < coordinates.length(); j++) {
//                            JSONArray coordinate = coordinates.getJSONArray(j);
//                            double longitude = coordinate.getDouble(0);
//                            double latitude = coordinate.getDouble(1);
//                            points.add(new LatLng(latitude, longitude));
//                        }
//
//                        saveRouteFromFile(points);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Error parsing GeoJSON", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void saveRouteFromFile(List<LatLng> points) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Название маршрута");
//        final EditText input = new EditText(this);
//        builder.setView(input);
//        builder.setPositiveButton("Сохранить", (dialog, which) -> {
//            String routeName = input.getText().toString();
//            if (routeName.isEmpty()) {
//                routeName = "Route" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            }
//            Route newRoute = new Route(routeName, points);
//            routeList.add(newRoute);
//            saveRouteListToPreferences();
//        });
//        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
//        builder.show();
//    }

    private void displayRoute(List<LatLng> points) {
        PolylineOptions polylineOptions = new PolylineOptions().addAll(points).color(Color.RED).width(20f);
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = googleMap.addPolyline(polylineOptions);
    }

    private void updateRoute(LatLng newPoint) {
        routePoints.add(newPoint);
        PolylineOptions polylineOptions = new PolylineOptions().addAll(routePoints).color(Color.RED).width(3);
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = googleMap.addPolyline(polylineOptions);
    }

    private void clearMapRoute() {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
        routePoints.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (isRecording) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void startRecording() {
        isRecording = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        routePoints.clear();
        clearMapRoute();
        startLocationUpdates();
        goToCurrentLocation();
        Toast.makeText(this, "Запись маршрута началась", Toast.LENGTH_SHORT).show();
    }

    private void stopRecordingAndSave() {
        isRecording = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        stopLocationUpdates();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Название маршрута");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String routeName = input.getText().toString();
            if (routeName.isEmpty()) {
                routeName = "Route" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            }

            Route newRoute = new Route(routeName, new ArrayList<>(routePoints));
            routeList.add(newRoute);
            saveRouteListToPreferences();
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveRouteListToPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        for (Route route : routeList) {
            try {
                JSONObject jsonRoute = route.toJson();
                jsonArray.put(jsonRoute);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString(ROUTE_LIST_KEY, jsonArray.toString());
        editor.apply();
    }

    private void loadRouteListFromPreferences() {
        String routeListString = sharedPreferences.getString(ROUTE_LIST_KEY, null);
        if (routeListString != null) {
            try {
                JSONArray jsonArray = new JSONArray(routeListString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonRoute = jsonArray.getJSONObject(i);
                    Route route = Route.fromJson(jsonRoute);
                    routeList.add(route);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}