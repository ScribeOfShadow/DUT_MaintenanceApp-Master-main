package com.example.dutmaintenance;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.example.dutmaintenance.databinding.ActivityInsertIssuePostBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class InsertIssuePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    //get user location
    private EditText latitudeEditText;
    private Button getLocationButton;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_LOCATION_PERMISSION = 1;


    //Elements

    private EditText issue_title;
    private EditText issue_location;
    private EditText issue_description;

    private TextView issue_status;

    private Button btn_post_issue;

    //Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mIssuePost;

    private DatabaseReference mPublicDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_issue_post);

        getSupportActionBar().setTitle("Post your Issue");


        // firebase authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uId = mUser.getUid();

        mIssuePost = FirebaseDatabase.getInstance("https://dutmaintenance-d3b07-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Issue Post").child(uId);

        mPublicDatabase = FirebaseDatabase.getInstance("https://dutmaintenance-d3b07-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Public database");

        // Initialize views
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mImageView = findViewById(R.id.image_view);
        issue_title = findViewById(R.id.issue_title);
        issue_location = findViewById(R.id.issue_location);
        issue_description = findViewById(R.id.issue_description);
        btn_post_issue = findViewById(R.id.btn_issue_post);
        issue_status = findViewById(R.id.issue_status);
        latitudeEditText = findViewById(R.id.issue_location);
        getLocationButton = findViewById(R.id.get_location_button);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InsertIssuePostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InsertIssuePostActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                } else {
                    getLocation();
                }
            }
        });


        // Set onClickListener for mButtonChooseImage
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        // Set onClickListener for mButtonUpload
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtain an instance of Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a reference to the "images" folder in the default storage bucket
                StorageReference storageRef = storage.getReference().child("images");

                // Assign the reference to the mStorageRef variable
                mStorageRef = storageRef;
                if (mImageUri != null) {
                    // Get the file extension of the image
                    String fileExtension = getFileExtension(mImageUri);

                    // Create a new StorageReference with a unique name
                    StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + fileExtension);

                    // Upload the image to Firebase storage
                    fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get the download URL of the image
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Store the download URL in the database
                                            String downloadUrl = uri.toString();

                                            // Associate the image with the post
                                            String postTitle = issue_title.getText().toString();
                                            String postLocation = issue_location.getText().toString();
                                            String postDescription = issue_description.getText().toString();
                                            String postStatus = issue_status.getText().toString();


                                            DatabaseReference newPostRef = mIssuePost.push();
                                            newPostRef.child("title").setValue(postTitle);
                                            newPostRef.child("location").setValue(postLocation);
                                            newPostRef.child("description").setValue(postDescription);
                                            newPostRef.child("status").setValue(postStatus);
                                            newPostRef.child("image").setValue(downloadUrl);
                                            // Store the post in the public database
                                            DatabaseReference publicPostRef = mPublicDatabase.push();
                                            publicPostRef.child("title").setValue(postTitle);
                                            publicPostRef.child("location").setValue(postLocation);
                                            publicPostRef.child("description").setValue(postDescription);
                                            publicPostRef.child("status").setValue(postStatus);
                                            publicPostRef.child("image").setValue(downloadUrl);
                                            publicPostRef.child("userId").setValue(uId);


                                            Toast.makeText(InsertIssuePostActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                            finish();

                                            fileReference.putFile(mImageUri)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                                            // Show a push notification
                                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(InsertIssuePostActivity.this, "my_channel_id")
                                                                    .setSmallIcon(R.drawable.my_notification_icon)
                                                                    .setContentTitle("New Issue Posted")
                                                                    .setContentText(postTitle)
                                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(InsertIssuePostActivity.this);
                                                            if (ActivityCompat.checkSelfPermission(InsertIssuePostActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                                                // TODO: Consider calling
                                                                //    ActivityCompat#requestPermissions
                                                                // here to request the missing permissions, and then overriding
                                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                                //                                          int[] grantResults)
                                                                // to handle the case where the user grants the permission. See the documentation
                                                                // for ActivityCompat#requestPermissions for more details.
                                                                return;
                                                            }
                                                            notificationManager.notify(1, builder.build());
                                                        }
                                                    });


                                            //code ends here
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InsertIssuePostActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(InsertIssuePostActivity.this, "No file selected", Toast.LENGTH_LONG).show();
                }
            }
        });



        // Set onClickListener for btn_post_issue
        btn_post_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = issue_title.getText().toString();
                String location = issue_location.getText().toString();
                String description = issue_description.getText().toString();


                if (TextUtils.isEmpty(title)) {
                    issue_title.setError("Title is required");
                    return;
                }

                if (TextUtils.isEmpty(location)) {
                    issue_location.setError("Location is required");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    issue_description.setError("Description is required");
                    return;
                }

                mButtonUpload.performClick();
            }
        });
    }



    // Method to open file chooser
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Method to get the file extension of an image
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Method to handle the result of the file chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mImageView.setImageURI(mImageUri);
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String address = getAddress(latitude, longitude);
                    latitudeEditText.setText(address);
                } else {
                    Toast.makeText(InsertIssuePostActivity.this, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // use a larger value for maxResults
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);
            return addressLine;
        } else {
            return "Unknown";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



}