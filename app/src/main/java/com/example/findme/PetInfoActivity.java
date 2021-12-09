package com.example.findme;

import android.content.Context;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PetInfoActivity extends AppCompatActivity {


    private ArrayList<ImageView> images;
    private boolean locationPermissionGranted;
    private static final String KEY_LOCATION = "location";
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String email;

    public String petId;
    public TextView petName;
    public TextView petDate;
    public TextView petProfile;
    public ImageView petImageView1;
    public ImageView petImageView2;
    public ImageView petImageView3;
    public ImageView petImageView4;

//    private ArrayList<PetInfo> petArray;

    // Firebase variable
    private DatabaseReference ref;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PetInfoActivity-fire", "Update 16:48");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        email = currentUser.getEmail();

        // 1) View setting
        setContentView(R.layout.petinfo);
        final Boolean[] first_click = {true};
        petName = findViewById(R.id.pet_name_here);
        petDate = findViewById(R.id.pet_date_here);
        petProfile = findViewById(R.id.pet_detail_here);
        petImageView1 = findViewById(R.id.imageView_1);
        petImageView2 = findViewById(R.id.imageView_2);
        petImageView3 = findViewById(R.id.imageView_3);
        petImageView4 = findViewById(R.id.imageView_4);


        getFirebaseData();

        //Main Button Event

        final Button main_button = findViewById(R.id.main_bar);
//        final Button here_button = findViewById(R.id.here_button);
        final Button not_here_button = findViewById(R.id.not_here_button);
        final ImageView map_preview_image = findViewById(R.id.map_preview);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Not Here event
        getLocationPermission();
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        not_here_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (first_click[0]) {
                    first_click[0] = false;
                    // Send report and give toast.
                    // divide it into 2 functions.
                    Toast toast = Toast.makeText(getBaseContext(), "Success! Thank you for participation.", Toast.LENGTH_LONG);
                    toast.show();

                    //서버로 위치 전송
                    report_not_here();

                } else {
                    Toast toast = Toast.makeText(getBaseContext(), "Please check location of the pet.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        map_preview_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to PetInfoMapActivity
                Intent intent = new Intent(getBaseContext(), PetInfoMapActivity.class);
                intent.putExtra("petId", petId);
                startActivity(intent);
            }
        });

    }


    void getFirebaseData(){
        Query query = FirebaseFirestore.getInstance().collection("pet");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        if(document.getId().equals(petId)){
                            String name = document.getString("name");
                            String profile = document.getString("profile");
                            List<String> imgPaths = (List<String>) document.get("img");
                            Date date = document.getDate("date");

                            petName.setText(name);
                            petDate.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date));
                            petProfile.setText(profile);
                            Glide.with(getApplicationContext()).load(imgPaths.get(0)).into(petImageView1);
                            Glide.with(getApplicationContext()).load(imgPaths.get(1)).into(petImageView2);
                            Glide.with(getApplicationContext()).load(imgPaths.get(2)).into(petImageView3);
                            Glide.with(getApplicationContext()).load(imgPaths.get(3)).into(petImageView4);

                        }
                    }
                } else {
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LOCATION, lastKnownLocation);
    }

    public void report_not_here() {
        Log.d("PetInfo-location", "Check permission");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission(); }

        // 1) 현재 위치 report에 저장하기

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
//                             2) weight 설정하기
                                    GeoPoint geo_location = new GeoPoint(location.getLatitude(), location.getLongitude());


                                    // 3) 문서 이름 정하기
                                    Date cur_time = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_kkmmss");

                                    NotHere report = new NotHere(email, geo_location,5, cur_time);

                                    String docs_name = format.format(cur_time);

                                    Log.d("PetInfoActivity", "send report to firebase " + docs_name);

                                    // 4) Firebase로 전송
                                    database = FirebaseFirestore.getInstance(); // Connect FireBase Here
                                    //ref = database.getReference("findme-a2f27");
                                    database.collection("pet").document(petId).collection("notHere").document(docs_name).set(report);
                        }
                        else {
                            Log.d("PetInfo-location", "location is null");
                        }
                    }
                });


    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
}