package com.example.bottles;

public class Bottle {

    private String id, message;
    private double latitude, longitude;
    public Bottle(){
        id = null;
        message = null;
        latitude = 100;
        longitude = 100;
    }
    public Bottle(String id, String message, double latitude, double longitude){
        this.id = id;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId(){
        return id;
    }
    public String getMessage(){
        return message;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }

}
