package com.example.lab1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Tank implements Parcelable {
    private String name;
    private String nation;
    private int level;
    private String type;
    private int armor;
    private String gun;
    private int speed;
    private int cost;
    private int photoResource;

    public Tank(String name, String nation, int level, String type, int armor, String gun, int speed, int cost, int photoResource) {
        this.name = name;
        this.nation = nation;
        this.level = level;
        this.type = type;
        this.armor = armor;
        this.gun = gun;
        this.speed = speed;
        this.cost = cost;
        this.photoResource = photoResource;
    }

    protected Tank(@NonNull Parcel in) {
        name = in.readString();
        nation = in.readString();
        level = in.readInt();
        type = in.readString();
        armor = in.readInt();
        gun = in.readString();
        speed = in.readInt();
        cost = in.readInt();
        photoResource = in.readInt();
    }

    public static final Creator<Tank> CREATOR = new Creator<Tank>() {
        @Override
        public Tank createFromParcel(Parcel in) {
            return new Tank(in);
        }

        @Override
        public Tank[] newArray(int size) {
            return new Tank[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNation() {
        return nation;
    }

    public int getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public int getArmor() {
        return armor;
    }

    public String getGun() {
        return gun;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCost() {
        return cost;
    }

    public int getPhotoResource() {
        return this.photoResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(nation);
        dest.writeInt(level);
        dest.writeString(type);
        dest.writeInt(armor);
        dest.writeString(gun);
        dest.writeInt(speed);
        dest.writeInt(cost);
        dest.writeInt(photoResource);
    }
}
