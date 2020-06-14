package com.kuycoding.periksayook.ui.dokter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.helpers.OnSwipeTouchListener;
import com.kuycoding.periksayook.model.Appointment;
import com.kuycoding.periksayook.ui.patient.appointment.DetailPhotoActivity;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class DokterDetailAppointmentActivity extends AppCompatActivity{
    public final static String EXTRA_APP = "extra_APP";
    private TextView tvNama, tvTgl, tvNote, tvJekel, tvStatus;
    private Appointment appointment;
    private TouchImageView imgPasien;
    private FirebaseFirestore fStore;
    private String doctorUid;
    private String patientId;
    private String patienName;
    private ImageButton swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter_cek_appointment);

        tvNama  = findViewById(R.id.txt_nama);
        tvTgl   = findViewById(R.id.txt_tgl);
        tvNote  = findViewById(R.id.txt_note);
        tvJekel = findViewById(R.id.txt_jekel);
        tvStatus = findViewById(R.id.txt_status);
        imgPasien = findViewById(R.id.img_pasien);
        imgPasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DokterDetailAppointmentActivity.this, DetailPhotoActivity.class);
                intent.putExtra(DetailPhotoActivity.EXTRA_APP_PATIENT_POTO, appointment);
                startActivity(intent);
            }
        });
        swipe = findViewById(R.id.swipe);

        appointment = getIntent().getParcelableExtra(EXTRA_APP);
        fStore = FirebaseFirestore.getInstance();
        readDetailAppointment();

        patientId = appointment.getPatientId();
        patienName = appointment.getPatientName();
        doctorUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Log.d("TAG", "Get uid patien" + patientId);
        Log.d("TAG", "Get uid dokter" + doctorUid);
        Log.d("TAG", "Get uid dokter" + patienName);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Pengecekan");

        swipeToAction();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void swipeToAction() {
        swipe.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                acceptTrue();
                sendNotificationAcc();
                Toast.makeText(DokterDetailAppointmentActivity.this, "Notifikasi terkirim ke " +appointment.getPatientName() + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                acceptFalse();
                sendNotificationRejected();
                Intent intent = new Intent(DokterDetailAppointmentActivity.this, DokterNotesActivity.class);
                intent.putExtra(DokterNotesActivity.EXTRA_PESAN, appointment);
                startActivity(intent);
                Toast.makeText(DokterDetailAppointmentActivity.this, "Notifikasi terkirim ke " +appointment.getPatientName() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readDetailAppointment() {
        DocumentReference getAppointment = fStore.collection("dokter").document();
        getAppointment.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Appointment appointment = getIntent().getParcelableExtra(EXTRA_APP);
                assert appointment != null;
                tvNama.setText(appointment.getPatientName());
                tvJekel.setText(appointment.getJekel());
                tvTgl.setText(appointment.getTgl());
                tvNote.setText(appointment.getNotes());
                tvStatus.setText(appointment.getStatus());

                Picasso.get()
                        .load(appointment.getImage())
                        .centerCrop()
                        .error(R.drawable.ic_broken_image_black_24dp)
                        .placeholder(R.drawable.ic_broken_image_black_24dp)
                        .fit()
                        .into(imgPasien);
            }
        });
    }

    private void sendNotificationAcc() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String sendToUserID, senToPasienName;

                    sendToUserID = patientId;
                    senToPasienName = patienName;
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NTJkYWIzNTMtYmIxZS00MjEzLWE4MDItMDIyNWNkYzZkZDIz");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"c0b97cfa-c57a-444d-a9d3-d924de2c8c84\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + sendToUserID + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"Selamat mata pasien " + senToPasienName + " normal " + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);
                        Log.d("strJsonBody",strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes(StandardCharsets.UTF_8);
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendNotificationRejected() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String sendToUserID, senToPasienName;

                    sendToUserID = patientId;
                    senToPasienName = patienName;
                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NTJkYWIzNTMtYmIxZS00MjEzLWE4MDItMDIyNWNkYzZkZDIz");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"c0b97cfa-c57a-444d-a9d3-d924de2c8c84\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + sendToUserID + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"dr. " + appointment.getDoctorName() + "  menyarankan  " + senToPasienName + " untuk memeriksakan matanya ke rumah sakit, atau buat perjanjian dengan kami." + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);
                        Log.d("strJsonBody",strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes(StandardCharsets.UTF_8);
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    private void acceptTrue() {
        DocumentReference dbDok = fStore.collection("dokter").document(doctorUid).collection("appointment").document(appointment.getAppointmentId());
        Map<String, Object> userMap = new HashMap<>();DocumentReference dbPasien = fStore.collection("users").document(appointment.getPatientId()).collection("appointment").document(appointment.getAppointmentId());

        userMap.put("accepted", true);
        userMap.put("rejected", false);
        userMap.put("status","Normal");

        dbPasien.update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: success update appointment user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });

        dbDok.update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: success update appointment dokter");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
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

    private void acceptFalse() {
        DocumentReference dbDok = fStore.collection("dokter").document(doctorUid).collection("appointment").document(appointment.getAppointmentId());
        DocumentReference dbPasien = fStore.collection("users").document(appointment.getPatientId()).collection("appointment").document(appointment.getAppointmentId());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("accepted", false);
        userMap.put("rejected", true);
        userMap.put("status","Tidak normal");

        dbPasien.update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: success update appointment user");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });

        dbDok.update(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","onSuccess: success update appointment dokter");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: " + e.toString());
            }
        });
    }

}
