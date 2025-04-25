package com.example.lab8;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RunDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Run run;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_run_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        run = (Run) getIntent().getSerializableExtra("run");
        if (run == null) {
            finish();
            return;
        }

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set stats
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvDistance = findViewById(R.id.tvDistance);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvPace = findViewById(R.id.tvPace);

        tvDate.setText(run.getFormattedDate());
        tvDistance.setText("Distance: " + run.getFormattedDistance());
        tvDuration.setText("Duration: " + run.getFormattedDuration());
        tvPace.setText(run.getFormattedSpeed());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (isDarkModeEnabled()) {
            applyMapDarkTheme();
        }

        // Decode polyline and draw it
        List<LatLng> path = decodePolyline(run.getPolyline());
        if (!path.isEmpty()) {
            int polylineColor = ContextCompat.getColor(this, isDarkModeEnabled()
                    ? R.color.polyline_color_dark
                    : R.color.polyline_color_light);
            polyline = mMap.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .color(polylineColor)
                    .width(20f));

            // Move camera to show entire route
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : path) {
                builder.include(point);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
        }
    }

    private void applyMapDarkTheme() {
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.night_mode_map
                    )
            );
            if (!success) {
                Log.e("RunDetails", "Style parsing failed");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("RunDetails", "Can't find style. Error: ", e);
        }
    }

    private boolean isDarkModeEnabled() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(lat / 1E5, lng / 1E5);
            poly.add(p);
        }
        return poly;
    }
}