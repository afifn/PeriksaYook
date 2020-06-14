package com.kuycoding.periksayook.adapter;

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
import com.kuycoding.periksayook.model.Patient;
import com.squareup.picasso.Picasso;

public class PatienAdapter extends FirestoreRecyclerAdapter<Patient, PatienAdapter.ViewHolder> {
    private static final String TAG = "HomePatientActivity";
    private PatienAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PatienAdapter(@NonNull FirestoreRecyclerOptions<Patient> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PatienAdapter.ViewHolder viewHolder, int i, @NonNull Patient patient) {
        viewHolder.tvName.setText(patient.getName());
        viewHolder.tvEmail.setText(patient.getEmail());
        viewHolder.tvJekel.setText(patient.getJekel());
        Picasso.get()
                .load(patient.getImgUrl())
                .fit()
                .placeholder(R.drawable.ic_broken_image_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(viewHolder.imgPoster);
        Log.d(TAG, "onBindViewHolder: " + patient.getImgUrl());
    }

    @NonNull
    @Override
    public PatienAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patient, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPoster;
        private TextView tvName, tvEmail, tvJekel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_profile);
            tvName = itemView.findViewById(R.id.tv_nama);
            tvJekel = itemView.findViewById(R.id.tv_jekel);
            tvEmail = itemView.findViewById(R.id.tv_email);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
        public void deleteItem(){
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, int position);

        void handleDeleteItem(DocumentSnapshot snapshot);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
