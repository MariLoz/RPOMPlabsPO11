package com.example.lab8;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TrackingService trackingService;
    private boolean isBound = false;
    private final float[] speedBuffer = new float[3];
    private int bufferIndex = 0;

    private TextView tvSpeed;
    private float currentSpeed = 0; // in m/s
    private static final float MS_TO_KMH = 3.6f; // Conversion factor
    private static final String PREF_MAP_TYPE = "map_type";
    private Button btnMapType;
    private int currentMapType = MAP_TYPE_NORMAL;
    private static final float DEFAULT_TRACKING_ZOOM = 16f;
    private boolean userInteractingWithMap = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private List<LatLng> pathPoints = new ArrayList<>();
    private Polyline pathPolyline;

    private boolean isTracking = false;
    private float totalDistance = 0;
    private Location previousLocation;

    private DatabaseHelper databaseHelper;

    // UI elements
    private Button btnStart, btnStop, btnHistory;
    private Chronometer chronometer;
    private TextView tvDistance;
    private long pauseOffset = 0;

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

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        currentMapType = prefs.getInt(PREF_MAP_TYPE, MAP_TYPE_NORMAL);
        // Initialize database
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnHistory = findViewById(R.id.btnHistory);
        chronometer = findViewById(R.id.chronometer);
        tvDistance = findViewById(R.id.tvDistance);
        tvSpeed = findViewById(R.id.tvSpeed);
        btnMapType = findViewById(R.id.btnMapType);
        btnMapType.setOnClickListener(v -> showMapTypeDialog());

        // Set up buttons
        btnStart.setOnClickListener(v -> startRun());
        btnStop.setOnClickListener(v -> stopRun());
        btnHistory.setOnClickListener(v -> {
            if (!isTracking) {
                Intent intent = new Intent(MainActivity.this, RunHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
            }
        });

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create location request
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



        // Location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null || !isTracking) {
                    Location loc = mMap.getMyLocation();
                    if(loc == null)
                        return;

                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    if (mMap.getCameraPosition().zoom <= DEFAULT_TRACKING_ZOOM && !userInteractingWithMap) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_TRACKING_ZOOM));
                    }
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updatePath(location);
                }
            }
        };
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            TrackingService.LocalBinder binder = (TrackingService.LocalBinder) service;
            trackingService = binder.getService();
            isBound = true;
            updateUIFromService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_mode_map));
        }

        mMap.setMapType(currentMapType);
        setupMapInteractions();
        enableMyLocation();

    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission is required for this app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updatePath(Location location) {
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        pathPoints.add(newLatLng);

        // Update polyline
        if (pathPolyline != null) {
            pathPolyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(pathPoints)
                .color(ContextCompat.getColor(this, R.color.polylineColor))
                .width(20f);
        pathPolyline = mMap.addPolyline(polylineOptions);


        if (mMap.getCameraPosition().zoom <= DEFAULT_TRACKING_ZOOM && !userInteractingWithMap) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, DEFAULT_TRACKING_ZOOM));
        }


        // Calculate distance
        if (previousLocation != null) {
            float distance = previousLocation.distanceTo(location);
            totalDistance += distance;
            tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", totalDistance / 1000));
        }
        previousLocation = location;

        if (location.hasSpeed()) {
            currentSpeed = location.getSpeed();
        } else if (previousLocation != null) {
            // Calculate speed manually if not provided
            long timeDelta = location.getTime() - previousLocation.getTime();
            if (timeDelta > 0) {
                float distance = previousLocation.distanceTo(location);
                currentSpeed = distance / (timeDelta / 1000f); // m/s
            }
        }

        updateSpeedDisplay();
    }

    private void updateSpeedDisplay() {
        // Add to buffer
        speedBuffer[bufferIndex] = currentSpeed * MS_TO_KMH;
        bufferIndex = (bufferIndex + 1) % speedBuffer.length;

        // Calculate average
        float avgSpeed = (speedBuffer[0] + speedBuffer[1] + speedBuffer[2]) / 3;

        tvSpeed.setText(String.format(Locale.getDefault(), "%.1f", avgSpeed));

        // Optional speed-based coloring
//        int colorRes = avgSpeed > 10 ? R.color.colorAccent : R.color.textColorPrimary;
//        tvSpeed.setTextColor(ContextCompat.getColor(this, colorRes));
    }

    private void setupMapInteractions() {
        mMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                userInteractingWithMap = true;
            }
        });

        mMap.setOnCameraIdleListener(() -> {
            userInteractingWithMap = false;
        });
    }

    private void startRun() {
        if (!isTracking) {
            isTracking = true;
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnHistory.setEnabled(false);

            Intent serviceIntent = new Intent(this, TrackingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

            // Reset tracking data
            pathPoints.clear();
            totalDistance = 0;
            previousLocation = null;
            tvDistance.setText("0.00 km");

            // Start timer
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
        }
    }

    private void stopRun() {
        if (isTracking) {
            isTracking = false;
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnHistory.setEnabled(true);

            if (pathPolyline != null) {
                pathPolyline.remove();
                pathPolyline = null;
            }
            pathPoints.clear();

            totalDistance = 0;
            tvDistance.setText("0.00 km");
            tvSpeed.setText("0,0");
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            previousLocation = null;

            if (isBound) {
                unbindService(connection);
                isBound = false;
            }
            stopService(new Intent(this, TrackingService.class));

            // Save the run
            if (trackingService != null && trackingService.getPathPoints().size() > 1) {
                String polyline = encodePolyline(convertToLatLng(trackingService.getPathPoints()));
                Run run = new Run(
                        trackingService.getTotalDistance(),
                        trackingService.getElapsedTime(),
                        polyline
                );
                databaseHelper.addRun(run);
                Toast.makeText(this, "Run saved!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, RunDetailsActivity.class);
                intent.putExtra("run", run);
                startActivity(intent);
            }

        }
    }

    private List<LatLng> convertToLatLng(List<Location> locations) {
        List<LatLng> latLngs = new ArrayList<>();
        for (Location location : locations) {
            latLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        return latLngs;
    }

    private void updateUIFromService() {
        if (isBound && trackingService != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isBound && trackingService != null) {
                        float distance = trackingService.getTotalDistance();
                        tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance / 1000));

                        List<Location> locations = trackingService.getPathPoints();
                        if (!locations.isEmpty()) {
                            updatePath(locations.get(locations.size() - 1));
                        }

                        updateUIFromService(); // Continue updating
                    }
                }
            }, 1000); // Update every second
        }
    }

    private String encodePolyline(List<LatLng> path) {
        long prevLat = 0;
        long prevLng = 0;
        StringBuilder result = new StringBuilder();

        for (LatLng point : path) {
            long lat = Math.round(point.latitude * 1e5);
            long lng = Math.round(point.longitude * 1e5);

            long dLat = lat - prevLat;
            long dLng = lng - prevLng;

            encode(dLat, result);
            encode(dLng, result);

            prevLat = lat;
            prevLng = lng;
        }

        return result.toString();
    }

    private void encode(long value, StringBuilder result) {
        value = value < 0 ? ~(value << 1) : value << 1;
        while (value >= 0x20) {
            result.append(Character.toChars((int) ((0x20 | (value & 0x1f)) + 63)));
            value >>= 5;
        }
        result.append(Character.toChars((int) (value + 63)));
    }

    private void showMapTypeDialog() {
        final String[] mapTypes = {
                getString(R.string.map_type_normal),
                getString(R.string.map_type_satellite),
                getString(R.string.map_type_hybrid),
                getString(R.string.map_type_terrain)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_map_type);
        builder.setItems(mapTypes, (dialog, which) -> {
            switch (which) {
                case 0:
                    currentMapType = MAP_TYPE_NORMAL;
                    break;
                case 1:
                    currentMapType = MAP_TYPE_SATELLITE;
                    break;
                case 2:
                    currentMapType = MAP_TYPE_HYBRID;
                    break;
                case 3:
                    currentMapType = MAP_TYPE_TERRAIN;
                    break;
            }
            if (mMap != null) {
                mMap.setMapType(currentMapType);
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTracking) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE).edit()
                .putInt(PREF_MAP_TYPE, currentMapType)
                .apply();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}