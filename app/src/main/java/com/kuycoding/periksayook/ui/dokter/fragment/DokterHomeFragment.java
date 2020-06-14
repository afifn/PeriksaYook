package com.kuycoding.periksayook.ui.dokter.fragment;


import android.annotation.SuppressLint;
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
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DokterHomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "tag";
    private TextView tvNama;
    private TextView tvtotalAppointment;
    private TextView tvTotalPerjanjian;
    private ImageView imgProfile;
    private String userId;
    private FirebaseFirestore fStore;

    public DokterHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dokter_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNama = view.findViewById(R.id.txt_nama);
        TextView tvUcapan = view.findViewById(R.id.txt_ucapan);
        imgProfile = view.findViewById(R.id.imgProfile);

        tvtotalAppointment = view.findViewById(R.id.txt_count_appointment);
        CardView btnLihatAppointment = view.findViewById(R.id.btn_lihat_appointment);
        btnLihatAppointment.setOnClickListener(this);

        tvTotalPerjanjian = view.findViewById(R.id.txt_count_perjanjian);
        CardView btnLihatPerjanjian = view.findViewById(R.id.btn_lihat_perjanjian);
        btnLihatPerjanjian.setOnClickListener(this);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        loadCurrentPatientData();

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
        tvUcapan.setText(greeting);
        total();

        FirebaseUser user;
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userID = user.getUid();
        OneSignal.sendTag("user_id", userID);
        OneSignal.setEmail(Objects.requireNonNull(user.getEmail()));
    }

    private void total() {
        fStore.collection("dokter").document(userId).collection("appointment").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int app = Objects.requireNonNull(task.getResult()).size();
                        String appointment = String.valueOf(app);
                        tvtotalAppointment.setText(appointment);
                    } else {
                        Log.d(TAG, "total: 0");
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure " + e.toString()));

        fStore.collection("dokter").document(userId).collection("booking").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int book = Objects.requireNonNull(task.getResult()).size();
                        String booking = String.valueOf(book);
                        tvTotalPerjanjian.setText(booking);
                    } else {
                        Log.d(TAG, "total: 0");
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
    }

    private void loadCurrentPatientData() {
        fStore.collection("dokter").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    tvNama.setText("dr. "+ Objects.requireNonNull(task.getResult()).getString("name"));
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
            case R.id.btn_lihat_appointment:
                Fragment fra1 = new DokterRequestFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.replace(R.id.container_layout, fra1);
                transaction1.addToBackStack(null);
                transaction1.commit();
                break;
            case R.id.btn_lihat_perjanjian:
                Fragment fra2 = new DokterBookingFragment();
                assert getFragmentManager() != null;
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.replace(R.id.container_layout, fra2);
                transaction2.addToBackStack(null);
                transaction2.commit();
            default:
                break;
        }
    }
}
