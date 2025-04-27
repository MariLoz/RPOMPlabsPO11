package com.example.minishop;

import android.os.Parcel;
import android.os.Parcelable;

public class Good implements Parcelable {
    private int id;
    private String name;
    private boolean check;
    private double price; // Добавляем стоимость

    public Good(int id, String name, boolean check, double price) {
        this.id = id;
        this.name = name;
        this.check = check;
        this.price = price;
    }

    protected Good(Parcel in) {
        id = in.readInt();
        name = in.readString();
        check = in.readByte() != 0;
        price = in.readDouble();
    }

    public static final Creator<Good> CREATOR = new Creator<Good>() {
        @Override
        public Good createFromParcel(Parcel in) {
            return new Good(in);
        }

        @Override
        public Good[] newArray(int size) {
            return new Good[size];
        }
    };

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

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByte((byte) (check ? 1 : 0));
        dest.writeDouble(price);
    }
}