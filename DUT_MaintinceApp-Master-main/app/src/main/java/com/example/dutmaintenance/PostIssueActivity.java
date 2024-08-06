package com.example.dutmaintenance;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostIssueActivity extends AppCompatActivity {

    //Elements

    private com.google.android.material.floatingactionbutton.FloatingActionButton fabBtn;

    //Recycler View

    private RecyclerView recyclerView;

    //Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference IssuePostDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_issue);

        getSupportActionBar().setTitle("My Issues");

        fabBtn=findViewById(R.id.fab_add);

        mAuth=FirebaseAuth.getInstance();

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uId=mUser.getUid();

        IssuePostDatabase= FirebaseDatabase.getInstance("https://dutmaintenance-d3b07-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Issue Post").child(uId);

        recyclerView=findViewById(R.id.recycler_Issue_post_id);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), InsertIssuePostActivity.class));

            }
        });

    }

    //FirebaseRecyclerOptions initialization
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> firebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(IssuePostDatabase,Data.class)
                        .build();

        //FirebaseRecyclerAdapter used to store Database items in local recyclerview located on issue_post_item
        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {

                holder.setJobTitle(model.getTitle());
                holder.setJobLocation(model.getLocation());
                holder.setJobDescription(model.getDescription());


            }

            //onCreateViewHolder used to display items from RecyclerAdapter in local recyclerview located on job_post_item
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issue_post_item, parent, false);
                return new MyViewHolder(view);

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    //Initialization of variables housed in Data to be stored in 'MyViewHolder' viewholder for use in FireBaseRecyclerAdapter
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        //myView initialization
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }


        //JobTitle (Issue name) initialization
        public void setJobTitle(String title) {

            TextView mTitle = myView.findViewById(R.id.Issue_title);
            mTitle.setText(title);

        }

        //JobArea (location) initialization
        public void setJobLocation(String location) {

            TextView mArea = myView.findViewById(R.id.Issue_location);
            mArea.setText(location);

        }

        //JobDescription initialization
        public void setJobDescription(String description) {

            TextView mDescription = myView.findViewById(R.id.Issue_description);
            mDescription.setText(description);

        }



    }
}