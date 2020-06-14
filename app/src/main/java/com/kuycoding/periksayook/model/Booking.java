package com.kuycoding.periksayook.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Booking implements Parcelable {
    private String patientName, doctorName, idPatient, idDoctor, notes, date, clock, idBooking;

    public Booking() {
    }

    public Booking(String patientName, String doctorName, String idPatient, String idDoctor, String notes, String date, String clock, String idBooking) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.idPatient = idPatient;
        this.idDoctor = idDoctor;
        this.notes = notes;
        this.date = date;
        this.clock = clock;
        this.idBooking = idBooking;
    }

    protected Booking(Parcel in) {
        patientName = in.readString();
        doctorName = in.readString();
        idPatient = in.readString();
        idDoctor = in.readString();
        notes = in.readString();
        date = in.readString();
        clock = in.readString();
        idBooking = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(String idPatient) {
        this.idPatient = idPatient;
    }

    public String getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(String idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(String idBooking) {
        this.idBooking = idBooking;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(patientName);
        dest.writeString(doctorName);
        dest.writeString(idPatient);
        dest.writeString(idDoctor);
        dest.writeString(notes);
        dest.writeString(date);
        dest.writeString(clock);
        dest.writeString(idBooking);
    }
}
