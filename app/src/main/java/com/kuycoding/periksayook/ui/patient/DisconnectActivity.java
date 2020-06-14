package com.kuycoding.periksayook.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kuycoding.periksayook.R;
import com.kuycoding.periksayook.ui.SplashActivity;

public class DisconnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnect);
        Button tryAgain = findViewById(R.id.btnTryAgain);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(new Intent(DisconnectActivity.this, SplashActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }
}
