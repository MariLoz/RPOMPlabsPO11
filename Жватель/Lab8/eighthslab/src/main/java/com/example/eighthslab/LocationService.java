package com.example.eighthslab;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "LocationService started");
        databaseHelper = new DatabaseHelper(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Проверка, включен ли GPS
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG, "GPS is disabled");
            stopSelf();
            return;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Log.d(TAG, "Location updated: Lat=" + location.getLatitude() + ", Lon=" + location.getLongitude() + ", Time=" + timestamp);
                try {
                    databaseHelper.addLocation(location.getLatitude(), location.getLongitude(), timestamp);
                    Log.d(TAG, "Location saved to database");
                } catch (Exception e) {
                    Log.e(TAG, "Error saving location to database: " + e.getMessage());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {
                Log.d(TAG, "Provider status changed: " + provider + ", status=" + status);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Log.d(TAG, "Provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d(TAG, "Provider disabled: " + provider);
                stopSelf();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting location updates");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, locationListener);
        } else {
            Log.e(TAG, "Location permissions not granted");
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LocationService stopped");
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}