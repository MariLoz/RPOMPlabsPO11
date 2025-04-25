package com.example.lab4;

import android.net.Uri;

import java.net.URI;
import java.security.PrivateKey;

public class PDFLog {

    public PDFLog(String date, String name, Uri uri) {
        this.date = date;
        this.name = name;
        this.uri = uri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    private String date;
    private String name;
    private Uri uri;
}
