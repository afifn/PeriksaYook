package com.kuycoding.periksayook.ui.dokter.fragment;


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
import com.kuycoding.periksayook.adapter.BookingAdapterPatient;
import com.kuycoding.periksayook.model.Booking;

import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class DokterBookingFragment extends Fragment {
    private static final String TAG = "HomePatientActivity";
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private BookingAdapterPatient bookingAdapter;


    public DokterBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dokter_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        viewBooking();
    }

    private void viewBooking() {
        recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.rv_list_booking);
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
                    Toast.makeText(getContext(), "Hapus", Toast.LENGTH_SHORT).show();
                    BookingAdapterPatient.ViewHoder viewHoder = (BookingAdapterPatient.ViewHoder) viewHolder;
                    viewHoder.deleteItem();
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



        Query query = fStore.collection("dokter").document(user.getUid()).collection("booking").orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Booking> options = new FirestoreRecyclerOptions.Builder<Booking>().setQuery(query, Booking.class).build();

        bookingAdapter = new BookingAdapterPatient(options);
        bookingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(bookingAdapter.getItemCount());
                //  super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(bookingAdapter);
        bookingAdapter.setOnItemClickListener(new BookingAdapterPatient.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Booking booking = documentSnapshot.toObject(Booking.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                String nama = documentSnapshot.getString("name");
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {
                final DocumentReference documentReference = snapshot.getReference();
                final Booking booking = snapshot.toObject(Booking.class);

                documentReference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: item deleted");
                            }
                        });
                Snackbar.make(recyclerView, "Item delete", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                documentReference.set(Objects.requireNonNull(booking));
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        bookingAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        bookingAdapter.stopListening();
    }

}
