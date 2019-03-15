package com.devstudiosng.yg.smooop.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class EppLocation extends RealmObject {
    @PrimaryKey
    @Required
    private String mId;

    @Required
    private String mPlace;

    private double mLattitude;

    private double mLongitude;

    private Date mCreatedAt;

    private Date mUpdatedAt;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public double getLattitude() {
        return mLattitude;
    }

    public void setLattitude(double lattitude) {
        mLattitude = lattitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt = createdAt;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        mUpdatedAt = updatedAt;
    }
}
