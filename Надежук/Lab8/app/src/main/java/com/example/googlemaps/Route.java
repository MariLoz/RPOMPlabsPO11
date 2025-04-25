package com.example.googlemaps;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private String name;
    private List<LatLng> points;

    public Route(String name, List<LatLng> points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public Route() {}

    public JSONObject toJson() throws JSONException {
        JSONObject jsonRoute = new JSONObject();
        jsonRoute.put("name", this.name);

        JSONArray jsonPoints = new JSONArray();
        for (LatLng point : this.points) {
            JSONObject jsonPoint = new JSONObject();
            jsonPoint.put("latitude", point.latitude);
            jsonPoint.put("longitude", point.longitude);
            jsonPoints.put(jsonPoint);
        }
        jsonRoute.put("points", jsonPoints);
        return jsonRoute;
    }

    @NonNull
    public static Route fromJson(@NonNull JSONObject jsonRoute) throws JSONException {
        Route route = new Route();
        route.setName(jsonRoute.getString("name"));

        JSONArray jsonPoints = jsonRoute.getJSONArray("points");
        List<LatLng> pointsList = new ArrayList<>();
        for (int i = 0; i < jsonPoints.length(); i++) {
            JSONObject jsonPoint = jsonPoints.getJSONObject(i);
            double latitude = jsonPoint.getDouble("latitude");
            double longitude = jsonPoint.getDouble("longitude");
            pointsList.add(new LatLng(latitude, longitude));
        }
        route.setPoints(pointsList);
        return route;
    }
}