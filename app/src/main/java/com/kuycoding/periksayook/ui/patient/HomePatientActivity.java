package com.kuycoding.periksayook.ui.patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.patient.fragment.DoctorFragment;
import com.kuycoding.periksayook.ui.patient.fragment.HomeFragment;
import com.kuycoding.periksayook.ui.patient.fragment.ProfileFragment;
import com.kuycoding.periksayook.ui.patient.fragment.RequestFragment;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class HomePatientActivity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final String TAG = "HomePatientActivity";
    private FirebaseAuth fAuth;
    private String userId;
    private Context context;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                    case R.id.navigation_doctor:
                        fragment = new DoctorFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                    case R.id.navigation_appointment:
                        fragment = new RequestFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                        return true;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
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
        setContentView(R.layout.activity_patient_home);

        //bottom
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            navigationView.setSelectedItemId(R.id.navigation_home);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //onesignal
//        OneSignal.startInit(this).setNotificationOpenedHandler((OneSignal.NotificationOpenedHandler) new ExampleNotificationOpenedHandler()).init();
//        String userID = user.getUid();
//        OneSignal.sendTag("user_id", userID);
//        OneSignal.setEmail(user.getEmail());

    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);


            Intent intent = new Intent(HomePatientActivity.this, RequestFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Follow the insturctions in the link below to prevent the launcher Activity from starting.
            // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
        }

    }
}
