package com.example.dutmaintenance;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    //Elements

    private android.widget.EditText email;
    private android.widget.EditText password;

    private android.widget.Button btnLogin;
    private android.widget.Button btnRegistration;

    //Firebase

    private FirebaseAuth mAuth;

    //Progress Dialog

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // firebase authentication
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            startActivity(new Intent(getApplicationContext(), DUTHomeActivity.class));

        }

        mDialog = new ProgressDialog(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel_id", "My Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Channel Description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        LoginFunction();

    }

    // login authentication using database and implementation of error checks
    // login authentication using database and implementation of error checks
    private void LoginFunction() {

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.login_password);

        btnLogin = findViewById(R.id.btn_login);
        btnRegistration = findViewById(R.id.btn_reg);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mEmail = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("*Required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    password.setError("*Required");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                // Check if the email and password match the predefined admin email and password
                if (mEmail.equals("admin@gmail.com") && pass.equals("admin")) {
                    Toast.makeText(getApplicationContext(), "Admin Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                    mDialog.dismiss();
                } else {
                    // Error checking, Error dialog, Redirect to HomeActivity on successful verification
                    mAuth.signInWithEmailAndPassword(mEmail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "LogIn Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DUTHomeActivity.class));

                                mDialog.dismiss();

                            } else {
                                Toast.makeText(getApplicationContext(), "Login Unsuccessful", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }

                        }
                    });
                }

            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), DUTRegistrationActivity.class));

            }
        });

    }
}
