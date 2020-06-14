package com.kuycoding.periksayook.ui.patient.appointment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.model.Appointment;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailPhotoActivity extends AppCompatActivity {
    public static final String EXTRA_APP_PATIENT_POTO = "detail_poto";
    ImageView imageViewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        imageViewDetail = findViewById(R.id.imgDetail);
        Appointment appointment = getIntent().getParcelableExtra(EXTRA_APP_PATIENT_POTO);
        assert appointment != null;
        Picasso.get().load(appointment.getImage()).into(imageViewDetail);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(appointment.getPatientName());
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
