package com.example.lab8;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Run implements Serializable {
    private int id;
    private float distance;
    private long duration;
    private String polyline;
    private String timestamp;

    public Run() {
    }

    public Run(float distance, long duration, String polyline) {
        this.distance = distance;
        this.duration = duration;
        this.polyline = polyline;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDistance() {
        return String.format("%.2f km", distance / 1000);
    }

    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getFormattedSpeed() {
        if (distance <= 0 || duration <= 0) return "0.0 km/h";

        // Calculate speed in km/h
        double hours = duration / 3600000.0; // milliseconds to hours
        double kilometers = distance / 1000.0; // meters to kilometers
        double speed = kilometers / hours;

        return String.format(Locale.getDefault(), "%.1f km/h", speed);
    }

    public String getFormattedDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return timestamp;
        }
    }
}