package com.example.android_l2;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    int id;
    String name;
    int price;
    int imageResId;

    public Product(int id, String name, int price, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readInt();
        imageResId = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeInt(imageResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}