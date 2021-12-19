package com.example.findme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class PetInfoMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    // [END maps_current_place_state_keys]

    public String petId;

    public boolean isRun = false;

    // [START maps_current_place_on_create]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");


        // [START_EXCLUDE silent]
        // [START maps_current_place_on_create_save_instance_state]
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // [END maps_current_place_on_create_save_instance_state]
        // [END_EXCLUDE]

        // Retrieve the content view that renders the map.
        setContentView(R.layout.petinfomap);


        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        // [START maps_current_place_map_fragment]
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.petReportsMap);
        mapFragment.getMapAsync(this);

        // [END maps_current_place_map_fragment]
    }
    // [END maps_current_place_on_create]

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        /* 스레드 실행 */
//        BackgroundThread thread = new BackgroundThread();
//        isRun = true;
//        thread.start();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        /* 스레드 중지 */
//        isRun = false;
////        value = 0; //value값 다시 0으로 초기화
//    }
//
//    class BackgroundThread extends Thread{
//
//        @Override
//        public void run() {
//            while(isRun){ // isRun 변수가 true가 되면
//                try{
//                    Thread.sleep(1000); //1초 지연
////                    value++; //value 값 1 증가
//                    Log.d("nothereupdate", "asdf");
//                    updateNotHereReports();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            try{
//                this.join(); // 스레드를 메인 스레드와 합치기
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//        /* isRun이 true인 동안은 계속해서 1초마다 value 값을 1씩 증가시킨다.
//         * 그러다가 isRun이 false가 되면 스레드가 메인 스레드와 합쳐지고 끝이난다.*/
//
//    }


    private void getNotHereReportsFromFirebase () {
        Query query = FirebaseFirestore.getInstance().collection("pet").document(petId).collection("notHere");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Collection<WeightedLatLng> weightedLatLngs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        LatLng latLng = new LatLng(document.getGeoPoint("latLng").getLatitude(), document.getGeoPoint("latLng").getLongitude());
                        double weight = document.getDouble("weight");
                        weightedLatLngs.add(new WeightedLatLng(latLng, weight));
                    }

                    // Create the gradient.
                    int[] colors = {
                            Color.rgb(102, 225, 0), // green
                            Color.rgb(255, 0, 0)    // red
                    };
                    float[] startPoints = {
                            0.2f, 1f
                    };
                    Gradient gradient = new Gradient(colors, startPoints);

                    HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
//                .data(latLngs)
                            .weightedData(weightedLatLngs)
                            .radius(50) //기본값 20, 10< <50
//                .gradient(gradient)
                            .build();

                    // Add a tile overlay to the map, using the heat map tile provider.
                    TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }
            }
        });

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) { return; }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            addHeatMap();
                            break;
                        case MODIFIED:
//                            addHeatMap();
                            break;
                        case REMOVED:
//                            addHeatMap();
                            break;
                    }
                }
            }
        });
    }

    void updateNotHereReports(){
        Query query = FirebaseFirestore.getInstance().collection("pet").document("ARSoHu4Dbtn7fMpsgaWk").collection("notHere");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Date date = document.getDate("date");
                        Date cur_date = new Date();

                        long mins = (cur_date.getTime() - date.getTime())/(60*1000);
                        Log.i("nothereupdate", String.valueOf(mins));

                        if(mins > 20){
                            document.getReference().update("weight", 0);
                            Log.d("nothereupdate", "weight to 0");

                        }
                        else if(mins > 10) {
                            document.getReference().update("weight", 3);
                            Log.d("nothereupdate", "weight to 3");
                        }

                        Log.d("notHereUpdate", document.getId() + " => " + document.getData());
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }

            }
        });
    }

    private void addHeatMap(){
        Query query = FirebaseFirestore.getInstance().collection("pet").document(petId).collection("notHere");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Collection<WeightedLatLng> weightedLatLngs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        LatLng latLng = new LatLng(document.getGeoPoint("latLng").getLatitude(), document.getGeoPoint("latLng").getLongitude());
                        double weight = document.getDouble("weight");
                        weightedLatLngs.add(new WeightedLatLng(latLng, weight));
                    }

                    // Create the gradient.
                    int[] colors = {
                            Color.rgb(102, 225, 0), // green
                            Color.rgb(255, 0, 0)    // red
                    };
                    float[] startPoints = {
                            0.2f, 1f
                    };
                    Gradient gradient = new Gradient(colors, startPoints);

                    HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
//                .data(latLngs)
                            .weightedData(weightedLatLngs)
                            .radius(50) //기본값 20, 10< <50
//                .gradient(gradient)
                            .build();

                    // Add a tile overlay to the map, using the heat map tile provider.
                    TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getHereReportsFromFirebase () {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = FirebaseFirestore.getInstance().collection("pet").document(petId).collection("here");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        LatLng latLng = new LatLng(document.getGeoPoint("latLng").getLatitude(), document.getGeoPoint("latLng").getLongitude());
                        Date date = document.getDate("date");
                        String img = document.getString("img");

                        Marker hereMarker = map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date).toString())
                        );
                        hereMarker.setTag(document.getId());
//                        hereMarker.showInfoWindow();
                    }

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            //Using position get Value from arraylist
                            showBottomSheetDialog((String) marker.getTag());
                            return false;
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }
            }
        });

        //수정 시 업데이트
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) { return; }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
//                            adapter.notifyDataSetChanged();
                            addMarker();
                            break;
                        case MODIFIED:
//                            addMarker();
                            break;
                        case REMOVED:
//                            addMarker();
                            break;
                    }
                }
            }
        });
    }

    private void addMarker(){
        Query query = FirebaseFirestore.getInstance().collection("pet").document(petId).collection("here");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        LatLng latLng = new LatLng(document.getGeoPoint("latLng").getLatitude(), document.getGeoPoint("latLng").getLongitude());
                        Date date = document.getDate("date");
                        String img = document.getString("img");

                        Marker hereMarker = map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date).toString())
                        );
                        hereMarker.setTag(document.getId());
//                        hereMarker.showInfoWindow();
                    }

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            //Using position get Value from arraylist
                            showBottomSheetDialog((String) marker.getTag());
                            return false;
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException()
                    );
                }
            }
        });
    }


    private void showBottomSheetDialog(String docId){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.here_report_bottom_sheet_dialog_layout);

        LinearLayout download = bottomSheetDialog.findViewById(R.id.here_report_info);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.here_report_view_img);
        TextView textViewId = bottomSheetDialog.findViewById(R.id.here_report_view_id);
        TextView textViewDate = bottomSheetDialog.findViewById(R.id.here_report_view_date);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pet").document(petId).collection("here").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getId().equals(docId)){

                            ///여기에서 가져오기!!!!
                            String imgPath = document.getString("img");
                            Date date = document.getDate("date");
                            String userMail = document.getString("userMail");
                            String[] temp = userMail.split("@");
                            String userId = temp[0];

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            StorageReference photoReference = storageReference.child(imgPath);
                            photoReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageView.setImageBitmap(bitmap);
                                    textViewId.setText("by " + temp[0]);
                                    textViewDate.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(date).toString() + "\n" + DateFormat.getTimeInstance(DateFormat.SHORT).format(date).toString());
                                    bottomSheetDialog.show();
                                }
                            });

//                            Glide.with(getApplicationContext()).asBitmap().load(photoReference).into(imageView);
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }
            }
        });


        Button btn = bottomSheetDialog.findViewById(R.id.see_more_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PetInfoMapActivity.this, ShowHereReportsActivity.class);
                intent.putExtra("petId", petId);
                startActivity(intent);
            }
        });

    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    // [START maps_current_place_on_save_instance_state]
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    // [END maps_current_place_on_save_instance_state]

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    // [START maps_current_place_on_map_ready]
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }
    // [END maps_current_place_on_map_ready]

    private void getFirebaseDatas(){
        getNotHereReportsFromFirebase();
        getHereReportsFromFirebase();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    // [START maps_current_place_get_device_location]
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                getFirebaseDatas();

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    /**
     * Prompts the user for permission to use the device location.
     */
    // [START maps_current_place_location_permission]
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
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [END maps_current_place_location_permission]

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    // [END maps_current_place_on_request_permissions_result]


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // [END maps_current_place_update_location_ui]
}

