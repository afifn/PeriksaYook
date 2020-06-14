package com.kuycoding.periksayook.ui.dokter.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DokterSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private EditText edit_email;
    private EditText edit_nama;
    private EditText edit_dinas;
    private EditText edit_alamat_dinas;
    private EditText edit_spesialis;
    private AutoCompleteTextView edit_jekel, edit_status;
    private ImageView imgProfile;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter_setting);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        edit_nama = findViewById(R.id.edit_set_nama);
        edit_email = findViewById(R.id.edit_set_email);
        edit_email.setEnabled(false);
        edit_dinas  = findViewById(R.id.edit_set_dinas);
        edit_alamat_dinas = findViewById(R.id.edit_set_alamat);
        edit_spesialis = findViewById(R.id.edit_set_spesial);

        imgProfile = findViewById(R.id.img_set_profile);
        imgProfile.setOnClickListener(this);

        String[] item = new String[] {
                "Laki-laki",
                "Perempuan"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);
        edit_jekel = findViewById(R.id.edit_set_jekel);
        edit_jekel.setAdapter(adapter);
        edit_jekel.setOnTouchListener((v, event) -> {
            edit_jekel.showDropDown();
            edit_jekel.requestFocus();
            return false;
        });

        String[] itemStatus = new String[] {
                "Ada",
                "Tidak ada"
        };
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemStatus);
        edit_status = findViewById(R.id.edit_set_status);
        edit_status.setAdapter(adapterStatus);
        edit_status.setOnTouchListener((v, event) -> {
            edit_status.showDropDown();
            edit_status.requestFocus();
            return false;
        });

        setEdit();

        Button btnSave = findViewById(R.id.btn_set_save);
        btnSave.setOnClickListener(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
    }

    private void setEdit() {
        fStore.collection("dokter").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            edit_email.setText(documentSnapshot.getString("email"));
                            edit_nama.setText(documentSnapshot.getString("name"));
                            edit_dinas.setText(documentSnapshot.getString("hospital"));
                            edit_alamat_dinas.setText(documentSnapshot.getString("city"));
                            edit_spesialis.setText(documentSnapshot.getString("spec"));
                            edit_jekel.setText(documentSnapshot.getString("jekel"));
                            edit_status.setText(documentSnapshot.getString("status"));
                            String img = documentSnapshot.getString("imgUrl");
                            Picasso.get()
                                    .load(img)
                                    .error(R.drawable.ic_broken_image_black_24dp)
                                    .placeholder(R.drawable.ic_broken_image_black_24dp)
                                    .into(imgProfile);
                        } else {
                            Log.d("TAG", "onSuccess: list empty");
                        }
                    }
                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_set_profile:
                startActivity(new Intent(this, DokterEditPotoActivity.class));
                break;
            case R.id.btn_set_save:
                saveEdit();
                Toast.makeText(this, "Profile berhasil diubah", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void saveEdit() {
        final String nama = edit_nama.getText().toString().trim();
        final String jekel = edit_jekel.getText().toString().trim();
        final String dina = edit_dinas.getText().toString().trim();
        final String alamat = edit_alamat_dinas.getText().toString().trim();
        final String spesialis = edit_spesialis.getText().toString().trim();
        final String status = edit_status.getText().toString().trim();

        DocumentReference dedit = fStore.collection("dokter").document(user.getUid());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", nama);
        userMap.put("hospital", dina);
        userMap.put("jekel", jekel);
        userMap.put("city", alamat);
        userMap.put("spec", spesialis);
        userMap.put("status", status);

        dedit.update(userMap).addOnCompleteListener(task -> Log.d("TAG", "saveEdit: "))
                .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
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
