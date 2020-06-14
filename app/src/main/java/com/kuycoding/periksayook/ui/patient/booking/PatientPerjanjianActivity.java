package com.kuycoding.periksayook.ui.patient.booking;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Appointment;
import com.kuycoding.periksayook.model.Booking;
import com.kuycoding.periksayook.ui.patient.PatienSuccesBookingActivity;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class PatientPerjanjianActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_APP_PATIENT = "extra_app_patient";
    private static final String TAG = "tag";
    private Booking booking;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Appointment appointment;
    private EditText edit_tanggal, edit_jam, edit_notes;
    private TextView tvNotif;
    private ImageView imgProfileDokter;
    private FirebaseFirestore fStore;
    private String userId;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_perjanjian);

        fStore = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        edit_jam = findViewById(R.id.edit_jam);
        edit_notes = findViewById(R.id.edit_pesan);
        edit_tanggal = findViewById(R.id.edit_tanggal);
        tvNotif = findViewById(R.id.txt_pesan_dokter);
        imgProfileDokter = findViewById(R.id.img_profile_dokter);
        Button btnBuatJanji = findViewById(R.id.btn_send_janji);
        btnBuatJanji.setOnClickListener(this);

        appointment = getIntent().getParcelableExtra(EXTRA_APP_PATIENT);

        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        loadData();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buat Perjanjian");
    }


    private void loadData() {
        fStore.collection("dokter").document(appointment.getDoctorId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String img = documentSnapshot.getString("imgUrl");
                            Picasso.get()
                                    .load(img)
                                    .fit()
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_broken_image_black_24dp)
                                    .error(R.drawable.ic_broken_image_black_24dp)
                                    .into(imgProfileDokter);
                            Log.d("TAG","iddokter" + appointment.getDoctorId());
                        } else {
                            Log.d(TAG, "onSuccess: ");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

        fStore.collection("users").document(userId).collection("appointment").document(appointment.getAppointmentId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            appointment = getIntent().getParcelableExtra(EXTRA_APP_PATIENT);
                            tvNotif.setText(documentSnapshot.getString("pesan_toPasien"));
                        } else {
                            Log.d(TAG, "onSuccess: data kosong");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

        edit_tanggal.setInputType(InputType.TYPE_NULL);
        edit_tanggal.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            datePickerDialog = new DatePickerDialog(PatientPerjanjianActivity.this, (view, i, i1, i2) -> {
                calendar.set(i, i1, i2);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy");
                String dateString = dateFormat.format(calendar.getTime());
                //editTgl.setText(i2 + "-" + (i1 + 1) + "-" + i);
                edit_tanggal.setText(dateString);
            }, year, month, day);
            datePickerDialog.show();
        });

        edit_jam.setInputType(InputType.TYPE_NULL);
        edit_jam.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(PatientPerjanjianActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                    calendar.set(hourOfDay, minute);
                    edit_jam.setText(updateTime(hourOfDay, minute));
                }
            },hour,minute,false);
            timePickerDialog.show();
        });
    }

    private String updateTime(int hours, int mins) {
        String timeSet;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes;
        if (mins < 10) {
            minutes = "0" + mins;
        } else
            minutes = String.valueOf(mins);
        // Append in a StringBuilder
        return String.valueOf(hours) + ':' +
                minutes + " " + timeSet;
    }

    private void sendBooking() {
        String tgl = edit_tanggal.getText().toString().trim();
        String jam = edit_jam.getText().toString().trim();
        String catatan = edit_notes.getText().toString().trim();
        String patienName = appointment.getPatientName().trim();
        String doctorName = appointment.getDoctorName().trim();
        String idPatient = appointment.getPatientId().trim();
        String idDoctor = appointment.getDoctorId().trim();

        if (TextUtils.isEmpty(tgl)) {
            edit_tanggal.setError("Tidak boleh kosong!");
        } if (TextUtils.isEmpty(jam)) {
            edit_jam.setError("Tidak boleh kosong!");
        } if (TextUtils.isEmpty(catatan)) {
            edit_notes.setError("Tidak boleh kosong!");
        } else {
            DocumentReference dbBooking = fStore.collection("booking").document();
            String idbooking;
            idbooking = dbBooking.getId();
            DocumentReference dbBookDok = fStore.collection("dokter").document(appointment.getDoctorId()).collection("booking").document(idbooking);
            DocumentReference dbBookPatien = fStore.collection("users").document(userId).collection("booking").document(idbooking);

            Map<String, Object> book = new HashMap<>();
            book.put("idBooking", idbooking);
            book.put("patientName", patienName);
            book.put("doctorName", doctorName);
            book.put("idPatient", idPatient);
            book.put("idDoctor", idDoctor);
            book.put("notes", catatan);
            book.put("date", tgl);
            book.put("clock", jam);

            booking = new Booking(patienName, doctorName, idPatient, idDoctor, catatan, tgl, jam, idbooking);
            dbBooking.set(book).addOnSuccessListener(aVoid -> Log.d("TAG","onSuccess"))
                    .addOnFailureListener(e -> Log.d("TAG","onFailure"+ e.toString()));

            dbBookDok.set(book).addOnSuccessListener(aVoid -> {

            });

            dbBookPatien.set(book).addOnSuccessListener(aVoid -> {

            });
            Log.d("TAG", "sendBooking: "+booking.getNotes());
            notifikasi();

            Intent intent = new Intent(PatientPerjanjianActivity.this, PatienSuccesBookingActivity.class);
            intent.putExtra(PatienSuccesBookingActivity.EXTRA_BOOKING, booking);
            startActivity(intent);
            finish();
        }
    }

    private void notifikasi() {
        AsyncTask.execute(() -> {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

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

                            + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + appointment.getDoctorId() + "\"}],"

                            + "\"data\": {\"foo\": \"bar\"},"
                            + "\"contents\": {\"en\": \"Pasien " + appointment.getPatientName() + " ingin bertemu pada " + booking.getDate() +" \"}"
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
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_janji) {
            sendBooking();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
