package com.kuycoding.periksayook.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kuycoding.periksayook.R;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputEmail;
    private ProgressDialog loadingBar;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        
        inputEmail = findViewById(R.id.edit_email);
        loadingBar = new ProgressDialog(this);

        TextView btLoginPage = findViewById(R.id.loginPage);
        btLoginPage.setOnClickListener(this);

        Button btReset = findViewById(R.id.bt_resetpassword);
        btReset.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginPage:
                Intent moveLogin = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(moveLogin);
                fileList();
                break;
            case R.id.bt_resetpassword:
                ResetPassword();
                clear();
                break;
        }
    }

    private void ResetPassword() {
        final String email = inputEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email is required");
        } if (!email.matches(emailPattern)) {
            inputEmail.setError("Invalid email address");
        } else {
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingBar.setCancelable(false);
            loadingBar.setIndeterminate(false);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(),"Password reset link was sent your email address",Toast.LENGTH_SHORT).show();
                    } else {
                        loadingBar.dismiss();
                        Toast.makeText(getApplicationContext(), "Mail sending error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void clear(){
        inputEmail.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}
