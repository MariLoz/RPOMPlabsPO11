package com.example.minishop.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Good implements Parcelable {
    private int id;
    private String name;
    private double cost; // Добавлено поле стоимости
    private boolean check;

    public Good(int id, String name, double cost, boolean check) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.check = check;
    }

    protected Good(Parcel in) {
        id = in.readInt();
        name = in.readString();
        cost = in.readDouble();
        check = in.readByte() != 0;
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCost() { return cost; } // Геттер для стоимости
    public void setCost(double cost) { this.cost = cost; } // Сеттер для стоимости
    public boolean isCheck() { return check; }
    public void setCheck(boolean check) { this.check = check; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeDouble(cost);
        parcel.writeByte((byte) (check ? 1 : 0));
    }
}