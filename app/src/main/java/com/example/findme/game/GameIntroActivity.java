package com.example.findme.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findme.MainActivity;
import com.example.findme.R;
import com.example.findme.domain.model.MissingContainer;
import com.example.findme.post.missingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GameIntroActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    TextView back_button;
    TextView my_score;
    TextView world_record;
    TextView world_record_nickname;
    Spinner missing_spinner;
    Button start_game;

    String selected_name = "others";
    String selected_id = "";

    public String file_path;
    public boolean is_others;
    public String documentId;

    private ArrayList<MissingContainer> missingContainers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_intro);

        // Get res.
        back_button = findViewById(R.id.back);
        start_game = findViewById(R.id.start);
        my_score = findViewById(R.id.my_score);
        world_record = findViewById(R.id.world_score);
        world_record_nickname = findViewById(R.id.world_score_nickname);
        missing_spinner = findViewById(R.id.spinner);

        // Get informations from firebase.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("user");
        Query query1 = userRef.whereEqualTo("Uid", uid);
        Query query2 = userRef.orderBy("Score", Query.Direction.DESCENDING).limit(1);
        // Get my score.
        query1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                my_score.setText(String.valueOf(document.getData().get("Score")));
                            }
                        }
                        else {
                            my_score.setText("Cannot Load");
                        }
                    }
                });
        query2.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("query2", document.getId() + " => " +document.getData());
                                Map data = document.getData();
                                Log.d("gi", data.toString());
                                world_record.setText(String.valueOf(data.get("Score")));
                                world_record_nickname.setText(String.valueOf(data.get("NickName")));
                            }
                        }
                        else {
                            world_record.setText("Cannot Load");
                        }
                    }
                });

        // test missingContainer class
        getMissingPets();

        missing_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int check = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++check > 1) {
                    Toast toast = Toast.makeText(getBaseContext(), missingContainers.get(i).getName() + " selected", Toast.LENGTH_SHORT);
                    toast.show();
                    MissingContainer select = (MissingContainer) missing_spinner.getItemAtPosition(i);
                    selected_name = select.getName();
                    selected_id = select.getDocId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast toast = Toast.makeText(getBaseContext(), "Nothing was Select", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_name == "others") {
                    Toast toast = Toast.makeText(getBaseContext(), "Others game will be implemented. Please select pet.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    intent.putExtra("petName", selected_name);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getMissingPets(){
        Query query = db.collection("pet");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Firebase", document.getId() + " => " + document.getData());

                        String name = document.getString("name");
                        String imgPath = document.getString("repImg");
                        String docId = document.getId();
                        missingContainers.add(new MissingContainer(imgPath, name, docId));
                    }
                    missingContainers.add(new MissingContainer("images/white.png", "others", null));

                    // Adapt item Adapter for spinner
                    selected_name = missingContainers.get(0).getName();
                    selected_id = missingContainers.get(0).getDocId();
                    missingAdapter missingAdapter = new missingAdapter(getApplicationContext(), missingContainers);
                    missing_spinner.setAdapter(missingAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase", "Error getting documents: ", task.getException());
                }

            }
        });
    }
}