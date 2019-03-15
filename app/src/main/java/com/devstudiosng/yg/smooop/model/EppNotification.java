package com.devstudiosng.yg.smooop.model;

import com.devstudiosng.yg.smooop.helpers.DateDeserializer;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class EppNotification extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    @Required
    private String id;

    @SerializedName("message")
    @Required
    private String message;

    @SerializedName("read")
    private int read;
    private int type;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public EppNotification() {
    }

    public EppNotification(String id, String message, int read, int type, String createdAt, String updatedAt) {
        this.id = id;
        this.message = message;
        this.read = read;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int isRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
