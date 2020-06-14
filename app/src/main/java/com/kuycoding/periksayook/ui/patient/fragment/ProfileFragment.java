package com.kuycoding.periksayook.ui.patient.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.AboutActivity;
import com.kuycoding.periksayook.ui.login.LoginActivity;
import com.kuycoding.periksayook.ui.patient.setting.PatientSettingActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "tag";
    private TextView tvNama, tvEmail;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private ImageView imgprofile;
    private FirebaseAuth fAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgprofile = view.findViewById(R.id.imgProfile);
        tvNama = view.findViewById(R.id.txt_nama_profile);
        tvEmail = view.findViewById(R.id.txt_email_profile);
        LinearLayout ll_edit = view.findViewById(R.id.ll_edit_profile);
        LinearLayout ll_about = view.findViewById(R.id.ll_about_app);
        LinearLayout ll_exit = view.findViewById(R.id.ll_out);

        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            tvNama.setText(documentSnapshot.getString("name"));
                            tvEmail.setText(documentSnapshot.getString("email"));
                            String img = documentSnapshot.getString("imgUrl");
                            Picasso.get()
                                    .load(img)
                                    .error(R.drawable.ic_broken_image_black_24dp)
                                    .placeholder(R.drawable.ic_broken_image_black_24dp)
                                    .into(imgprofile);
                        } else {
                            Log.d(TAG, "onSuccess: Empty");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

        ll_edit.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_profile:
                startActivity(new Intent(getActivity(), PatientSettingActivity.class));
                break;
            case R.id.ll_about_app:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.ll_out:
                if (fAuth.getCurrentUser() != null) {
                    fAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Objects.requireNonNull(getActivity()).finishAffinity();
                    break;
                }

        }
    }
}
