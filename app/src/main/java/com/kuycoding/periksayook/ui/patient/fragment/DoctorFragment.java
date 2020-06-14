package com.kuycoding.periksayook.ui.patient.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.adapter.DoctorAdapterRv;
import com.kuycoding.periksayook.model.Doctor;
import com.kuycoding.periksayook.ui.patient.DetailDoctorActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoctorFragment extends Fragment {
    private RecyclerView recyclerView;
    private DoctorAdapterRv adapterRv;

    public DoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        loadCurrentPatientData();
    }

    private void loadCurrentPatientData() {
        recyclerView = getView().findViewById(R.id.rv_list_dokter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
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
                Doctor doctor = documentSnapshot.toObject(Doctor.class);

                Intent intent = new Intent(getContext(), DetailDoctorActivity.class);
                intent.putExtra(DetailDoctorActivity.EXTRA_DOCTOR, doctor);
                startActivity(intent);
            }

            @Override
            public void handleDeleteItem(DocumentSnapshot snapshot) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterRv.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterRv.startListening();
    }
}
