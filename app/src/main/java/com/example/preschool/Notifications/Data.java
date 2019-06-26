package com.example.preschool.Notifications;

public class Data {
    private String user;
    private String idClass;
    private int icon;
    private String body;
    private String title;
    private String sented;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIdClass() {
        return idClass;
    }

    public void setIdClass(String idClass) {
        this.idClass = idClass;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public Data(String user, String idClass, int icon, String body, String title, String sented) {
        this.user = user;
        this.idClass = idClass;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
    }

    public Data() {
    }
}
