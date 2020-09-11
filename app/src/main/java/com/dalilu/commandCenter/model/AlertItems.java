package com.dalilu.commandCenter.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.firestore.GeoPoint;

public class AlertItems extends BaseObservable {
    public String userName;
    public String url;
    public String audioUrl;
    public String userPhotoUrl;
    public Object timeStamp;
    public double latitude;
    public double longitude;
    public String dateReported;
    public String address;
    public String phoneNumber;
    public String status;
    public String id;
    public int type, audioLength;
    public boolean isSolved ;
    public GeoPoint coordinates;

    public AlertItems() {
    }

    //Image type constructor
    public AlertItems(int type, String userName, String userPhotoUrl, String url, Object timeStamp,
                      String address, String id, String dateReported,boolean isSolved) {

        this.type = type;
        this.userName = userName;
        this.userPhotoUrl = userPhotoUrl;
        this.url = url;
        this.timeStamp = timeStamp;
        this.address = address;
        this.id = id;
        this.dateReported = dateReported;
        this.isSolved = isSolved;

    }

    //Video type constructor
    public AlertItems(int type, String id, String userName, String url, String userPhotoUrl, Object timeStamp, String address, boolean isSolved) {

        this.type = type;
        this.id = id;
        this.userName = userName;
        this.url = url;
        this.userPhotoUrl = userPhotoUrl;
        this.timeStamp = timeStamp;
        this.address = address;
        this.isSolved = isSolved;


    }

    //audio type url
    public AlertItems(int type, String userName, String audioUrl, String userPhotoUrl, Object timeStamp,
                      double latitude, double longitude, int audioLength) {

        this.type = type;
        this.userName = userName;
        this.audioUrl = audioUrl;
        this.userPhotoUrl = userPhotoUrl;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.audioLength = audioLength;

    }

    public String getId() {
        return id;
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUrl() {
        return url;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public long getTimeStamp() {
        return (long) timeStamp;
    }

    @Bindable
    public String getDateReported() {
        return dateReported;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

}
