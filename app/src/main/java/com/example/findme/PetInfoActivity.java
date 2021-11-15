package com.example.findme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PetInfoActivity extends AppCompatActivity {

    private ArrayList<PetInfo> PetArray;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.petinfo);

        final TextView name = findViewById(R.id.pet_name_here);
        final TextView date = findViewById(R.id.pet_date_here);
        final TextView detail = findViewById(R.id.pet_detail_here);
        final ArrayList<ImageView> images = new ArrayList<ImageView>();
        images.add(findViewById(R.id.imageView_1));
        images.add(findViewById(R.id.imageView_2));
        images.add(findViewById(R.id.imageView_3));
        images.add(findViewById(R.id.imageView_4));

        //이걸 firebase와 연동.
        for(ImageView i : images){
            i.setImageResource(R.drawable.dog);
            //PetArray.getImage()로 url array(array list 아님!) 받기
        }

        /*
         *
         *   Click Event
         *
         */
        final Button main_button = findViewById(R.id.main_bar);
//      final Button here_button = findViewById(R.id.here_button);
        final Button not_here_button = findViewById(R.id.not_here_button);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        not_here_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send report and give toast.
                // divide it into 2 functions.
                Toast toast = Toast.makeText(getBaseContext(), "Success! Thank you for participation.", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }
}