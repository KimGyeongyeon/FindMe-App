package com.example.findme.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.findme.R;
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
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    // Game
    List<String> pet_imgs = new ArrayList<>();
    List<String> qc_imgs = new ArrayList<>();
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

        // Get pet info from game intro.
        Intent intent = getIntent();
        String petName = (String) intent.getExtras().get("petName");

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
        Query query1 = petInfoRef.whereEqualTo("name", petName);
        query1.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase1", document.getId() + " => " + document.getData());
                                pet_name_ = (String) document.get("name");
                                pet_imgs = (List<String>) document.get("img");

                                // Set original, qc list.
                                Glide.with(original).load(pet_imgs.get(0)).into(original);

                                pet_imgs.remove(0);
                                Collections.shuffle(pet_imgs);

                                StorageReference qcReference = storageReference.child("game/");
                                qcReference.listAll()
                                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                            @Override
                                            public void onSuccess(ListResult listResult) {
                                                for (StorageReference item : listResult.getItems()) {
                                                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            qc_imgs.add(String.valueOf(uri));
                                                        }
                                                    });
                                                }
                                                // Set pet name.
                                                pet_name.setText("Does this picture below look like " + pet_name_ + "?");

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
                                                            new Handler().postDelayed(new Runnable()
                                                            {
                                                                @Override
                                                                public void run()
                                                                {
                                                                    //딜레이 후 시작할 코드 작성
                                                                    Log.d("pet_imgs", pet_imgs.toString());
                                                                    Log.d("qc_imgs", qc_imgs.toString());
                                                                    Log.d("game_imgs", game_imgs.toString());

                                                                    //set compare list
                                                                    compares = game_imgs;
                                                                    compares.add(0, pet_imgs.get(0));
                                                                    compares.add(1, qc_imgs.get(0));
                                                                    compares.add(2, pet_imgs.get(1));
                                                                    game_start();
                                                                }
                                                            }, 1000);

                                                        } else {
                                                            Log.d("Firebase2", "download here image fail");
                                                        }
                                                    }
                                                });
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

        if (compares.size() < 10) {
            toast = Toast.makeText(getBaseContext(), "There are not enough reports to play the game.", Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), GameIntroActivity.class);
            startActivity(intent);
            finish();
        }

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
        if (!correctness.get(1)) score++;
        if (correctness.get(2)) score++;
        score = score * 10;

        // Get User's score.
        userRef = db.collection("user");
        Query query = userRef.whereEqualTo("Uid", uid);

        // score to communicate with firebase thread.
        int Score = score;
        int emoji = 0x1F926;
        if (Score == 30) emoji = 0x1F947;
        if (Score == 20) emoji = 0x1F948;
        if (Score == 10) emoji = 0x1F949;

        // variable for firebase thread.
        int Emoji = emoji;
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firebase1", document.getId() + " => " + document.getData());

                                // update game result.
                                int finalScore = Score + Integer.parseInt(String.valueOf(document.get("Score")));
                                int gameCount = 1 + Integer.parseInt(String.valueOf((document.get("gameCount"))));
                                userRef.document(document.getId())
                                        .update("Score", finalScore, "gameCount", gameCount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("POST", "DocumentSnapshot successfully updated!");
                                                pet_name.setText("You Get " + String.valueOf(Score) + "/30" + getEmojiByUnicode(Emoji) +"\nyour score changes " + String.valueOf(document.get("Score")) + " -> "
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