package com.example.warewatch_70.models;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;

public class HistoryModel implements Serializable {
    public static final String COLLECTION = "history";
    public static final String USER = "user";
    public static final String WAREHOUSE = "warehouse";
    public static final String TIME = "time";

    private DocumentReference user;
    private DocumentReference warehouse;
    private Date time;

    public HistoryModel(){}

    public HistoryModel(DocumentReference user, DocumentReference warehouse, Date time) {
        this.user = user;
        this.warehouse = warehouse;
        this.time = time;
    }

    public DocumentReference getUser() {
        return user;
    }

    public DocumentReference getWarehouse() {
        return warehouse;
    }

    public Date getTime() {
        return time;
    }
}
