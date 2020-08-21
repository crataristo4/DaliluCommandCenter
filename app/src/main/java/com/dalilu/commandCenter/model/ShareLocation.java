package com.dalilu.commandCenter.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ShareLocation extends BaseObservable {
    public String location;
    public String photo;
    public String userName;
    public String url;
    public String knownName;
    public String date;
    public Object timeStamp;
    public double latitude, longitude;
    private boolean isSharingLocation;


    public ShareLocation() {
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bindable
    public String getKnownName() {
        return knownName;
    }

    public void setKnownName(String knownName) {
        this.knownName = knownName;
    }

    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Bindable
    public long getTimeStamp() {
        return (long) timeStamp;
    }

    public void setTimeStamp(long time) {
        this.timeStamp = time;
    }


    public boolean isSharingLocation() {
        return isSharingLocation;
    }

    public void setSharingLocation(boolean sharingLocation) {
        isSharingLocation = sharingLocation;
    }
}
