package com.example.lab2;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Item implements Parcelable {
    public Item(String product_name, int price, int id, int photo, int amount) {
        this.product_name = product_name;
        this.price = price;
        this.id = id;
        this.photo = photo;
        this.amount = amount;
    }

    protected Item(Parcel in) {
        product_name = in.readString();
        price = in.readInt();
        id = in.readInt();
        photo = in.readInt();
        amount = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void increment()
    {
        amount++;
    }

    public void decrement()
    {
        if(amount != 0)
            amount--;
    }

    private String product_name;
    private int price;
    private int id;
    private int photo;
    private int amount;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(product_name);
        dest.writeInt(price);
        dest.writeInt(id);
        dest.writeInt(photo);
        dest.writeInt(amount);
    }
}
