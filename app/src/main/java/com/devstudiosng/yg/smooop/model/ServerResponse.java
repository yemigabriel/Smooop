package com.devstudiosng.yg.smooop.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    @SerializedName("status")
    private int status;
    @SerializedName("type")
    private String type;
    @SerializedName("message")
    private String message;

    public ServerResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(ServerResponse.class);
        return json;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, ServerResponse.class);
    }



}
