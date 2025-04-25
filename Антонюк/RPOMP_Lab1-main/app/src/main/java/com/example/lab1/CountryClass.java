package com.example.lab1;

public class CountryClass {
    private String name;        // Название страны (common)
    private String capital;     // Столица
    private String currency;    // Валюта (название и символ)
    private String flagUrl;    // URL флага (картинка)

    // Конструктор
    public CountryClass(String name, String capital, String currency, String flagUrl) {
        this.name = name;
        this.capital = capital;
        this.currency = currency;
        this.flagUrl = flagUrl;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFlagUrl() {
        return flagUrl;
    }
}