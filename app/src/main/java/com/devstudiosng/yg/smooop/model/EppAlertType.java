package com.devstudiosng.yg.smooop.model;

public class EppAlertType {
    private int id;
    private String type;
    private int icon;

    public EppAlertType() {
    }

    public EppAlertType(String type, int icon) {
        this.type = type;
        this.icon = icon;
    }

    public EppAlertType(int id, String type, int icon) {
        this.id = id;
        this.type = type;
        this.icon = icon;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
