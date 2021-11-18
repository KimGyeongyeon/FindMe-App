package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    Button start_game;

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
                                world_record.setText(String.valueOf(data.get("Score")));
                                world_record_nickname.setText(String.valueOf(data.get("Nickname")));
                            }
                        }
                        else {
                            world_record.setText("Cannot Load");
                        }
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
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}