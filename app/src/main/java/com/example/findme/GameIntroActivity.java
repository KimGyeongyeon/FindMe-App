package com.example.findme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameIntroActivity extends AppCompatActivity {

    TextView back_button;
    TextView my_score;
    TextView world_record;
    Button start_game;

    String my_score_;
    String world_record_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_intro);

        back_button = findViewById(R.id.back);
        start_game = findViewById(R.id.start);
        my_score = findViewById(R.id.my_score);
        world_record = findViewById(R.id.world_score);

        //Get user's and highest score from firebase.
        // my_score_ =
        // world_record_ =
        // my_score.setText(my_score_);
        // world_record.setText(world_record_);

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
}