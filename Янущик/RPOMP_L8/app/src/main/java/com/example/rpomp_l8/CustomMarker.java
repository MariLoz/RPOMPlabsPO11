package com.example.rpomp_l8;

import org.osmdroid.util.GeoPoint;

public class CustomMarker {
    private GeoPoint position;
    private String title;
    private String description;
    private long id;

    public CustomMarker(GeoPoint position, String title, String description) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position = position;
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.id = System.currentTimeMillis();
    }

    public GeoPoint getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }
}