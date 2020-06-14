package com.kuycoding.periksayook.ui.dokter.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.AboutActivity;
import com.kuycoding.periksayook.ui.dokter.settings.DokterSettingActivity;
import com.kuycoding.periksayook.ui.login.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DokterProfileFragment extends Fragment implements View.OnClickListener {
    private TextView tvNama, tvEmail, tvStatus;
    private ImageView imgprofile;
    private FirebaseAuth fAuth;

    public DokterProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dokter_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgprofile = view.findViewById(R.id.imgProfile);
        tvNama = view.findViewById(R.id.txt_nama_profile);
        tvEmail = view.findViewById(R.id.txt_email_profile);
        tvStatus = view.findViewById(R.id.txt_status);

        LinearLayout ll_edit = view.findViewById(R.id.ll_edit_profile);
        LinearLayout ll_about = view.findViewById(R.id.ll_about_app);
       // LinearLayout ll_hapus = view.findViewById(R.id.ll_delete);
        LinearLayout ll_exit = view.findViewById(R.id.ll_out);

        fAuth = FirebaseAuth.getInstance();

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        fStore.collection("dokter").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        tvNama.setText("dr. "+ Objects.requireNonNull(task.getResult()).getString("name"));
                        tvEmail.setText(task.getResult().getString("email"));
                        tvStatus.setText(task.getResult().getString("status"));
                        String img = task.getResult().getString("imgUrl");
                        Picasso.get()
                                .load(img)
                                .error(R.drawable.ic_broken_image_black_24dp)
                                .placeholder(R.drawable.ic_broken_image_black_24dp)
                                .into(imgprofile);
                    }
                });

        DocumentReference dbuser = fStore.collection("users").document(user.getUid());
        dbuser.addSnapshotListener((documentSnapshot, e) -> {
            assert documentSnapshot != null;

        });

        ll_edit.setOnClickListener(this);
        ll_about.setOnClickListener(this);
//        ll_hapus.setOnClickListener(this);
        ll_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_edit_profile:
                startActivity(new Intent(getActivity(), DokterSettingActivity.class));
                break;
            case R.id.ll_about_app:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
           /* case R.id.ll_delete:
                new AlertDialog.Builder(getContext())
                        .setTitle("Hapus Akun")
                        .setMessage("Apakah anda yakin untuk menghapus akun ini?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hapusUser();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;*/
            case R.id.ll_out:
                if (fAuth.getCurrentUser() != null) {
                    fAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Objects.requireNonNull(getActivity()).finish();
                    break;
                }

        }
    }

    private void hapusUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        FirebaseFirestore.getInstance().collection("dokter").document(user.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "onSuccess: remove db user");
                    }
                });

        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Sukses hapus akun", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "onComplete: remove auth");
                    fAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    Toast.makeText(getContext(), "Gagal hapus akun", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
