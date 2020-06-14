package com.kuycoding.periksayook.ui.patient.setting;

import android.Manifest;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.periksayook.R;
import com.squareup.picasso.Picasso;

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

public class EditUploadActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "tag";
    private ImageView imgProfile;
    private String profileImageUrl;
    private Uri uriProfileImage;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private static final int CHOOSE_IMAGE = 101;
    private static final int CHOOSE_IMAGE_GALLERY = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_upload);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(this);
        Button btnSave = findViewById(R.id.btn_set_save);
        btnSave.setOnClickListener(this);

        fStore.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String imgU = documentSnapshot.getString("imgUrl");
                            Log.d(TAG, "onCreate: " + imgU);
                            Picasso.get()
                                    .load(imgU)
                                    .error(R.drawable.ic_broken_image_black_24dp)
                                    .placeholder(R.drawable.ic_broken_image_black_24dp)
                                    .into(imgProfile);
                        } else {
                            Log.d(TAG, "onSuccess: 0");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Foto Profile");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgProfile:
                showImageChooser();
                break;
            case R.id.btn_set_save:
                saveEdit();
                Toast.makeText(this, "Photo berhasil di ubah", Toast.LENGTH_SHORT).show();
                break;
            default:
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
        if (requestCode == CHOOSE_IMAGE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgProfile.setImageBitmap(bitmap);
                uploadProfileImage();
                Uri imgUri = data.getData();
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
                        Toast.makeText(EditUploadActivity.this, "Profile pic upload successful", Toast.LENGTH_SHORT).show();
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            profileImageUrl = uri.toString();
                        });
                        dialog.dismiss();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progres = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded "+ (int) progres + " %");
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveEdit() {
        final String img = profileImageUrl;

        DocumentReference dedit = fStore.collection("users").document(user.getUid());
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("imgUrl", img);

        dedit.update(userMap).addOnCompleteListener(task -> {
            Log.d("TAG", "saveEdit: ");
        }).addOnFailureListener(e -> {
            Log.d("TAG", "onFailure: " + e.toString());
        });
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
