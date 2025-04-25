package com.example.listdisplay;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Item {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("localized_name")
    private String localizedName;

    @SerializedName("primary_attr")
    private String primaryAttr;

    @SerializedName("attack_type")
    private String attackType;

    @SerializedName("roles")
    private List<String> roles;

    @SerializedName("img")
    private String img;

    @SerializedName("icon")
    private String icon;

    @SerializedName("base_health")
    private int baseHealth;

    @SerializedName("base_mana")
    private int baseMana;

    @SerializedName("base_armor")
    private int baseArmor;

    @SerializedName("base_attack_min")
    private int baseAttackMin;

    @SerializedName("base_attack_max")
    private int baseAttackMax;

    @SerializedName("move_speed")
    private int moveSpeed;

    // Геттеры для всех полей
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public String getPrimaryAttr() {
        return primaryAttr;
    }

    public String getAttackType() {
        return attackType;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getImg() {
        String heroName = name.replace("npc_dota_hero_", "");
        return "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/" + heroName + ".png";
    }

    public String getIcon() {
        String heroName = name.replace("npc_dota_hero_", "");
        return "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/heroes/icons/" + heroName + ".png";
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public int getBaseArmor() {
        return baseArmor;
    }

    public int getBaseAttackMin() {
        return baseAttackMin;
    }

    public int getBaseAttackMax() {
        return baseAttackMax;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }
}