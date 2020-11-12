package com.example.warewatch_70.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

import java.io.Serializable;

public class WarehouseModel implements Serializable {
    public final static String COLLECTION = "warehouses";
    public final static String NAME = "name";
    public final static String OWNER = "owner";
    public final static String TYPE = "type";
    public final static String VIDEOPATH = "videoPath";
    public final static String POSITION = "position";

    private String name;
    private DocumentReference user;
    private DocumentReference owner;
    private String videoPath;
    private GeoPoint position;

    public WarehouseModel(){}

    public WarehouseModel(String name) {
        this.name = name;
    }

    public WarehouseModel(String name, String videoPath) {
        this.name = name;
        this.videoPath = videoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentReference getOwner() {
        return owner;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public GeoPoint getPosition() {
        return position;
    }
}
