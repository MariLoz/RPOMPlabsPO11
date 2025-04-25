package com.example.myapplication;

import java.io.Serializable;

public class Constellation implements Serializable {
    private String name;
    public Constellation(String name) {
        this.name = name;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
