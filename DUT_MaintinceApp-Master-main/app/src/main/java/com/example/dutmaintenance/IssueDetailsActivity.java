package com.example.dutmaintenance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class IssueDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Elements
    private TextView mTitle;
    private TextView mLocation;
    private TextView mDescription;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    // Issue details
    private String mTitleText;
    private String mLocationText;
    private String mDescriptionText;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);

        getSupportActionBar().setTitle("Issue Details");

        // Get the issue details from the intent
        Intent intent = getIntent();
        mTitleText = intent.getStringExtra("title");
        mLocationText = intent.getStringExtra("location");
        mDescriptionText = intent.getStringExtra("description");
        mLatitude = intent.getDoubleExtra("latitude", 0);
        mLongitude = intent.getDoubleExtra("longitude", 0);

        // Initialize the elements
        mTitle = findViewById(R.id.issue_details_title);
        mLocation = findViewById(R.id.issue_details_location);
        mDescription = findViewById(R.id.issue_details_description);
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Set the issue details
        mTitle.setText(mTitleText);
        mLocation.setText(mLocationText);
        mDescription.setText(mDescriptionText);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng location = new LatLng(mLatitude, mLongitude);
        mGoogleMap.addMarker(new MarkerOptions().position(location).title(mTitleText));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Uri gmmIntentUri = Uri.parse("geo:" + mLatitude + "," + mLongitude + "?q=" + Uri.encode(mLocationText));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
