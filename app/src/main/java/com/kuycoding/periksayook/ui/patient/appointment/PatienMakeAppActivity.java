package com.kuycoding.periksayook.ui.patient.appointment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Appointment;
import com.kuycoding.periksayook.ui.patient.appointment.camera.CamActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PatienMakeAppActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA_PICT_REQ = 100;
    private static final String TAG = "tag";
    private EditText edit_nama;
    private AutoCompleteTextView spinner_jekel;
    private EditText edit_tgl;
    private EditText edit_paesan;
    private ImageView imgProfile;
    private Uri imgUri;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId;
    private String profileImageUrl;
    private Date date_created;
    private DatePickerDialog datePickerDialog;
    private Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patien_make_app);

        edit_nama = findViewById(R.id.edit_req_nama_pasien);
        edit_paesan = findViewById(R.id.edit_req_note_pasien);

        String[] item = new String[]{
                "Laki-laki",
                "Perempuan"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);

        spinner_jekel = findViewById(R.id.edit_req_jekel_pasien);
        spinner_jekel.setAdapter(adapter);
        spinner_jekel.setOnTouchListener((v, event) -> {
            spinner_jekel.showDropDown();
            spinner_jekel.requestFocus();
            return false;
        });

        imgProfile = findViewById(R.id.img_req_pasien);
        imgProfile.setOnClickListener(this);


        edit_tgl = findViewById(R.id.edit_req_tglahir_pasien);
        edit_tgl.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            datePickerDialog = new DatePickerDialog(this, R.style.MySpinnerDatePickerStyle, (view, i, i1, i2) -> {
                Calendar cal = Calendar.getInstance();
                cal.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = dateFormat1.format(cal.getTime());
                //editTgl.setText(i2 + "-" + (i1 + 1) + "-" + i);
                edit_tgl.requestFocus();
                edit_tgl.setText(dateString);
            }, year, month, day);
            datePickerDialog.show();
        });

        Button btnSend = findViewById(R.id.btn_send_appointment);
        btnSend.setOnClickListener(v -> requestAppoinment());

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        date_created = Calendar.getInstance().getTime();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Identitas Anak");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PICT_REQ);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PICT_REQ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestAppoinment() {
        final String nama = edit_nama.getText().toString().trim();
        final String jekel = spinner_jekel.getText().toString().trim();
        final String tgl = edit_tgl.getText().toString().trim();
        final String pesan = edit_paesan.getText().toString().trim();
        final String imageUrl = profileImageUrl;

        if (TextUtils.isEmpty(nama)) {
            edit_nama.setError("Tidak boleh kosong!");
        }
        if (TextUtils.isEmpty(jekel)) {
            spinner_jekel.setError("Tidak boleh kosong!");
        }
        if (TextUtils.isEmpty(tgl)) {
            edit_tgl.setError("Tidak boleh kosong!");
        }
        if (TextUtils.isEmpty(pesan)) {
            edit_paesan.setError("Tidak boleh kosong!");
        } else {
            DocumentReference dRefAppointment = fStore.collection("appointment").document();
            String appointment_id;
            appointment_id = dRefAppointment.getId();
            DocumentReference dRefUser = fStore.collection("users").document(userId).collection("appointment").document(appointment_id);
            //DocumentReference dRefDokter = fStore.collection("dokter").document(doctorUid).collection("appointment").document(appointment_id);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("doctorName", null);
            userMap.put("patientName", nama);
            userMap.put("doctorId", null);
            userMap.put("patientId", userId);
            userMap.put("jekel", jekel);
            userMap.put("tgl", tgl);
            userMap.put("notes", pesan);
            userMap.put("image", imageUrl);
            userMap.put("accepted", false);
            userMap.put("rejected", false);
            userMap.put("appointmentId", appointment_id);
            userMap.put("date_created", date_created);
            userMap.put("status", "Menunggu pengecekan");
            userMap.put("pesan_toPasien", "Tidak ada pesan!");

            Appointment appointment = new Appointment(null, nama, null, userId, jekel, tgl, imageUrl, pesan, false, false, appointment_id, date_created, "buka", "Tidak ada pesan!");

            dRefUser.set(userMap).addOnCompleteListener(task -> Log.d(TAG, "onSuccess: success add appointment user" + userId))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
            //dRefDokter.set(userMap).addOnCompleteListener(task -> Log.d(TAG,"onSuccess: success add appointment dokter" + userID))
            // .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));
            dRefAppointment.set(userMap).addOnCompleteListener(task -> Log.d(TAG, "onSuccess: success add appointment appointment" + userId))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

            Intent intent = new Intent(PatienMakeAppActivity.this, PatientAppListDoctorActivity.class);
            intent.putExtra(PatientAppListDoctorActivity.EXTRA_APP, appointment);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_req_pasien:
                LayoutInflater inflater = LayoutInflater.from(this);
                @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_info, null);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showCameraChoose();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;
        }
    }

    private void showCameraChoose() {
        startActivityForResult(new Intent(PatienMakeAppActivity.this, CamActivity.class), CAMERA_PICT_REQ);
      /*  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_PICT_REQ);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PICT_REQ && resultCode == RESULT_OK) {

            assert data != null;
            File imgFile = new File(Objects.requireNonNull(data.getStringExtra("keyName")));
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            Bitmap bitmap = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            imgProfile.setImageBitmap(bitmap);
            imgUri = Uri.fromFile(imgFile);
            imgProfile.setImageURI(imgUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] dataBAOS = baos.toByteArray();

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading Image...");
            dialog.show();
            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("dataPasien/" + System.currentTimeMillis() + ".jpg");

            UploadTask uploadTask = profileImageRef.putBytes(dataBAOS);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    profileImageUrl = uri.toString();
                    Log.d(TAG, "onActivityResult: " + profileImageUrl);
                });
                dialog.dismiss();
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progres = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded "+ (int) progres + " %");
                }
            }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
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
