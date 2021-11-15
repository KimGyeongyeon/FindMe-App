package com.example.findme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PetInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.petinfo);

        final Button main_button = findViewById(R.id.main_bar);
        final Button here_button = findViewById(R.id.here_button);
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

        here_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getBaseContext(), "여기 없애줘 경연아.", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        not_here_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send report and give toast.
                // divide it into 2 functions.
            }
        });

        map_preview_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to PetInfoMapActivity
                Intent intent = new Intent(getBaseContext(), PetInfoMapActivity.class);
                startActivity(intent);
            }
        });

    }
}