package com.kuycoding.periksayook.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Admin implements Parcelable {
    String nama, email, jekel, password;

    public Admin(String nama, String email, String jekel, String password) {
        this.nama = nama;
        this.email = email;
        this.jekel = jekel;
        this.password = password;
    }

    protected Admin(Parcel in) {
        nama = in.readString();
        email = in.readString();
        jekel = in.readString();
        password = in.readString();
    }

    public static final Creator<Admin> CREATOR = new Creator<Admin>() {
        @Override
        public Admin createFromParcel(Parcel in) {
            return new Admin(in);
        }

        @Override
        public Admin[] newArray(int size) {
            return new Admin[size];
        }
    };

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJekel() {
        return jekel;
    }

    public void setJekel(String jekel) {
        this.jekel = jekel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(email);
        dest.writeString(jekel);
        dest.writeString(password);
    }
}
