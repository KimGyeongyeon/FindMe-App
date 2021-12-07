package com.example.findme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.grpc.Context;

public class PostActivity extends Activity implements AdapterView.OnItemSelectedListener{

    Spinner missing_spinner;
    TextView back_button;
    CardView location_button;
    ConstraintLayout image_button;
    Button submit_button;
    ImageView camera_image;

    private boolean locationPermissionGranted;
    private static final String KEY_LOCATION = "location";
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private CollectionReference get;

    private static final String TAG = PostActivity.class.getSimpleName();
    String selected_name = "others";
    String selected_id = "";
//    LatLng location;

    public String file_path;
    public boolean is_others;
    public String documentId;

    private ArrayList<missingContainer> missingContainers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Image from camera activity.
        Intent intent = getIntent();
        Bitmap image = (Bitmap) intent.getExtras().get("bitmap");

        setContentView(R.layout.post);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();


        getLocationPermission();
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // test missingContainer class
        getMissingPets();

//        missingContainer test1 = new missingContainer("pet-info/Rudy/Rudy_3.PNG", "Rudy");
//        missingContainer test2 = new missingContainer("pet-info/Gold/gold_01_2.jpg", "Gold");
//        missingContainer test3 = new missingContainer("pet-info/milo/milo_4.PNG", "Milo");
//        missingContainer others = new missingContainer("images/white.png", "Others");
//        missingContainer missing[] = {test1, test2, test3, others};

        missing_spinner = findViewById(R.id.spinner);
        back_button = findViewById(R.id.back);
        location_button = findViewById(R.id.location_card);
        image_button = findViewById(R.id.camera_layout);
        submit_button = findViewById(R.id.submit_button);
        camera_image = findViewById(R.id.camera_image);

        missing_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++check > 1) {
                    Toast toast = Toast.makeText(getBaseContext(), missingContainers.get(i).name + " selected", Toast.LENGTH_SHORT);
                    toast.show();
                    missingContainer select = (missingContainer) missing_spinner.getItemAtPosition(i);
                    selected_name = select.name;
                    selected_id = select.docId;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast toast = Toast.makeText(getBaseContext(), "Nothing was Select", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        if (image != null) camera_image.setImageBitmap(image);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getBaseContext(), "submit", Toast.LENGTH_LONG);
                toast.show();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyy_MM_dd_HH_mm_ss", Locale.KOREA);
                Date now = new Date();
                String filename = formatter.format(now);
                StorageReference imagesRef;

                is_others = false;
                if(selected_name.equals("others")){
                    is_others = true;
                    file_path = "new/" + filename + ".jpg";
                } else {
                    file_path = "here/" + selected_name + "/" + filename + ".jpg";
                    documentId = selected_id;
                }
                imagesRef = storageReference.child(file_path);

                // send data.
                if (ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    getLocationPermission(); }
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(PostActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location loc) {

                                // Got last known location. In some rare situations this can be null.
                                if (loc != null) {

                                    Log.d("asdf", Double.toString(loc.getLatitude()));
//                                    Toast toast = Toast.makeText(getBaseContext(), "location success", Toast.LENGTH_LONG);
//                                    toast.show();

                                    Map<String, Object> info = new HashMap<>();
                                    info.put("date", now);
                                    info.put("img", file_path);
                                    info.put ("latLng", new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                                    info.put("userMail",currentUser.getEmail());
//                                    if(documentId == null) return;
                                    if (is_others) get = db.collection("others");
                                    else get = db.collection("pet").document(documentId).collection("here");
                                    get.add(info)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    UploadTask uploadTask = imagesRef.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast toast = Toast.makeText(getBaseContext(), "upload failure", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Toast toast = Toast.makeText(getBaseContext(), "upload success", Toast.LENGTH_LONG);
                                            toast.show();
//                        String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                        }
                                    });
                                }
                                else {
                                    Log.d("PetInfo-location", "location is null");
                                }
                            }
                        });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void getMissingPets(){
        Query query = FirebaseFirestore.getInstance().collection("pet");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        String name = document.getString("name");
                        String imgPath = document.getString("repImg");
                        String docId = document.getId();
                        missingContainers.add(new missingContainer(imgPath, name, docId));
                    }
                    missingContainers.add(new missingContainer("images/white.png", "others", null));

                    // Adapt item Adapter for spinner
                    selected_name = missingContainers.get(0).name;
                    selected_id = missingContainers.get(0).docId;
                    missingAdapter missingAdapter = new missingAdapter(getApplicationContext(), missingContainers);
                    missing_spinner.setAdapter(missingAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
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

    // implement AdapterView.OnItemSelectedListener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

}