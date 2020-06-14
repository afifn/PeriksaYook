package com.kuycoding.periksayook.ui.dokter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Appointment;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class DokterNotesActivity extends AppCompatActivity {
    public final static String EXTRA_PESAN = "extra_pesan";
    private FirebaseFirestore fStore;
    private Appointment appointment;
    private EditText edit_pesan;
    private String doctorUid;
    private String isiPesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter_notes);

        edit_pesan   = findViewById(R.id.edit_pesan);
        Button btnSend = findViewById(R.id.btn_send_catatan);
        btnSend.setOnClickListener(v -> senPesanToPasien());

        appointment = getIntent().getParcelableExtra(EXTRA_PESAN);
        doctorUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String appointmentId = appointment.getAppointmentId();

        fStore = FirebaseFirestore.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Kirim Pesan");
    }

    private void senPesanToPasien() {
        isiPesan = edit_pesan.getText().toString().trim();


        if (TextUtils.isEmpty(isiPesan)) {
            edit_pesan.setError("Tidak boleh kosong!");
        } else {
            DocumentReference pesanRef = fStore.collection("pesan").document(appointment.getAppointmentId());
            DocumentReference pesanDokterRef = fStore.collection("dokter").document(doctorUid).collection("appointment").document(appointment.getAppointmentId());
            DocumentReference pesanPatienRef = fStore.collection("users").document(appointment.getPatientId()).collection("appointment").document(appointment.getAppointmentId());

            Map<String, Object> map = new HashMap<>();
            map.put("pesan_toPasien",isiPesan);

            pesanRef.update(map).addOnSuccessListener(aVoid -> Log.d("TAG","onSuccess"))
                    .addOnFailureListener(e -> Log.d("TAG","onFailed"+ e.toString()));

            pesanPatienRef.update(map).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess"))
                    .addOnFailureListener(e -> Log.d("TAG","onFailed"+ e.toString()));

            pesanDokterRef.update(map).addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> Log.d("TAG","onFailed"+ e.toString()));

            sendNotification();
            startActivity(new Intent(this, DokterActivity.class));
            finish();
        }
    }

    private void sendNotification() {
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

                            + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + appointment.getPatientId() + "\"}],"

                            + "\"data\": {\"foo\": \"bar\"},"
                            + "\"contents\": {\"en\": \" " + isiPesan +  "\"}"
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
