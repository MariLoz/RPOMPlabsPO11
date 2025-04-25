package com.example.lab1;

import java.io.Serializable;
import java.util.Objects;

public class Benchmark implements Serializable {
    public Benchmark(String device, String score, String num_of_benchmarks, int vendor) {
        this.device = device;
        this.score = score;
        this.num_of_benchmarks = num_of_benchmarks;
        this.vendor = vendor;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getNum_of_benchmarks() {
        return num_of_benchmarks;
    }

    public void setNum_of_benchmarks(String num_of_benchmarks) {
        this.num_of_benchmarks = num_of_benchmarks;
    }

    public int getVendor() {
        return vendor;
    }

    public void setVendor(int vendor) {
        this.vendor = vendor;
    }


    private String device;
    private String score;
    private String num_of_benchmarks;
    private int vendor;
}
