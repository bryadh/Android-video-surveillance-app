package com.example.warewatch_70.models;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {
    public static final String COLLECTION = "users";
    public static final String USERNAME = "username";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String ADMIN = "admin";
    public static final String FIRSTLOG = "firstLog";
    public static final String LASTUPDATE = "lastUpdate";
    public static final String WHOWNER = "whOwnerRef";

    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String phone;
    private boolean admin;
    private boolean firstLog;
    private Date lastUpdate;
    private String whOwnerRef;

    public UserModel(String username, String firstname, String lastname, String password, String phone, boolean admin, boolean firstLog, Date lastUpdate, String whOwnerRef) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.phone = phone;
        this.admin = admin;
        this.firstLog = firstLog;
        this.lastUpdate = lastUpdate;
        this.whOwnerRef = whOwnerRef;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isFirstLog() {
        return firstLog;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getWhOwnerRef() {
        return whOwnerRef;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstLog(boolean firstLog) {
        this.firstLog = firstLog;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
