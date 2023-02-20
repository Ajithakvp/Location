package com.example.location;

public class LocationList {

    public String Latitude;
    public String Longtitude;
    public String Address;

    public LocationList(String latitude, String longtitude, String address) {
        Latitude = latitude;
        Longtitude = longtitude;
        Address = address;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongtitude() {
        return Longtitude;
    }

    public void setLongtitude(String longtitude) {
        Longtitude = longtitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
