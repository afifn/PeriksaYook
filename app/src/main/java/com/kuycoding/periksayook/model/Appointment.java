package com.kuycoding.periksayook.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Appointment implements Parcelable {
    private String doctorName;
    private String patientName;
    private String doctorId;
    private String patientId;
    private String jekel;
    private String tgl;
    private String image;
    private String notes;
    private boolean accepted;
    private boolean rejected;
    private String appointmentId;
    private Date date_created;
    private String status;
    private String note_toPasien;

    public Appointment() {
    }

    public Appointment(String doctorName, String patientName, String doctorId, String patientId, String jekel, String tgl, String image, String notes, boolean accepted, boolean rejected, String appointmentId, Date date_created, String status, String note_toPasien) {
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.jekel = jekel;
        this.tgl = tgl;
        this.image = image;
        this.notes = notes;
        this.accepted = accepted;
        this.rejected = rejected;
        this.appointmentId = appointmentId;
        this.date_created = date_created;
        this.status = status;
        this.note_toPasien = note_toPasien;
    }

    protected Appointment(Parcel in) {
        doctorName = in.readString();
        patientName = in.readString();
        doctorId = in.readString();
        patientId = in.readString();
        jekel = in.readString();
        tgl = in.readString();
        image = in.readString();
        notes = in.readString();
        accepted = in.readByte() != 0;
        rejected = in.readByte() != 0;
        appointmentId = in.readString();
        status = in.readString();
        note_toPasien = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doctorName);
        dest.writeString(patientName);
        dest.writeString(doctorId);
        dest.writeString(patientId);
        dest.writeString(jekel);
        dest.writeString(tgl);
        dest.writeString(image);
        dest.writeString(notes);
        dest.writeByte((byte) (accepted ? 1 : 0));
        dest.writeByte((byte) (rejected ? 1 : 0));
        dest.writeString(appointmentId);
        dest.writeString(status);
        dest.writeString(note_toPasien);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getJekel() {
        return jekel;
    }

    public void setJekel(String jekel) {
        this.jekel = jekel;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote_toPasien() {
        return note_toPasien;
    }

    public void setNote_toPasien(String note_toPasien) {
        this.note_toPasien = note_toPasien;
    }
}
