package com.example.rpomp_l8;

import org.osmdroid.util.GeoPoint;

public class LocationData {
    private final double latitude;
    private final double longitude;
    private final long timestamp;

    public LocationData(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }

    public long getTimestamp() {
        return timestamp;
    }
}