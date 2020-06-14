package com.kuycoding.periksayook.ui.patient.appointment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.adapter.DoctorAdapterRv;
import com.kuycoding.periksayook.model.Appointment;
import com.kuycoding.periksayook.model.Doctor;
import com.kuycoding.periksayook.ui.patient.PatienVerifyAppActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PatientAppListDoctorActivity extends AppCompatActivity {
    public static final String EXTRA_APP = "extra_app";
    private static final String TAG = "tag";
    private Appointment appointment;
    private RecyclerView recyclerView;
    private DoctorAdapterRv adapterRv;
    private FirebaseFirestore fStore;
    private Date date_created;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_app_list_doctor);

        setUpRecyclerViewDoctor();
        appointment = getIntent().getParcelableExtra(EXTRA_APP);
        fStore = FirebaseFirestore.getInstance();
        date_created = Calendar.getInstance().getTime();
        Log.d(TAG, "onCreate: " + appointment.getDoctorId());
    }

    private void setUpRecyclerViewDoctor() {
        recyclerView = findViewById(R.id.rv_list_dokter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = FirebaseFirestore.getInstance().collection("dokter").orderBy("name",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>().setQuery(query, Doctor.class).build();

        adapterRv = new DoctorAdapterRv(options);
        adapterRv.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapterRv.getItemCount());
                //  super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapterRv);
        adapterRv.setOnItemClickListener(new DoctorAdapterRv.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                doctor = documentSnapshot.toObject(Doctor.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String nama = documentSnapshot.getString("name");

                DocumentReference dRefAppointment = fStore.collection("appointment").document(appointment.getAppointmentId());
                DocumentReference dRefUser = fStore.collection("users").document(appointment.getPatientId()).collection("appointment").document(appointment.getAppointmentId());
                DocumentReference dRefDokter = fStore.collection("dokter").document(doctor.getUid()).collection("appointment").document(appointment.getAppointmentId());

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("doctorName",doctor.getName());
                userMap.put("patientName",appointment.getPatientName());
                userMap.put("doctorId",doctor.getUid());
                userMap.put("patientId",appointment.getPatientId());
                userMap.put("jekel",appointment.getJekel());
                userMap.put("tgl", appointment.getTgl());
                userMap.put("notes",appointment.getNotes());
                userMap.put("image",appointment.getImage());
                userMap.put("accepted", false);
                userMap.put("rejected", false);
                userMap.put("appointmentId", appointment.getAppointmentId());
                userMap.put("date_created", date_created);
                userMap.put("status","buka");
                userMap.put("pesan_toPasien", appointment.getNote_toPasien());

                dRefAppointment.update(userMap).addOnCompleteListener(task -> {
                    Log.d(TAG, "onSuccess: " );
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: ");
                });

                dRefUser.update(userMap).addOnCompleteListener(task -> {
                    Log.d(TAG, "onSuccess: ");
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: ");
                });

                dRefDokter.set(userMap).addOnCompleteListener(task -> {
                    Log.d(TAG, "setUpRecyclerViewDoctor: " + appointment.getAppointmentId());
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: " + e.toString());
                });

                notifikasi();
                Intent intent = new Intent(PatientAppListDoctorActivity.this, PatienVerifyAppActivity.class);
                intent.putExtra(PatienVerifyAppActivity.EXTRA_SUCCESS, appointment);
                startActivity(intent);

                Log.d(TAG, "setUpRecyclerViewDoctor: " +doctor.getUid());
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

                                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + doctor.getUid() + "\"}],"

                                    + "\"data\": {\"foo\": \"bar\"},"
                                    + "\"contents\": {\"en\": \"Request pengecekan mata, atas nama pasien " + appointment.getPatientName() + "\"}"
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
            public void handleDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }

    private void notifikasi() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterRv.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterRv.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
