package com.example.dutmaintenance;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DUTHomeActivity extends AppCompatActivity {

    //Elements

    private android.widget.Button btnAllIssue;
    private android.widget.Button btnPostIssue;

    //Firebase

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth=FirebaseAuth.getInstance();

        btnAllIssue=findViewById(R.id.btn_allIssue);
        btnPostIssue=findViewById(R.id.btn_PostIssue);

        //Redirect to AllIssueActivity
        btnAllIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),AllIssueActivity.class));

            }
        });

        //Redirect to PostIssueActivity
        btnPostIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),PostIssueActivity.class));

            }
        });

        ImageView img_slider =findViewById(R.id.img_slider);
        AnimationDrawable animationDrawable = (AnimationDrawable)  img_slider.getDrawable();
        animationDrawable.start();


    }

    public void openWebsite(View view) {
        String url = "https://www.dut.ac.za/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void openYoutube(View view) {
        String url = "https://www.youtube.com/user/DutCampusTv";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    public void OpenInsta(View view) {
        String url = "https://www.instagram.com/dut_official1/?hl=en";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        menu.findItem(R.id.action_sort).setVisible(false);
        menu.findItem(R.id.action_location).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
