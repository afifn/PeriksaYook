package com.kuycoding.periksayook.ui.admin.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.AboutActivity;
import com.kuycoding.periksayook.ui.login.LoginActivity;

import java.util.Objects;

public class AdminProfileActivity extends AppCompatActivity {
    private TextView tv_nama, tv_email;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        tv_email = findViewById(R.id.txt_email_profile);
        tv_nama = findViewById(R.id.txt_nama_profile);

        fAuth = FirebaseAuth.getInstance();

        LinearLayout ll_admin = findViewById(R.id.ll_admin);
        ll_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminProfileActivity.this, AddAdminActivity.class));
            }
        });
        LinearLayout ll_about = findViewById(R.id.ll_about_app);
        ll_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminProfileActivity.this, AboutActivity.class));
            }
        });
        LinearLayout ll_keluar = findViewById(R.id.ll_out);
        ll_keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminProfileActivity.this, LoginActivity.class));
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        readAdmin();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
    }

    private void readAdmin() {
        FirebaseFirestore.getInstance().collection("admin").document(fAuth.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            tv_email.setText(documentSnapshot.getString("email"));
                            tv_nama.setText(documentSnapshot.getString("name"));
                            Log.d("TAG", "onSuccess: " + documentSnapshot.getString("email"));
                        } else {
                            Log.d("TAG", "onSuccess: kosong ");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: ");
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
