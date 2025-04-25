package com.example.laba2;

import android.os.Parcel;
import android.os.Parcelable;

public class Good implements Parcelable, Comparable<Good> {
    private int id;
    private String name;
    private double price;
    private int goodImg;
    private boolean check;
    public Good(int id, String name, double price, int goodImg) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.goodImg = goodImg;
        this.check = false;
    }
    public Good(int id, String name, double price, int goodImg, boolean check) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.goodImg = goodImg;
        this.check = check;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public int getGoodImg() {
        return goodImg;
    }

    public void setGoodImg(int goodImg){
        this.goodImg = goodImg;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeInt(goodImg);
    }

    public static final Parcelable.Creator<Good> CREATOR = new Parcelable.Creator<Good>() {
        public Good createFromParcel(Parcel in){
            return new Good(in);
        }

        public Good[] newArray(int size){
            return new Good[size];
        }
    };

    private Good(Parcel parcel) {
        id = parcel.readInt();
        name = parcel.readString();
        price = parcel.readDouble();
        goodImg = parcel.readInt();
    }

    @Override
    public int compareTo(Good other) {
        return Integer.compare(this.id, other.id);
    }
}