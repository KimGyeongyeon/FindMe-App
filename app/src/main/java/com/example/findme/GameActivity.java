package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class GameActivity extends AppCompatActivity {

    // Game
    List<String> compares;
    List<Boolean> correctness;
    String pet_name_;

    // etc.
    private long backKeyPressedTime  = 0;
    private Toast toast;

    // Res
    ProgressBar bar;
    TextView count;
    TextView pet_name;
    ImageView original;
    ImageView compare;
    ImageButton correct;
    ImageButton wrong;

    // Firebase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String uid;
    private FirebaseFirestore db;
    private CollectionReference petInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        // Get res.
        bar = findViewById(R.id.progressBar);
        count = findViewById(R.id.count);
        pet_name = findViewById(R.id.pet_name);
        original = findViewById(R.id.original_image);
        compare = findViewById(R.id.compare_image);
        correct = findViewById(R.id.correct_button);
        wrong = findViewById(R.id.wrong_button);

        // Get firebase information, get original image and compare image lists.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        petInfoRef = db.collection("pet");
        Query query1 = petInfoRef.whereEqualTo("name", "milo");
        query1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase", document.getId() + " => " + document.getData());
                                List<String> uris = (List<String>) document.get("img");
                                game_prepare(uris);
                            }
                        }
                        else {
                            Log.d("Firebase", "fail");
                        }
                    }
                });


    }
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(getBaseContext(), "Press back again to quit game. (Doesn't saved)", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), GameIntroActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void game_prepare(List<String> uris) {
        // Set pet name.
        pet_name_ = "milo";
        pet_name.setText("Is this below " + pet_name_ + "?");

        // Set remain counts.
        count.setText("1");
        bar.setProgress(10);
        Log.d("uri check", uris.get(0));
        Glide.with(this).load(uris.get(0)).into(original);
    }
}