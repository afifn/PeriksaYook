package com.kuycoding.periksayook.ui.patient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Doctor;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailDoctorActivity extends AppCompatActivity  {
    public final static String EXTRA_DOCTOR = "extra_doctor";
    private final static String TAG = "DetailDoctorActivity";
    private FirebaseFirestore fStore;
    private TextView tvName, tvSpec, tvPraktik, tvAlamat, tvStatus, tvJekel;
    private ImageView imgProfile;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_doctor);

        tvName = findViewById(R.id.txt_nama_detail);
        tvJekel = findViewById(R.id.txt_jekel_detail);
        tvSpec = findViewById(R.id.txt_spesialis_detail);
        tvPraktik = findViewById(R.id.txt_pratik_detail);
        tvAlamat = findViewById(R.id.txt_alamat_detail);
        tvStatus = findViewById(R.id.txt_status_detail);
        imgProfile = findViewById(R.id.img_dokter_detail);

        fStore = FirebaseFirestore.getInstance();
        readDetail();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Dokter");
    }

    private void readDetail() {
        DocumentReference documentReference = fStore.collection("dokter").document();
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Doctor doctor = getIntent().getParcelableExtra(EXTRA_DOCTOR);
                assert doctor != null;
                String id = documentSnapshot.getId();
                Log.d(TAG, "onEvent: " + doctor.getName());
                Log.d(TAG, "onID : " + id);
                tvName.setText("dr. " + doctor.getName());
                tvStatus.setText(doctor.getStatus());
                tvAlamat.setText(doctor.getCity());
                tvJekel.setText(doctor.getJekel());
                tvPraktik.setText(doctor.getHospital());
                tvSpec.setText(doctor.getSpec());
                Picasso.get()
                        .load(doctor.getImgUrl())
                        .centerCrop()
                        .error(R.drawable.ic_broken_image_black_24dp)
                        .placeholder(R.drawable.ic_broken_image_black_24dp)
                        .fit()
                        .into(imgProfile);
                Log.d(TAG, "Gambar : " + doctor.getImgUrl());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}