package com.kuycoding.periksayook.ui.dokter.fragment;


import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.adapter.AppointmentAdapter;
import com.kuycoding.periksayook.model.Appointment;
import com.kuycoding.periksayook.ui.dokter.DokterDetailAppointmentActivity;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class DokterRequestFragment extends Fragment {
    private final static String  TAG = "tag";
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private AppointmentAdapter mAppointmentAdapter;

    public DokterRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dokter_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        viewRequestPengecekan();
    }

    private void viewRequestPengecekan() {
        recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.rv_list_appointment);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    Toast.makeText(getContext(), "Hapus" , Toast.LENGTH_SHORT).show();
                    AppointmentAdapter.ViewModel viewModel = (AppointmentAdapter.ViewModel) viewHolder;
                    viewModel.deleteItem();
                }
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.red))
                        .addActionIcon(R.drawable.ic_delete_black_24dp)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);

        Query query = fStore.collection("dokter").document(user.getUid()).collection("appointment").orderBy("date_created", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>().setQuery(query, Appointment.class).build();

        mAppointmentAdapter = new AppointmentAdapter(options);
        mAppointmentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(mAppointmentAdapter.getItemCount());
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
                Intent intent = new Intent(getContext(), DokterDetailAppointmentActivity.class);
                intent.putExtra(DokterDetailAppointmentActivity.EXTRA_APP, appointment);
                startActivity(intent);
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                final DocumentReference documentReference = snapshot.getReference();
                final Appointment appointment = snapshot.toObject(Appointment.class);

                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: ");
                            }
                        });
                Snackbar.make(recyclerView, "Item delete", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                documentReference.set(Objects.requireNonNull(appointment));
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAppointmentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAppointmentAdapter.stopListening();
    }
}
