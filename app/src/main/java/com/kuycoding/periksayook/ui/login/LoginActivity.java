package com.kuycoding.periksayook.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.admin.ui.AdminActivity;
import com.kuycoding.periksayook.ui.dokter.DokterActivity;
import com.kuycoding.periksayook.ui.patient.HomePatientActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.edit_email);
        inputPassword = findViewById(R.id.edit_password);

        loadingBar = new ProgressDialog(this);

        Button btRegisterPage = findViewById(R.id.bt_register);
        btRegisterPage.setOnClickListener(this);

        Button btLogin = findViewById(R.id.bt_login);
        btLogin.setOnClickListener(this);

        TextView btResetpage = findViewById(R.id.resetPassword);
        btResetpage.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                Intent moveRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(moveRegister);
                finish();
                break;
            case R.id.resetPassword:
                Intent moveReset = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(moveReset);
                finish();
                break;
            case R.id.bt_login:
                LoginAccount();
                break;
                default:
                    break;
        }
    }

    private void LoginAccount() {
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email or username is required");
        }
//        if (!email.matches(emailPattern)) {
//            inputEmail.setError("Invalid email address");
//        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password is required");
        } if (password.length() < 6) {
            inputPassword.setError("Password Must be 6 Characters");
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingBar.setCancelable(false);
            loadingBar.setIndeterminate(false);

            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            if (email.matches(emailPattern)) {
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Login not successufull", Toast.LENGTH_SHORT).show();
                        } else {
                            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            CollectionReference usersRef = fStore.collection("users");
                            CollectionReference userAdmin = fStore.collection("admin");
                            CollectionReference userDokter = fStore.collection("dokter");
                            userAdmin.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser admin = FirebaseAuth.getInstance().getCurrentUser();
                                        //boolean emailVerified = admin.isEmailVerified();
                                        if (admin == null) {
                                            loadingBar.dismiss();
                                            Toast.makeText(LoginActivity.this, "Verify the email id", Toast.LENGTH_SHORT).show();
                                            fAuth.signOut();
                                        } else {
                                            DocumentSnapshot document = task.getResult();
                                            assert document != null;
                                            if (document.exists()) {
                                                String role = document.getString("role");
                                                assert role != null;
                                                if (role.equals("10")) {
                                                    loadingBar.dismiss();
                                                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                            usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                    boolean emailVerify = user.isEmailVerified();
                                        if (user == null) {
                                            loadingBar.dismiss();
                                            Toast.makeText(LoginActivity.this, "Verify the email id", Toast.LENGTH_SHORT).show();
                                            fAuth.signOut();
                                        } else {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            assert documentSnapshot != null;
                                            if (documentSnapshot.exists()) {
                                                String role = documentSnapshot.getString("role");
                                                assert role != null;
                                                if (role.equals("1")) {
                                                    loadingBar.dismiss();
                                                    startActivity(new Intent(LoginActivity.this, HomePatientActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                            userDokter.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                                    boolean emailVerify = user.isEmailVerified();
                                        if (user == null) {
                                            loadingBar.dismiss();
                                            Toast.makeText(LoginActivity.this, "Verify the email id", Toast.LENGTH_SHORT).show();
                                            fAuth.signOut();
                                        } else {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            assert documentSnapshot != null;
                                            if (documentSnapshot.exists()) {
                                                String role = documentSnapshot.getString("role");
                                                assert role != null;
                                                if (role.equals("0")) {
                                                    loadingBar.dismiss();
                                                    startActivity(new Intent(LoginActivity.this, DokterActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                FirebaseUser user = fAuth.getCurrentUser();
            /*    FirebaseFirestore documentReference = FirebaseFirestore.getInstance();
                documentReference.collection("username").document(email);
                documentReference.collection("username").document(email).collection("password").document(password);
                documentReference.collection("users").whereEqualTo("username", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot documentSnapshot = task.getResult();
                        Toast.makeText(LoginActivity.this, "Data ada", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });*/

                /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child("patients");
                reference.orderByChild("username").equalTo(email).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        startActivity(new Intent(LoginActivity.this, HomePatientActivity.class));
                        loadingBar.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loadingBar.dismiss();
                    }
                });*/
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}
