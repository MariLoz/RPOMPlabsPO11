package com.example.kozinlab3;

public class MyNote {
    private int id;
    private String country;

    public MyNote(int id, String country) {
        this.id = id;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }
}