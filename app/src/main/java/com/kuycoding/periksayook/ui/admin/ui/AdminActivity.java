package com.kuycoding.periksayook.ui.admin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.admin.ui.dokter.ListDoctorActivity;
import com.kuycoding.periksayook.ui.admin.ui.pasient.ListPatientActivity;
import com.kuycoding.periksayook.ui.admin.ui.profile.AdminProfileActivity;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private LinearLayout ll_dokter, ll_pasien, ll_appointment, ll_booking, ll_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ll_dokter = findViewById(R.id.ll_dokter);
        ll_pasien = findViewById(R.id.ll_pasien);
        ll_profile = findViewById(R.id.ll_profile);

        ll_dokter.setOnClickListener(this);
        ll_pasien.setOnClickListener(this);
        ll_profile.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_dokter:
                startActivity(new Intent(this, ListDoctorActivity.class));
                break;
            case R.id.ll_pasien:
                startActivity(new Intent(this, ListPatientActivity.class));
                break;
            case R.id.ll_profile:
                startActivity(new Intent(this, AdminProfileActivity.class));
        }
    }
}
