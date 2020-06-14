package com.kuycoding.periksayook.ui.patient.appointment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Appointment;
import com.kuycoding.periksayook.ui.patient.booking.PatientPerjanjianActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class PatienDetailAppointmentActivity extends AppCompatActivity {

    public static final String EXTRA_APP_PATIENT = "extrac_app_patient";
    private TextView tvDokterName, tvPasienName, tvTgl, tvKelamin, tvNote, tvStatus;
    private FirebaseFirestore fStore;
    private ImageView imgPasien;
    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patien_detail_appointment);

        fStore = FirebaseFirestore.getInstance();
        tvDokterName = findViewById(R.id.txt_detail_app_patien_dokter);
        tvPasienName = findViewById(R.id.txt_detail_app_patien_nama);
        tvTgl        = findViewById(R.id.txt_detail_app_patien_tgl);
        tvKelamin    = findViewById(R.id.txt_detail_app_patien_jekel);
        tvNote       = findViewById(R.id.txt_detail_app_patien_note);
        tvStatus     = findViewById(R.id.txt_detail_app_patien_status);
        imgPasien    = findViewById(R.id.img_app_pasien);
        imgPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatienDetailAppointmentActivity.this, DetailPhotoActivity.class);
                intent.putExtra(DetailPhotoActivity.EXTRA_APP_PATIENT_POTO, appointment);
                startActivity(intent);
            }
        });

        readDetail();
        Button btnKirimPerjanjian = findViewById(R.id.btn_buat_perjanjian);
        btnKirimPerjanjian.setOnClickListener(v -> {
            Intent intent = new Intent(PatienDetailAppointmentActivity.this, PatientPerjanjianActivity.class);
            intent.putExtra(PatientPerjanjianActivity.EXTRA_APP_PATIENT, appointment);
            startActivity(intent);
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Pengecekan");
    }

    @SuppressLint("SetTextI18n")
    private void readDetail() {
        appointment = getIntent().getParcelableExtra(EXTRA_APP_PATIENT);
        assert appointment != null;
        tvDokterName.setText("dr. " +appointment.getDoctorName());
        tvPasienName.setText(appointment.getPatientName());
        tvTgl.setText(appointment.getTgl());
        tvKelamin.setText(appointment.getJekel());
        tvNote.setText(appointment.getNotes());
        tvStatus.setText(appointment.getStatus());
        Picasso.get()
                .load(appointment.getImage())
                .centerCrop()
                .error(R.drawable.ic_broken_image_black_24dp)
                .placeholder(R.drawable.ic_broken_image_black_24dp)
                .fit()
                .into(imgPasien);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
