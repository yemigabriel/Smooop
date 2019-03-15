package com.devstudiosng.yg.smooop.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class EppContact extends RealmObject {
    @PrimaryKey
    @Required
    private String mId;

    @Required
    private String mName;

    private String mPhone;

    private String mEmail;

    private Date mCreatedAt;

    private Date mUpdatedAt;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
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
