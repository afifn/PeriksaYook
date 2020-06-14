package com.kuycoding.periksayook.model;

public class Patient {
    private String name;
    private String email;
    private String username;
    private String jekel;
    private String phone;
    private String password;
    private String imgUrl;
    private String type;

    public Patient() {
    }

    public Patient(String name, String email, String username, String jekel, String phone, String password, String imgUrl, String type) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.jekel = jekel;
        this.phone = phone;
        this.password = password;
        this.imgUrl = imgUrl;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJekel() {
        return jekel;
    }

    public void setJekel(String jekel) {
        this.jekel = jekel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
