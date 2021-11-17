package com.example.findme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PetInfoActivity extends AppCompatActivity {


    private ArrayList<ImageView> images;
    private boolean locationPermissionGranted;
    private static final String KEY_LOCATION = "location";
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Firebase variable
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ArrayList<PetInfo> petArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PetInfoActivity-fire","Updated 20:23");
        super.onCreate(savedInstanceState);

        // 1) View setting
        setContentView(R.layout.petinfo);
        final Boolean[] first_click = {true};
        final TextView name = findViewById(R.id.pet_name_here);
        final TextView date = findViewById(R.id.pet_date_here);
        final TextView detail = findViewById(R.id.pet_detail_here);
        images = new ArrayList<>();
        images.add(findViewById(R.id.imageView_1));
        images.add(findViewById(R.id.imageView_2));
        images.add(findViewById(R.id.imageView_3));
        images.add(findViewById(R.id.imageView_4));

        // 2) Initialize Firebase-related variables
        petArray = new ArrayList<>(); //Include Every Pet information
        database = FirebaseDatabase.getInstance(); // Connect FireBase Here
        ref = database.getReference("pet");// Connect DB table

// Ver 2. Guide suggested
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //PetInfo object 형태로 값 저장
                    Log.d("PetInfoActivity", "sibal");
                    PetInfo info = dataSnapshot.getValue(PetInfo.class);
                    petArray.add(info);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PetInfoActivity-74", String.valueOf(error.toException()));
            }
        };
        ref.addValueEventListener(postListener);

// ver 1. Use ref directly
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                petArray.clear();
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Log.d("sibal", "sibal");
//                    //PetInfo object 형태로 값 저장
//                    PetInfo info = dataSnapshot.getValue(PetInfo.class);
//                    petArray.add(info);
//                }
//                Log.d("PetInfoActivity-69", String.format("petArray size : %d",petArray.size()));
//                PetInfoActivity.this.notify();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("PetInfoActivity-74", String.valueOf(error.toException()));
//            }
//        });


        // 3) Distribute the info to view
        // 요청한 pet의 정보를 찾고 그에 해당하는 index를 설정하는 방식으로 변경하자.

        if(petArray.size()>0){
            int num = 0;
            for(ImageView i : images) {
                Glide.with(i).load(petArray.get(0).getImage().get(num))
                        .into(i);
                num++;
            }
            name.setText(petArray.get(0).getName());
            date.setText(petArray.get(0).getDate());
            detail.setText(petArray.get(0).getProfile());
        }else{
            Log.d("PetInfo-111",
                    String.format("petArray size : %d",petArray.size()));

            // Use sample data
            PetInfo sample = new PetInfo();
            sample = sample.sample_data();
            int num = 0;
            for(ImageView i : images) {
                Glide.with(i).load(sample.getImage().get(num))
                        .into(i);
                num++;
            }
            name.setText(sample.getName());
            date.setText(sample.getDate());
            detail.setText(sample.getProfile());

        }






        /*
         *
         *   a ) Click Event
         *
         */


        final Button main_button = findViewById(R.id.main_bar);
        final Button not_here_button = findViewById(R.id.not_here_button);

        //Main Button Event

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
        if(savedInstanceState != null){
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        not_here_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(first_click[0]){
                    first_click[0] = false;
                    // Send report and give toast.
                    // divide it into 2 functions.
                    Toast toast = Toast.makeText(getBaseContext(), "Success! Thank you for participation.", Toast.LENGTH_LONG);
                    toast.show();

                    //현재위치 double[], weight=5를 보낸다.

                    report_not_here();
                }
                else{
                    Toast toast = Toast.makeText(getBaseContext(), "Please check location of the pet.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }
    // [END OnCreate]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_LOCATION, lastKnownLocation);
    }

    /*
     *
     *   Not Here support methods
     *
     */

    public void report_not_here() {
        database = FirebaseDatabase.getInstance(); // Connect FireBase Here
        DatabaseReference ref;
        ref = database.getReference("notHere");
        NotHere report = new NotHere();

        // 1) 현재 위치 report에 저장하기

        final Location[] ret_value = new Location[1]; //we will save return value here
        ret_value[0] = new Location("LocationManager#GPS_PROVIDER");
        try {
            if (locationPermissionGranted) {


                //Get location
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.

                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.d("PetInfo-location","Got recent location");
                                ret_value[0] = new Location(lastKnownLocation);
                            }
                        } else {
                            Log.d("PetInfo-location", "Current location is null. Using defaults.");
                            ret_value[0].setLatitude(36.181818);
                            ret_value[0].setLongitude(127.351818);
                        }
                    }
                });


            }
            else{
                Log.d("PetInfo-location", "Location permission denied");
                ret_value[0] = new Location("LocationManager#GPS_PROVIDER");
                ret_value[0].setLatitude(36.181818);
                ret_value[0].setLongitude(127.351818);

            }

        } catch (SecurityException e)  {
            Log.e("PetInfo-location: %s", e.getMessage(), e);
        }


        report.setLocation(ret_value[0]);



        // 2) weight 설정하기
        report.setWeight(5);

        // 3) 문서 이름 정하기
        Date cur_time = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_kkmmss");
        String docs_name = format.format(cur_time);
//        String docs_name = "kzcdO4y49mLKaeiaVWmM";

        Log.d("PetInfoActivity", "send report to firebase "+docs_name);
        ref.child(docs_name).setValue(report);
    }

//
//    private Location current_location() {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        final Location[] ret_value = new Location[1]; //we will save return value here
//        ret_value[0] = new Location("LocationManager#GPS_PROVIDER");
//        try {
//            if (locationPermissionGranted) {
//
//
//                //Get location
//                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//
//                            lastKnownLocation = task.getResult();
//                            if (lastKnownLocation != null) {
//                                Log.d("PetInfo-location","Got recent location");
//                                ret_value[0] = new Location(lastKnownLocation);
//                            }
//                        } else {
//                            Log.d("PetInfo-location", "Current location is null. Using defaults.");
//                            ret_value[0].setLatitude(36.181818);
//                            ret_value[0].setLongitude(127.351818);
//                        }
//                    }
//                });
//
//
//            }
//            else{
//                Log.d("PetInfo-location", "Location permission denied");
//                ret_value[0] = new Location("LocationManager#GPS_PROVIDER");
//                ret_value[0].setLatitude(36.181818);
//                ret_value[0].setLongitude(127.351818);
//
//            }
//
//        } catch (SecurityException e)  {
//            Log.e("PetInfo-location: %s", e.getMessage(), e);
//        }
//
//        return ret_value[0];
//    }

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