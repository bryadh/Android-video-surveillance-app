package com.example.warewatch_70.models;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;

public class AlertsModel implements Serializable {
    public final static String COLLECTION = "alerts";
    public final static String OWNER = "owner";
    public final static String USER = "user";
    public final static String WAREHOUSE = "warehouse";
    public final static String TIME = "time";


    private DocumentReference owner;
    private DocumentReference user;
    private DocumentReference warehouse;
    private Date time;

    public AlertsModel(){}

    public AlertsModel(DocumentReference owner, DocumentReference user, DocumentReference warehouse, Date time) {
        this.owner = owner;
        this.user = user;
        this.warehouse = warehouse;
        this.time = time;
    }

    public DocumentReference getOwner() {
        return owner;
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
