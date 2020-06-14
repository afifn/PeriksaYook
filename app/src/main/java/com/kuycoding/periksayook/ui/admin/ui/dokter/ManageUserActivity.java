package com.kuycoding.periksayook.ui.admin.ui.dokter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Doctor;
import com.squareup.picasso.Picasso;

public class ManageUserActivity extends AppCompatActivity {
    public static final String EXTRA_DOKTER = "extra_dokter";
    private TextView tvEmail;
    private TextView tvHospital;
    private TextView tvKelamin;
    private ImageView imgDokter;
    private Button btnDelete;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        fStore = FirebaseFirestore.getInstance();
        Doctor doctor = getIntent().getParcelableExtra(EXTRA_DOKTER);

        TextView tvNama = findViewById(R.id.txt_nama);
        tvEmail = findViewById(R.id.txt_email);
        tvHospital = findViewById(R.id.txt_hospital);
        tvKelamin = findViewById(R.id.txt_kelamin);
        imgDokter = findViewById(R.id.img_profile_dokter);
        btnDelete = findViewById(R.id.btnDeleted);

        assert doctor != null;
        tvNama.setText(doctor.getName());
        tvEmail.setText(doctor.getEmail());
        tvHospital.setText(doctor.getHospital());
        tvKelamin.setText(doctor.getJekel());
        Picasso.get()
                .load(doctor.getImgUrl())
                .into(imgDokter);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("dokter").document(doctor.getUid()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                user.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ManageUserActivity.this, "Sukses hapus user", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ManageUserActivity.this, ListDoctorActivity.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "onFailure: remove auth " + e.toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "onFailure: remove user + auth" + e.toString());
                    }
                });
            }
        });
    }
}
