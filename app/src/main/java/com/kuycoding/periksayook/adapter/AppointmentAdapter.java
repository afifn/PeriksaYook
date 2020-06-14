package com.kuycoding.periksayook.adapter;

import android.annotation.SuppressLint;
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
import com.kuycoding.periksayook.model.Appointment;
import com.squareup.picasso.Picasso;

public class AppointmentAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentAdapter.ViewModel> {
    private static final String TAG = "HomePatientActivity";
    private AppointmentAdapter.OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AppointmentAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull AppointmentAdapter.ViewModel viewModel, int i, @NonNull Appointment appointment) {
        viewModel.tvDokterName.setText("dr. " + appointment.getDoctorName());
        viewModel.tvPatientName.setText(appointment.getPatientName());
        viewModel.tvId.setText(appointment.getAppointmentId());
        Picasso.get()
                .load(appointment.getImage())
                .placeholder(R.drawable.ic_assignment_black_24dp)
                .error(R.drawable.ic_assignment_black_24dp)
                .into(viewModel.imgPatient);

        Log.d(TAG, "OnBindView Dokter : " + appointment.getDoctorName());
        Log.d(TAG, "OnBindView Pasien : " + appointment.getPatientName());
        Log.d(TAG, "OnBindView Kekamin : " + appointment.getJekel());
        Log.d(TAG, "OnBindView Tgl : " + appointment.getTgl() + " ID " + appointment.getAppointmentId());
    }

    @NonNull
    @Override
    public AppointmentAdapter.ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_appointment, parent, false);
        return new AppointmentAdapter.ViewModel(view);
    }

    public class ViewModel extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgPatient;
        private TextView tvDokterName, tvPatientName, tvId;

        ViewModel(@NonNull View itemView) {
            super(itemView);
            tvDokterName = itemView.findViewById(R.id.appListDName);
            tvPatientName = itemView.findViewById(R.id.appListPasienName);
            tvId = itemView.findViewById(R.id.appId);
            imgPatient = itemView.findViewById(R.id.imageList);

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
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void handleDeleteItem(DocumentSnapshot snapshot);
    }

    public void setOnItemClickListener(AppointmentAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}