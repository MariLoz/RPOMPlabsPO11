package com.example.eighthslab;

public class LocationData {
    private int id;
    private double latitude;
    private double longitude;
    private String timestamp;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}