package com.example.warewatch_70.extras;

public class GeoPoint extends Object implements Comparable<GeoPoint> {

    private double latitude;
    private double longitude;

    public GeoPoint(){}

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public int compareTo(GeoPoint o) {
        return 0;
    }
}
