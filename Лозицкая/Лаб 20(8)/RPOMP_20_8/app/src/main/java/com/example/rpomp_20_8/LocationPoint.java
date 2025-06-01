package com.example.rpomp_20_8;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LocationPoint {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;
    public double longitude;
    public long timestamp;

    public LocationPoint(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
