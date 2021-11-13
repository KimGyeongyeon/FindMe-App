package com.example.findme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class HereRepotActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.here);

//        Button imageButton = findViewById(R.id.camera_layout);
//        Button submitButton = findViewById(R.id.submit_button);
//
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast toast = Toast.makeText(getBaseContext(), "gallery", Toast.LENGTH_LONG);
//                toast.show();
//            }
//        });
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast toast = Toast.makeText(getBaseContext(), "submit", Toast.LENGTH_LONG);
//                toast.show();
//            }
//        });
    }
}