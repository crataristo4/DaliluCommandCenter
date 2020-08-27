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
    public AlertItems(int type, String userName, String url, String userPhotoUrl, Object timeStamp, String address) {

        this.type = type;
        this.userName = userName;
        this.url = url;
        this.userPhotoUrl = userPhotoUrl;
        this.timeStamp = timeStamp;
        this.address = address;


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

    public void setId(String id) {
        this.id = id;
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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public long getTimeStamp() {
        return (long) timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
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

    @Bindable
    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(int audioLength) {
        this.audioLength = audioLength;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }
}
