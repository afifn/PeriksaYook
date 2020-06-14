package com.kuycoding.periksayook.ui.dokter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.adapter.AppointmentAdapter;
import com.kuycoding.periksayook.model.Appointment;

import java.util.Objects;

public class DokterRequestAppointmentActivity extends AppCompatActivity {
    private static final String TAG = "HomePatientActivity";
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private AppointmentAdapter mAppointmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokter_request_appointment);

        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        setUpRecyclerViewDoctor();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request Pengecekan");
    }

    private void setUpRecyclerViewDoctor() {
        recyclerView = findViewById(R.id.rv_list_appointment);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = fStore.collection("dokter").document(user.getUid()).collection("appointment").orderBy("date_created", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>().setQuery(query, Appointment.class).build();

        mAppointmentAdapter = new AppointmentAdapter(options);
        mAppointmentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(mAppointmentAdapter.getItemCount());
                //  super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(mAppointmentAdapter);
        mAppointmentAdapter.setOnItemClickListener(new AppointmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Appointment appointment = documentSnapshot.toObject(Appointment.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String nama = documentSnapshot.getString("name");

                Log.d(TAG, "\nMessage id : " + id + " \nPath :" + path + " \nNama :" + nama);
                Intent intent = new Intent(DokterRequestAppointmentActivity.this, DokterDetailAppointmentActivity.class);
                intent.putExtra(DokterDetailAppointmentActivity.EXTRA_APP, appointment);
                startActivity(intent);
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {

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

    @Override
    protected void onStart() {
        super.onStart();
        mAppointmentAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppointmentAdapter.stopListening();
    }
}
