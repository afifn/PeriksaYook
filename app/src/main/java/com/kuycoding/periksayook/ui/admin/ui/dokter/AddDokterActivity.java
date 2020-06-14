package com.kuycoding.periksayook.ui.admin.ui.dokter;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.kuycoding.periksayook.model.Doctor;

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

public class AddDokterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHOOSE_IMAGE = 101;
    public static final String TAG = "TAG";
    private static final int CHOOSE_IMAGE_GALLERY = 102;
    private DatabaseReference databaseReference;
    private EditText edt_nama;
    private EditText edt_hospital;
    private EditText edt_email;
    private EditText edt_password;
    private EditText edt_city;
    private EditText edt_spesialis;
    private AutoCompleteTextView edit_jekel;
    private ImageView imgProfile;
    private ProgressDialog loadingBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private String profileImageUrl;
    private Uri uriProfileImage;
    public Uri imgUri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dokter);

        loadingBar = new ProgressDialog(this);

        edt_nama = findViewById(R.id.edit_name);
        edt_spesialis = findViewById(R.id.edit_spec);
        edt_hospital = findViewById(R.id.edit_rumahsakit);
        edt_email = findViewById(R.id.edit_email);
        edt_password = findViewById(R.id.edit_password);
        edt_city = findViewById(R.id.edit_city);
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

        imgProfile = findViewById(R.id.img_profile_dokter);
        imgProfile.setOnClickListener(this);
        Button btnAddDokter = findViewById(R.id.btn_add_dokter);
        btnAddDokter.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tambah Dokter");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_profile_dokter:
                showImageChooser();
                break;
            case R.id.btn_add_dokter:
                CreateDokter();
                break;
        }
    }

    private void CreateDokter() {
        final String nama = edt_nama.getText().toString();
        final String spesialis = edt_spesialis.getText().toString().trim();
        final String hospital = edt_hospital.getText().toString();
        final String city = edt_city.getText().toString();
        final String email = edt_email.getText().toString();
        final String jekel = edit_jekel.getText().toString();
        final String password = edt_password.getText().toString();
        final String status = "Ada";
        final String type = "dokter";
        final String imageUrl = profileImageUrl;
        final String id = databaseReference.push().getKey();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (nama.isEmpty()) {
            edt_nama.setError("Tidak boleh kosong");
        } else if (spesialis.isEmpty()) {
            edt_spesialis.setError("Tidak boleh kosong");
        } else if (hospital.isEmpty()) {
            edt_hospital.setError("Tidak boleh kosong");
        } else if (city.isEmpty()) {
            edt_city.setError("Tidak boleh kosong");
        } else if (email.isEmpty()){
            edt_email.setError("Tidak boleh kosong");
        } else if (!email.matches(emailPattern)) {
            edt_email.setError("Format email salah");
        } else if (password.isEmpty()) {
            edt_password.setError("Tidak boleh kosong");
        } else if (password.length() < 6){
            edt_password.setError("Password kurang dari 6 karakter");
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
                        Toast.makeText(AddDokterActivity.this, "User created", Toast.LENGTH_SHORT).show();
                        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = fStore.collection("dokter").document(userID);
                        String req_id = documentReference.getId();
                        String dokterGetid = req_id;

                        Map<String, Object> userMap = new HashMap<>();
                        FirebaseUser user = fAuth.getCurrentUser();
                        userMap.put("role", "0");
                        userMap.put("id", id);
                        userMap.put("uid", req_id);
                        userMap.put("name", nama);
                        userMap.put("spec",spesialis);
                        userMap.put("city", city);
                        userMap.put("hospital",hospital);
                        userMap.put("jekel", jekel);
                        userMap.put("email", email);
                        userMap.put("password",password);
                        userMap.put("status",status);
                        userMap.put("imgUrl", imageUrl);
                        userMap.put("type",type);

                        Doctor doctor = new Doctor(nama, email, password, spesialis, hospital, city, imageUrl , status, jekel, req_id, id, type);
                        databaseReference.child("users").child("dokter").child(user.getUid()).setValue(doctor);

                        clearText();
                        documentReference.set(userMap).addOnCompleteListener(task1 -> Log.d(TAG,"onSuccess: user profile is created for " + userID)).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
                        loadingBar.dismiss();
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(AddDokterActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
        if (requestCode == CHOOSE_IMAGE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddDokterActivity.this, "Profile pic upload successful", Toast.LENGTH_SHORT).show();
                           // profileImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl = uri.toString();
                                    Log.d(TAG,"Gambar : " + profileImageUrl);
                                }
                            });
                            dialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progres = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded "+ (int) progres + " %");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddDokterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void sendEmailVerify() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddDokterActivity.this,"Check your Email for verification",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }

    private void clearText() {
        edt_nama.setText("");
        edt_city.setText("");
        edt_spesialis.setText("");
        edt_password.setText("");
        edt_email.setText("");
        edt_hospital.setText("");
        imgProfile.setImageResource(R.drawable.profile);
    }
}
