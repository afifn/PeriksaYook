package com.kuycoding.periksayook.ui.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Patient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHOOSE_IMAGE = 101;
    private static final int CHOOSE_IMAGE_GALLERY = 102;
    private DatabaseReference databaseReference;
    public static final String TAG = "TAG";
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private AutoCompleteTextView inputJekel;
    private ProgressDialog loadingBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private ImageView imgProfile;
    private String profileImageUrl;
    private Uri uriProfileImage;
    public Uri imgUri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputName = findViewById(R.id.edit_name);
        inputEmail = findViewById(R.id.edit_email);
        inputPassword = findViewById(R.id.edit_password);

        String[] item = new String[]{
                "Laki-laki",
                "Perempuan"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);
        inputJekel = findViewById(R.id.edit_gender);
        inputJekel.setAdapter(adapter);
        inputJekel.setOnTouchListener((v, event) -> {
            inputJekel.showDropDown();
            inputJekel.requestFocus();
            return false;
        });

        imgProfile = findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);

        Button btLoginPage = findViewById(R.id.bt_login);
        btLoginPage.setOnClickListener(this);

        Button btRegister = findViewById(R.id.bt_register);
        btRegister.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_profile:
                showImageChooser();
                break;
            case R.id.bt_login:
                Intent moveLoginPage = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(moveLoginPage);
                break;
            case R.id.bt_register:
                CreateAccount();
                break;
        }
    }

    private void showImageChooser() {
        try {
            PackageManager packageManager = getPackageManager();
            int hasPerm = packageManager.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                CharSequence[] options = {"Take photo", "Choose From Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CHOOSE_IMAGE);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, CHOOSE_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                Toast.makeText(this, "Camera permission error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgProfile.setImageBitmap(bitmap);
                uploadProfileImage();
                imgUri = data.getData();
                imgProfile.setImageURI(imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK){
            try {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Uploading Image...");
                dialog.show();

                uriProfileImage = data.getData();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
                byte[] dataBAOS = bytes.toByteArray();
                Log.e("onActivityResult: ", "Pick From Camera");

                String timestap = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                File destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" +timestap+".jpg");
                FileOutputStream fileOutputStream;
                try {
                    destination.createNewFile();
                    fileOutputStream = new FileOutputStream(destination);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileImageUrl = destination.getAbsolutePath();
                imgProfile.setImageBitmap(bitmap);

                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
                UploadTask uploadTask = profileImageRef.putBytes(dataBAOS);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            profileImageUrl = uri.toString();
                            Log.d(TAG, "onActivityResult: " + profileImageUrl);
                        });
                        dialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progres = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded "+ (int) progres + " %");
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading File...");
            dialog.show();

            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(RegisterActivity.this, "Profile pic upload successful", Toast.LENGTH_SHORT).show();
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            profileImageUrl = uri.toString();
                            Log.d(TAG, "upload gambar : " + profileImageUrl);
                        });
                        dialog.dismiss();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progres = 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded " + (int) progres + " %");
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void CreateAccount() {
        final String name = inputName.getText().toString();
        final String jekel = inputJekel.getText().toString();
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        final String imageUrl = profileImageUrl;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(name)) {
            inputName.setError("Name is required");
        }
        if (TextUtils.isEmpty(jekel)) {
            inputJekel.setError("Gender is required");
        }
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
        }
        if (!email.matches(emailPattern)) {
            inputEmail.setError("Invalid email address");
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Password is required");
        }
        if (password.length() < 6) {
            inputPassword.setError("Password Must be 6 Characters");
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendEmailVerification();
                    Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();

                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> userMap = new HashMap<>();

                    FirebaseUser user = fAuth.getCurrentUser();
                    userMap.put("role", "1");
                    userMap.put("name", name);
                    userMap.put("phone", null);
                    userMap.put("email", email);
                    userMap.put("jekel", jekel);
                    userMap.put("password", password);
                    userMap.put("imgUrl", imageUrl);

                    Patient patient = new Patient(name, email,"", jekel, null, password, imageUrl, "1");
                    databaseReference.child("users").child("patients").child(user.getUid()).setValue(patient);

                    documentReference.set(userMap).addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: user profile is created for " + userID))
                            .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
                    fAuth.signOut();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
