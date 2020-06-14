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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Booking;
import com.squareup.picasso.Picasso;

public class BookingAdapter extends FirestoreRecyclerAdapter<Booking, BookingAdapter.ViewHoder> {
    private static final String TAG = "HomePatientActivity";
    private OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookingAdapter(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull BookingAdapter.ViewHoder viewHoder, int i, @NonNull Booking booking) {
        viewHoder.tvDokter.setText("Janji dengan dr. " + booking.getDoctorName());
        viewHoder.tvJam.setText(booking.getClock());
        viewHoder.tvTanggal.setText(booking.getDate());

        FirebaseFirestore.getInstance().collection("dokter").document(booking.getIdDoctor()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String img = documentSnapshot.getString("imgUrl");
                            Picasso.get()
                                    .load(img)
                                    .placeholder(R.drawable.ic_broken_image_black_24dp)
                                    .error(R.drawable.ic_broken_image_black_24dp)
                                    .centerCrop()
                                    .fit()
                                    .into(viewHoder.imgProfile);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_booking, parent, false);
        return new BookingAdapter.ViewHoder(view);
    }


    public class ViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imgProfile;
        private TextView tvDokter, tvJam, tvTanggal;
        private String doctorId;
        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.img_dokter);
            tvDokter = itemView.findViewById(R.id.txt_dokter_name);
            tvJam  = itemView.findViewById(R.id.txt_hari);
            tvTanggal = itemView.findViewById(R.id.txt_tanggal);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
        public void deleteItem() {
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void handleDeleteItem(DocumentSnapshot snapshot);
    }

    public void setOnItemClickListener(BookingAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
