package com.kuycoding.periksayook.ui.patient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Booking;

public class PatienSuccesBookingActivity extends AppCompatActivity {
    public static final String EXTRA_BOOKING = "extra_booking";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patien_succes_booking);

        TextView tvSukses = findViewById(R.id.txt_sukses_appointment);
        TextView tvNama = findViewById(R.id.txt_sukses_nama);
        Button btnHome = findViewById(R.id.btn_home);

        Booking booking = getIntent().getParcelableExtra(EXTRA_BOOKING);
        assert booking != null;
        tvSukses.setText("Ditunggu ya kedatangannya dengan dr. "+ booking.getDoctorName() +" di RS USU pada " + booking.getDate() + " jam " + booking.getClock());
        tvNama.setText("Sampai ketemu nanti ananda " + booking.getPatientName());

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(PatienSuccesBookingActivity.this, HomePatientActivity.class));
            finish();
        });
    }
}
