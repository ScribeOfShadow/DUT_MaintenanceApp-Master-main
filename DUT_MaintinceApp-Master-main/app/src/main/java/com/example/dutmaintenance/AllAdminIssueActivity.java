package com.example.dutmaintenance;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AllAdminIssueActivity extends AppCompatActivity {

    //Recycler

    private RecyclerView recyclerView;
    private static AllAdminIssueActivity mContext;

    //Firebase

    private static DatabaseReference mAllIssuePost;
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




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alladmin_issue);

        mContext = AllAdminIssueActivity.this;

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

        recyclerView = findViewById(R.id.recycler_alladmin_issue);


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

    // Sort procedures for alladminissuepost activity
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
                holder.setIssueStatus("PENDING");
                String postKey = getRef(position).getKey();
                holder.setDeleteButton(postKey);
                holder.setChangeStatusButton(postKey);





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

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alladminissuepost, parent, false);
                return new AllIssuePostViewHolder(view);


            }

        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    // AllIssuePostViewHolder displays FirebaseRecyclerAdapter items from activity_all_issue recyclerview on allissuepost activity CardViews
    public class AllIssuePostViewHolder extends RecyclerView.ViewHolder {

        View myview;


        public void setChangeStatusButton(final String postKey) {
            Button changeStatusButton = itemView.findViewById(R.id.mark_fixed_button);
            final TextView statusTextView = itemView.findViewById(R.id.issue_status);
            final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Public database").child(postKey);

            // Add a listener to the database reference to update the status TextView when the value changes
            postRef.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String status = snapshot.getValue(String.class);
                        statusTextView.setText(status);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Error getting status value", error.toException());
                }
            });

            changeStatusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Change status value from "pending" to "completed"
                    String newStatus = "ISSUE FIXED";

                    // Update value in Firebase database for the specific post
                    postRef.child("status").setValue(newStatus);

                    // Update the TextView to display the new status value
                    statusTextView.setText(newStatus);
                }
            });
        }

        public void setDeleteButton(final String postKey) {
            Button deleteButton = myview.findViewById(R.id.delete_post_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Public database").child(postKey);
                    postRef.removeValue();
                }
            });
        }


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