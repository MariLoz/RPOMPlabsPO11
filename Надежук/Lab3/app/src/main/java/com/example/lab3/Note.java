package com.example.lab3;

import androidx.annotation.NonNull;

public class Note {
    private final long id;
    private String description;

    Note(long id, String description){
        this.id = id;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return this.id + " : " + this.description;
    }
}
