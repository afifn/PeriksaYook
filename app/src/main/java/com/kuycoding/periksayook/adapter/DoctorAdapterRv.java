package com.kuycoding.periksayook.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Doctor;
import com.squareup.picasso.Picasso;

public class DoctorAdapterRv extends FirestoreRecyclerAdapter<Doctor, DoctorAdapterRv.ViewHoder> {
    private static final String TAG = "HomePatientActivity";
    private OnItemClickListener listener;
    private Context context;

    public DoctorAdapterRv(@NonNull FirestoreRecyclerOptions<Doctor> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final DoctorAdapterRv.ViewHoder viewHoder, int i, @NonNull final Doctor doctor) {
        viewHoder.tvName.setText("dr. " + doctor.getName());
        viewHoder.tvSpecialist.setText("Spesialis " + doctor.getSpec());
        viewHoder.tvUniv.setText(doctor.getHospital());
        viewHoder.tvStatus.setText(doctor.getStatus());
        Picasso.get()
                .load(doctor.getImgUrl())
                .centerCrop()
                .fit()
                .error(R.drawable.ic_broken_image_black_24dp)
                .placeholder(R.drawable.ic_broken_image_black_24dp)
                .into(viewHoder.imgPoster);
        Log.d(TAG, "Gambar : " + doctor.getImgUrl());
        Log.d(TAG, "Nama Dokter : " + doctor.getName());
        Log.d(TAG, "Spesialis : " + doctor.getSpec());
        Log.d(TAG, "Dinas : " + doctor.getHospital());
    }

    @NonNull
    @Override
    public DoctorAdapterRv.ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_doctor, parent, false);
        return new DoctorAdapterRv.ViewHoder(view);
    }

    public class ViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPoster;
        private TextView tvName, tvUniv, tvSpecialist, tvStatus;
        String doctorId;

        ViewHoder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_dokter_poster);
            tvName = itemView.findViewById(R.id.tv_dokter_title);
            tvSpecialist = itemView.findViewById(R.id.tv_dokter_spesialis);
            tvUniv = itemView.findViewById(R.id.tv_dokter_dinas);
            tvStatus = itemView.findViewById(R.id.tv_dokter_online_offline);
            doctorId = null;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
        public void itemDelete() {
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void handleDeleteItem(DocumentSnapshot snapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
