package com.example.findme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HereReportActivity extends Activity {

    TextView back_button;
    CardView location_button;
    ConstraintLayout image_button;
    Button submit_button;
    ImageView camera_image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Image from camera activity.
        Intent intent = getIntent();
        Bitmap image = (Bitmap)intent.getExtras().get("bitmap");

        setContentView(R.layout.here);

        back_button = findViewById(R.id.back);
        location_button = findViewById(R.id.location_card);
        image_button = findViewById(R.id.camera_layout);
        submit_button = findViewById(R.id.submit_button);
        camera_image = findViewById(R.id.camera_image);

        if (image != null) camera_image.setImageBitmap(image);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getBaseContext(), "location", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getBaseContext(), "gallery", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getBaseContext(), "submit", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}