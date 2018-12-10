package com.example.aditya.bitcointracker.Model;

public class RatesRoot {
    public boolean success;
    public int timestamp;
    public String base;
    public String date;
    public Rates rates;

    public RatesRoot(boolean success, int timestamp, String base, String date, Rates rates) {
        this.success = success;
        this.timestamp = timestamp;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public Rates getRates() {
        return rates;
    }
}
