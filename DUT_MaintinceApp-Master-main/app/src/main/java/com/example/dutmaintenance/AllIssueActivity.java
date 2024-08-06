package com.example.dutmaintenance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AllIssueActivity extends AppCompatActivity {

    //Recycler

    private RecyclerView recyclerView;
    private static AllIssueActivity mContext;

    //Firebase

    private DatabaseReference mAllIssuePost;
    private DatabaseReference favouriteref, fvrtref, fvrt_listref;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    //Sort

    LinearLayoutManager mLayoutManager;
    SharedPreferences mSharedPref;

    //Dialog

    private ProgressDialog mDialog;

    //Elements

    ImageButton fvrt_btn;
    Boolean fvrtChecker = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_issue);


        mContext = AllIssueActivity.this;


        getSupportActionBar().setTitle("All Issues");


        //Date Sort Function
        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest");


        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }

        //Database initialization

        mAllIssuePost = FirebaseDatabase.getInstance("https://dutmaintenance-d3b07-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Public database");
        mAllIssuePost.keepSynced(true);

        recyclerView = findViewById(R.id.recycler_all_issue);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        //Hide logout
        menu.findItem(R.id.logout).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    // Sort procedures for allissuepost activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int sortid = item.getItemId();

        // Sort by date
        if (sortid == R.id.action_sort) {
            //Display alert dialog to choose sorting
            showSortDialog();
            return true;

        }
        // Sort by location
        else if (sortid == R.id.action_location) {
            //Display alert dialog to choose sorting
            showLocationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        //options to display in dialog
        String[] sortOptions = {"Newest", "Oldest"};
        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by") //set title
                .setIcon(R.drawable.sort) //set icon
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            //sort by newest
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Sort", "newest"); //where 'Sort' is key & 'newest' is value
                            editor.apply(); //save in shared preferences
                            recreate(); //restart activity to take effect

                        } else if (i == 1) {
                            {
                                //sort by oldest
                                SharedPreferences.Editor editor = mSharedPref.edit();
                                editor.putString("Sort", "oldest"); //where 'Sort' is key & 'oldest' is value
                                editor.apply(); //save in shared preferences
                                recreate(); //restart activity to take effect

                            }
                        }

                    }
                });
        builder.show();
    }

    // Dialog Box requesting access to devices location services
    private void showLocationDialog() {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);

        builder.setMessage("Allow 'DUT Maintenance Portal App' to access Google's location service?");
        builder.setTitle("Enable Location");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            // If permission is granted
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences.Editor editor = mSharedPref.edit();
                Query firebaseSearchQuery = mAllIssuePost.orderByChild("area").startAt("Durban North").endAt("Pinetown"); //where 'area' is key & location is our parameter
                editor.apply(); //save in shared preferences
                recreate(); //restart activity to take effect

            }
        });
        builder.setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
            // If permission is denied
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //FirebaseRecyclerOptions to initialize FirebaseRecyclerAdapter
        FirebaseRecyclerOptions<Data> firebaseRecyclerOptions =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mAllIssuePost, Data.class)
                        .build();

        //FirebaseRecyclerAdapter to store items from mAllIssuePost (public) database
        FirebaseRecyclerAdapter<Data, AllIssuePostViewHolder> adapter = new FirebaseRecyclerAdapter<Data, AllIssuePostViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull AllIssuePostViewHolder holder, int position, @NonNull Data model) {

                holder.setIssueTitle(model.getTitle());
                holder.setIssueLocation(model.getLocation());
                holder.setIssueDescription(model.getDescription());
                holder.setIssueImage(model.getImage());
                holder.setIssueStatus(model.getStatus());





                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), IssueDetailsActivity.class);


                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("location", model.getLocation());
                        intent.putExtra("description", model.getDescription());

                        startActivity(intent);

                    }

                });

            }

            @NonNull
            @Override
            public AllIssuePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allissuepost, parent, false);
                return new AllIssuePostViewHolder(view);




            }



        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    // AllIssuePostViewHolder displays FirebaseRecyclerAdapter items from activity_all_issue recyclerview on alljobpost activity CardViews
    public static class AllIssuePostViewHolder extends RecyclerView.ViewHolder {

        View myview;



        public AllIssuePostViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setIssueTitle(String title) {

            TextView mTitle = myview.findViewById(R.id.all_issue_post_title);
            mTitle.setText(title);

        }


        public void setIssueLocation(String location) {

            TextView mLocation = myview.findViewById(R.id.all_issue_post_location);
            mLocation.setText(location);

        }


        public void setIssueDescription(String description) {

            TextView mDescription = myview.findViewById(R.id.all_issue_post_description);
            mDescription.setText(description);

        }


        public void setIssueImage(String image) {

            ImageView mImage = myview.findViewById(R.id.issue_image_real);
            Glide.with(myview.getContext())
                    .load(image)
                    .into(mImage);

        }

        public void setIssueStatus(String status) {

            TextView mStatus = myview.findViewById(R.id.issue_status);
            mStatus.setText(status);

        }


    }
}