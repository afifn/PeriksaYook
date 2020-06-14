package com.kuycoding.periksayook.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Doctor implements Parcelable {
    private String name;
    private String email;
    private String password;
    private String spec;
    private String hospital;
    private String city;
    private String imgUrl;
    private String status;
    private String jekel;
    private String uid;
    private String id;
    private String type;

    public Doctor(String name, String email, String password, String spec, String hospital, String city, String imgUrl, String status, String jekel, String uid, String id, String type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.spec = spec;
        this.hospital = hospital;
        this.city = city;
        this.imgUrl = imgUrl;
        this.status = status;
        this.jekel = jekel;
        this.uid = uid;
        this.id = id;
        this.type = type;
    }

    public Doctor(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Doctor() {

    }

    protected Doctor(Parcel in) {
        name = in.readString();
        email = in.readString();
        password = in.readString();
        spec = in.readString();
        hospital = in.readString();
        city = in.readString();
        imgUrl = in.readString();
        status = in.readString();
        jekel = in.readString();
        uid = in.readString();
        id = in.readString();
        type = in.readString();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJekel() {
        return jekel;
    }

    public void setJekel(String jekel) {
        this.jekel = jekel;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(spec);
        dest.writeString(hospital);
        dest.writeString(city);
        dest.writeString(imgUrl);
        dest.writeString(status);
        dest.writeString(jekel);
        dest.writeString(uid);
        dest.writeString(id);
        dest.writeString(type);
    }
}
