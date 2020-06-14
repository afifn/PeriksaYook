package com.kuycoding.periksayook.ui.patient.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.handler.NetworkHandler;
import com.kuycoding.periksayook.ui.patient.DisconnectActivity;
import com.kuycoding.periksayook.ui.patient.booking.PatientListBooking;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "tag";
    private TextView tvNama;
    private TextView tvTotalDokter;
    private TextView tvtotalAppointment;
    private TextView tvTotalPerjanjian;
    private FirebaseFirestore fStore;
    private ImageView imgProfile;
    private String userId;
   // private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    //   progressBar = view.findViewById(R.id.progress_bar);
        tvNama = view.findViewById(R.id.txt_nama);
        TextView tvUcapan = view.findViewById(R.id.txt_ucapan);
        imgProfile = view.findViewById(R.id.imgProfile);

        tvTotalDokter = view.findViewById(R.id.txt_count_doctor);
        CardView btnLihatDokter = view.findViewById(R.id.btn_lihat_dokter);
        btnLihatDokter.setOnClickListener(this);

        tvtotalAppointment = view.findViewById(R.id.txt_count_appointment);
        CardView btnLihatAppointment = view.findViewById(R.id.btn_lihat_appointment);
        btnLihatAppointment.setOnClickListener(this);

        tvTotalPerjanjian = view.findViewById(R.id.txt_count_perjanjian);
        CardView btnLihatPerjanjian = view.findViewById(R.id.btn_lihat_perjanjian);
        btnLihatPerjanjian.setOnClickListener(this);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting = null;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Selamat Pagi";
        } else if (timeOfDay >= 12 && timeOfDay <= 16) {
            greeting = "Selamat Siang";
        } else if (timeOfDay >= 16 && timeOfDay <= 20) {
            greeting = "Selamat Sore";
        } else {
            greeting = "Selamat Malam";
        }
        //   tvUcapan.setText(greeting);
        if (getContext() != null) {
            if (NetworkHandler.isConnectedToNetwork(getContext())) {
//                progressBar.setVisibility(View.VISIBLE);
                loadCurrentPatientData();
                total();
            } else {
                startActivity(new Intent(getActivity(), DisconnectActivity.class));
//                progressBar.setVisibility(View.VISIBLE);
            }
        }

        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        OneSignal.sendTag("user_id", userID);
        OneSignal.setEmail(Objects.requireNonNull(user.getEmail()));
    }

    @SuppressLint("SetTextI18n")
    private void total() {
        fStore.collection("dokter").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int doc = Objects.requireNonNull(task.getResult()).size();
                        String docter = String.valueOf(doc);
                        Log.d(TAG, "total: " + docter);
                        tvTotalDokter.setText(docter);
                    } else {
                        Log.d(TAG, "total: 0");
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

        fStore.collection("users").document(userId).collection("appointment").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int appoinmen = task.getResult().size();
                        String appointment = String.valueOf(appoinmen);
                        tvtotalAppointment.setText(appointment);
                        Log.d(TAG, "total: appointment" + appointment);
                    } else {
                        Log.d(TAG, "total: 0");
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

        fStore.collection("users").document(userId).collection("booking").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int book = task.getResult().size();
                        String booking = String.valueOf(book);
                        tvTotalPerjanjian.setText(booking);
                        Log.d(TAG, "total: booking" + booking);
                    } else {
                        Log.d(TAG, "total booking: 0");
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }

    private void loadCurrentPatientData() {
        fStore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
//                    progressBar.setVisibility(View.GONE);
                    tvNama.setText("ananda "+task.getResult().getString("name"));
                    String profileUrl = task.getResult().getString("imgUrl");
                    Picasso.get()
                            .load(profileUrl)
                            .centerCrop()
                            .error(R.drawable.ic_broken_image_black_24dp)
                            .placeholder(R.drawable.ic_broken_image_black_24dp)
                            .fit()
                            .into(imgProfile);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lihat_dokter:
                Fragment fra1 = new DoctorFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_layout, fra1);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.btn_lihat_appointment:
                Fragment fra2 = new RequestFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.replace(R.id.container_layout, fra2);
                transaction1.addToBackStack(null);
                transaction1.commit();
                break;
            case R.id.btn_lihat_perjanjian:
                startActivity(new Intent(getActivity(), PatientListBooking.class));
                break;
                default:
                    break;
        }
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


            Intent intent = new Intent(getActivity(), RequestFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Follow the insturctions in the link below to prevent the launcher Activity from starting.
            // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
        }

    }
}
