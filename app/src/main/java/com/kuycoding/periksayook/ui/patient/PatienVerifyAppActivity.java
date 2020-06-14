package com.kuycoding.periksayook.ui.patient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Appointment;

public class PatienVerifyAppActivity extends AppCompatActivity {
    public static final String EXTRA_SUCCESS = "extra_success";
    private FirebaseFirestore firestore;
    private Appointment appointment;
    private TextView tvSuccess, tvNama;
    private Button btnHome;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patien_verify_app);

        firestore = FirebaseFirestore.getInstance();
        tvSuccess = findViewById(R.id.txt_sukses_appointment);
        tvNama = findViewById(R.id.txt_sukses_nama);
        btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomePatientActivity.class));
            finish();
        });
        appointment = getIntent().getParcelableExtra(EXTRA_SUCCESS);
        assert appointment != null;
        firestore.collection("users").document(appointment.getPatientId()).collection("appointment").document(appointment.getAppointmentId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("TAG", "onSuccess: " + appointment.getAppointmentId() + " " + appointment.getPatientId());
                        String doctorname = task.getResult().getString("doctorName");
                        tvSuccess.setText("Permintaan kontrol dengan dokter dr. " + doctorname + " telah terkirim");
                      //  tvNama.setText("sampai ketemu nanti ananda " + appointment.getPatientName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
