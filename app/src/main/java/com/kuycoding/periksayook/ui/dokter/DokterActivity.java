package com.kuycoding.periksayook.ui.dokter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.dokter.fragment.DokterBookingFragment;
import com.kuycoding.periksayook.ui.dokter.fragment.DokterHomeFragment;
import com.kuycoding.periksayook.ui.dokter.fragment.DokterProfileFragment;
import com.kuycoding.periksayook.ui.dokter.fragment.DokterRequestFragment;

import java.util.Objects;

public class DokterActivity extends AppCompatActivity {
    private static final String TAG = "DokterActivity";
    private static final String STATUS = "status";

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;

                switch (item.getItemId()) {
                    case R.id.navigation_home_doc:
                        fragment = new DokterHomeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                    case R.id.navigation_appointment_doc:
                        fragment = new DokterRequestFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                    case R.id.navigation_janjian_doc:
                        fragment = new DokterBookingFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                    case R.id.navigation_profile_doc:
                        fragment = new DokterProfileFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        userId = Objects.requireNonNull(user).getUid();

        /*OneSignal.startInit(this).setNotificationOpenedHandler(new ExampleNotificationOpenedHandler()).init();
        String userID = user.getUid();
        OneSignal.sendTag("user_id", userID);
        OneSignal.setEmail(Objects.requireNonNull(user.getEmail()));*/

        BottomNavigationView navView = findViewById(R.id.nav_view_doc);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_home_doc);
        }
    }

}
