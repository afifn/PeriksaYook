package com.kuycoding.periksayook.ui.admin.ui.dokter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.adapter.DoctorAdapterRv;
import com.kuycoding.periksayook.model.Doctor;

import java.util.Objects;

public class ListDoctorActivity extends AppCompatActivity {
    private DoctorAdapterRv adapterRv;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_doctor);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

//        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        FloatingActionButton fab = findViewById(R.id.fab_add_dokter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListDoctorActivity.this, AddDokterActivity.class));
            }
        });

        setUpRecyclerViewDoctor();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daftar Dokter");
    }

    private void setUpRecyclerViewDoctor() {
        recyclerView = findViewById(R.id.rv_list_dokter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        /*new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(ListDoctorActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                    DoctorAdapterRv.ViewHoder viewModel = (DoctorAdapterRv.ViewHoder) viewHolder;
                    viewModel.itemDelete();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(ListDoctorActivity.this, R.color.red))
                        .addActionIcon(R.drawable.ic_delete_black_24dp)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);*/

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
                Doctor doctor = documentSnapshot.toObject(Doctor.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String nama = documentSnapshot.getString("name");
                Log.d("TAG", "onItemClick: " + doctor.getUid());
                Log.d("TAG", "onItemClick Passwd: " + doctor.getPassword());
/*
                Intent intent = new Intent(ListDoctorActivity.this, ManageUserActivity.class);
                intent.putExtra(ManageUserActivity.EXTRA_DOKTER, doctor);
                startActivity(intent);*/
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                Doctor doctor = snapshot.toObject(Doctor.class);

            }
        });
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
