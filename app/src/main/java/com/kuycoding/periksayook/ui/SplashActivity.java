package com.kuycoding.periksayook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.handler.NetworkHandler;
import com.kuycoding.periksayook.ui.admin.ui.AdminActivity;
import com.kuycoding.periksayook.ui.dokter.DokterActivity;
import com.kuycoding.periksayook.ui.login.LoginActivity;
import com.kuycoding.periksayook.ui.patient.DisconnectActivity;
import com.kuycoding.periksayook.ui.patient.HomePatientActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        final ImageView imageView = findViewById(R.id.splash);
        final LinearLayout linearLayout = findViewById(R.id.ll_top_spalsh);

        final Animation zoomAnim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom);
        imageView.startAnimation(zoomAnim);
        linearLayout.startAnimation(zoomAnim);
        zoomAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (fAuth.getCurrentUser() == null) {
                    int waktu_load = 1500;
                    new Handler().postDelayed(() -> {
                        Intent main = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(main);
                        finish();
                    }, waktu_load);
                } else {
                    if (getApplicationContext() != null) {
                        if (NetworkHandler.isConnectedToNetwork(getApplicationContext())) {
                            launcAuth();
                        } else {
                            startActivity(new Intent(SplashActivity.this, DisconnectActivity.class));
                            finish();
                            Toast.makeText(SplashActivity.this, "Periksa koneksi", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void launcAuth() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        CollectionReference usersRef = fStore.collection("users");
        CollectionReference userAdmin = fStore.collection("admin");
        CollectionReference userDokter = fStore.collection("dokter");

        userAdmin.document(uid).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                assert role != null;
                if (role.equals("10")) {
 //                   loadingBar.dismiss();
                    startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                    finish();
                }
            }
        });

        usersRef.document(uid).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                assert role != null;
                if (role.equals("1")) {
   //                 loadingBar.dismiss();
                    startActivity(new Intent(SplashActivity.this, HomePatientActivity.class));
                    finish();
                }
            }
        });
        userDokter.document(uid).get().addOnCompleteListener(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            assert documentSnapshot != null;
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                assert role != null;
                if (role.equals("0")) {
//                        loadingBar.dismiss();
                    startActivity(new Intent(SplashActivity.this, DokterActivity.class));
                    finish();
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }
}
