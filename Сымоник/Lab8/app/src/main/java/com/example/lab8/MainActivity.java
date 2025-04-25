package com.example.lab8;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.SphericalUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline currentRoute;
    private final List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btnLoadGpx).setOnClickListener(v -> openFilePicker());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearMap());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.0, 23.0), 12));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/gpx+xml");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            loadGpxFile(uri);
        }
    }

    private void loadGpxFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            List<LatLng> points = parseGpx(inputStream);
            drawRoute(points);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки GPX файла", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private List<LatLng> parseGpx(InputStream inputStream) throws XmlPullParserException, IOException {
        List<LatLng> points = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "trkpt".equals(parser.getName())) {
                double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                points.add(new LatLng(lat, lon));
            }
            eventType = parser.next();
        }
        return points;
    }

    private void drawRoute(List<LatLng> points) {
        clearMap();

        if (!points.isEmpty()) {
            // Добавляем маркеры для первой и последней точки
            addMarker(points.get(0), "Старт", BitmapDescriptorFactory.HUE_GREEN);
            addMarker(points.get(points.size()-1), "Финиш", BitmapDescriptorFactory.HUE_RED);

            // Рисуем линию маршрута
            currentRoute = mMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .color(Color.BLUE)
                    .width(10));

            // Центрируем карту на маршруте
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : points) builder.include(point);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

            // Показываем статистику
            showRouteStats(points);
        }
    }

    private void addMarker(LatLng position, String title, float color) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
        markers.add(marker);
    }

    private void clearMap() {
        if (currentRoute != null) currentRoute.remove();
        for (Marker marker : markers) marker.remove();
        markers.clear();
    }

    private void showRouteStats(List<LatLng> points) {
        double distance = SphericalUtil.computeLength(points);
        String stats = String.format("Точек: %d\nРасстояние: %.1f км",
                points.size(), distance/1000);
        Toast.makeText(this, stats, Toast.LENGTH_LONG).show();
    }
}