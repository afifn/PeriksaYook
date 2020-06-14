package com.kuycoding.periksayook.ui.admin.ui.pasient;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.adapter.PatienAdapter;
import com.kuycoding.periksayook.model.Patient;

import java.util.Objects;

public class ListPatientActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PatienAdapter adapterRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patient);

        setUpRecyclerViewDoctor();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        FloatingActionButton fab_add = findViewById(R.id.fab_add_patien);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListPatientActivity.this, AddPatientActivity.class));
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daftar Pasien");
    }

    private void setUpRecyclerViewDoctor() {
        recyclerView = findViewById(R.id.rv_list_patient);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Query query = FirebaseFirestore.getInstance().collection("users").orderBy("name",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>().setQuery(query, Patient.class).build();

       /* new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(ListPatientActivity.this, "Hapus", Toast.LENGTH_SHORT).show();
                    PatienAdapter.ViewHolder viewModel = (PatienAdapter.ViewHolder) viewHolder;
                    viewModel.deleteItem();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(ListPatientActivity.this,R.color.red))
                        .addActionIcon(R.drawable.ic_delete_black_24dp)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);*/

        adapterRv = new PatienAdapter(options);
        adapterRv.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapterRv.getItemCount());
                //  super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapterRv);
        adapterRv.setOnItemClickListener(new PatienAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot snapshot, int position) {
                Patient patient = snapshot.toObject(Patient.class);
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
               /* final DocumentReference documentReference = snapshot.getReference();
                final Patient patient = snapshot.toObject(Patient.class);

                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                AuthCredential credential = EmailAuthProvider.getCredential("email","password");
                                FirebaseUser user = null;
                                if (user != null) {
                                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                                        user.delete().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "onSuccess: delete user auth");
                                            }
                                        });
                                    });
                                }
                                Log.d(TAG, "onSuccess: ");
                            }
                        });
                Snackbar.make(recyclerView, "Item delete", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                documentReference.set(patient);
                            }
                        }).show();*/
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
