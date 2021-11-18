package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    // Game
    List<String> pet_imgs = new ArrayList<>();
    List<String> game_imgs = new ArrayList<>();
    List<String> compares = new ArrayList<>();
    List<Boolean> correctness = new ArrayList<>();
    String pet_name_;

    // etc.
    private long backKeyPressedTime  = 0;
    private Toast toast;

    // Res
    ProgressBar bar;
    TextView count;
    TextView result;
    Button return_button;
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
    private CollectionReference userRef;
    private CollectionReference petInfoRef;
    private CollectionReference gameRef;
    private FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference pathReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        // Get res.
        bar = findViewById(R.id.progressBar);
        count = findViewById(R.id.count);
        result = findViewById(R.id.result);
        return_button = findViewById(R.id.return_button);
        pet_name = findViewById(R.id.pet_name);
        original = findViewById(R.id.original_image);
        compare = findViewById(R.id.compare_image);
        correct = findViewById(R.id.correct_button);
        wrong = findViewById(R.id.wrong_button);

        result.setVisibility(View.INVISIBLE);
        return_button.setVisibility(View.INVISIBLE);
        clickable(false);

        // Get firebase information, get original image and compare image lists.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        petInfoRef = db.collection("pet");
        gameRef = db.collection("game");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Query query1 = petInfoRef.whereEqualTo("name", "Gold");
        query1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase1", document.getId() + " => " + document.getData());
                                pet_name_ = (String) document.get("name");
                                pet_imgs = (List<String>) document.get("img");

                                // Set original.
                                Glide.with(original).load(pet_imgs.get(0)).into(original);

                                // Set pet name.
                                pet_name.setText("Is this below " + pet_name_ + "?");

                                // Second query
                                gameRef = db.collection("pet").document(document.getId()).collection("here");

                                gameRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                pathReference = storageReference.child((String) document.get("img"));
                                                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Log.d("uri", String.valueOf(uri));
                                                        game_imgs.add(String.valueOf(uri));
                                                    }
                                                });
                                            }
                                            Log.d("Firebase2", game_imgs.toString());
                                            // Set compare List
                                            compares = game_imgs;
                                            compares.add(pet_imgs.get(1));
                                            compares.add(pet_imgs.get(2));
                                            compares.add(pet_imgs.get(3));
                                            game_start();
                                        } else {
                                            Log.d("Firebase2", "download here image fail");
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            Log.d("Firebase", "download pet fail");
                        }
                    }
                });
        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickable(false);
                correctness.add(true);
                game_start();
            }
        });
        wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickable(false);
                correctness.add(false);
                game_start();
            }
        });
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameIntroActivity.class);
                startActivity(intent);
                finish();
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

    private void game_start() {

        int size = correctness.size();

        if (size >= 10) game_end();
        else {
            // Set remain counts.
            count.setText(Integer.toString(size+1));
            bar.setProgress((size+1)*10);

            // Set compare img
            Log.d("Load Image", compares.get(size));
            Glide.with(compare).load(compares.get(size)).into(compare);

            clickable(true);
        }
    }

    private void game_end() {

        // Hide images.
        compare.setVisibility(View.INVISIBLE);
        original.setVisibility(View.INVISIBLE);
        pet_name.setText("");

        // Show result
        result.setText(getEmojiByUnicode(0x1F60A) + "Congratulation!" + getEmojiByUnicode(0x1F60A));
        result.setVisibility(View.VISIBLE);

        // Take result depend on QC.
        int score = 0;
        if (correctness.get(0)) score++;
        if (correctness.get(1)) score++;
        if (correctness.get(2)) score++;
        score = score * 10;

        // Get User's score.
        userRef = db.collection("user");
        Query query = userRef.whereEqualTo("Uid", uid);
        int Score = score;
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase1", document.getId() + " => " + document.getData());
                                int finalScore = Score + Integer.parseInt(String.valueOf(document.get("Score")));
                                userRef.document(document.getId())
                                        .update("Score", finalScore)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("POST", "DocumentSnapshot successfully updated!");
                                                pet_name.setText("your score changes " + String.valueOf(document.get("Score")) + " -> "
                                                        + String.valueOf(finalScore));
                                                return_button.setVisibility(View.VISIBLE);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("POST", "Error updating document", e);
                                            }
                                        });
                            }
                        }
                        else {
                            Log.d("Firebase", "download pet fail");
                        }
                    }
                });
    }

    private void clickable(boolean b) {
        correct.setEnabled(b);
        wrong.setEnabled(b);
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}