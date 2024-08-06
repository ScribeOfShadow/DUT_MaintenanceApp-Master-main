package com.example.dutmaintenance;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class DUTRegistrationActivity extends AppCompatActivity {

    private android.widget.EditText emailReg;
    private android.widget.EditText passReg;

    private android.widget.Button btnReg;
    private android.widget.Button btnLogin;

    //Firebase Auth

    private FirebaseAuth mAuth;

    //Progress Dialog

    private ProgressDialog mDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dut_registration);

        //Firebase initialization
        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);

        Registration();

    }

    //User Registration Function
    private void Registration(){

        emailReg=findViewById(R.id.email_registration);
        passReg=findViewById(R.id.registration_password);

        btnReg=findViewById(R.id.btn_registration);
        btnLogin=findViewById(R.id.btn_login);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=emailReg.getText().toString().trim();
                String pass=passReg.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    emailReg.setError("*Required Field"); //Error Check
                    return;

                }

                if (TextUtils.isEmpty(pass)){
                    passReg.setError("*Required Field"); //Error Check
                    return;

                }

                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //If registration is successful, a new database table will be created in FirebaseHostConsole
                        if (task.isSuccessful()){

                            Toast.makeText(getApplicationContext(), "Account Registered Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),DUTHomeActivity.class));

                            mDialog.dismiss();

                            //If registration is unsuccessful, fields will be reset and user will be required to try again
                        }else {
                            Toast.makeText(getApplicationContext(), "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                            emailReg.getText().clear();
                            passReg.getText().clear();
                            mDialog.dismiss();
                        }


                    }
                });

            }
        });

        //Redirect to login page via btnLogin
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });

    }

}