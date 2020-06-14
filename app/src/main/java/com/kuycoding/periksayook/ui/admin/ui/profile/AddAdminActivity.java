package com.kuycoding.periksayook.ui.admin.ui.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAdminActivity extends AppCompatActivity {
    private static final String TAG = "tag";
    private EditText editNama, editEmail, editpass;
    private AutoCompleteTextView edit_jekel;
    private ProgressDialog loadingBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        loadingBar = new ProgressDialog(this);

        editNama = findViewById(R.id.edit_nama);
        editEmail = findViewById(R.id.edit_email);
        editpass = findViewById(R.id.edit_password);

        String[] item = new String[]{
                "Laki-laki",
                "Perempuan"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);
        edit_jekel = findViewById(R.id.edit_jekel);
        edit_jekel.setAdapter(adapter);
        edit_jekel.setOnTouchListener((v, event) -> {
            edit_jekel.showDropDown();
            edit_jekel.requestFocus();
            return false;
        });

        Button button = findViewById(R.id.btn_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAdmin();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Admin");
    }

    private void createAdmin() {
        final String nama = editNama.getText().toString();
        final String email = editEmail.getText().toString();
        final String password = editpass.getText().toString();
        final String jekel = edit_jekel.getText().toString();
        final String role = "10";

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (nama.isEmpty()) {
            editNama.setError("tidak boleh kosong");
        } else if (email.isEmpty()) {
            editEmail.setError("tidak boleh kosong");
        } else if (!email.matches(emailPattern)) {
            editEmail.setError("format salah");
        } else if (password.isEmpty()) {
            editpass.setError("tidak boleh kosong");
        } else if (jekel.isEmpty()) {
            edit_jekel.setError("tidak boleh kosong");
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendEmailVerify();
                        Toast.makeText(AddAdminActivity.this, "User created", Toast.LENGTH_SHORT).show();
                        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        DocumentReference dbadmin = fStore.collection("admin").document(userID);

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", email);
                        userMap.put("jekel", jekel);
                        userMap.put("name", nama);
                        userMap.put("password", password);
                        userMap.put("role", role);

                        Admin admin = new Admin(nama, email, jekel, password);

                        clearText();
                        loadingBar.dismiss();
                        dbadmin.set(userMap).addOnCompleteListener(task1 -> {
                            Log.d(TAG, "onComplete: ");
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(AddAdminActivity.this, "Error " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void clearText() {
        editEmail.setText("");
        edit_jekel.setText("");
        editpass.setText("");
        editNama.setText("");
    }

    private void sendEmailVerify() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddAdminActivity.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
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
