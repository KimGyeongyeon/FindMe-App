package com.example.findme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class PetInfoActivity extends AppCompatActivity {

    private ArrayList<PetInfo> petArray;
    private ArrayList<ImageView> images;
    // private Context context;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) View setting
        setContentView(R.layout.petinfo);
        final TextView name = findViewById(R.id.pet_name_here);
        final TextView date = findViewById(R.id.pet_date_here);
        final TextView detail = findViewById(R.id.pet_detail_here);
        images = new ArrayList<>();
        images.add(findViewById(R.id.imageView_1));
        images.add(findViewById(R.id.imageView_2));
        images.add(findViewById(R.id.imageView_3));
        images.add(findViewById(R.id.imageView_4));

        // 2) Initialize FireBase-related variables
        petArray = new ArrayList<>(); //Include Every Pet information
        database = FirebaseDatabase.getInstance(); // Connect FireBase Here
        ref = database.getReference("pet");// Connect DB table

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petArray.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //PetInfo object 형태로 값 저장
                    PetInfo info = dataSnapshot.getValue(PetInfo.class);
                    petArray.add(info);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PetInfoActivity", String.valueOf(error.toException()));
            }
        });



         // 3) Distribute the info to view

        // 요청한 pet의 정보를 찾고 그에 해당하는 index를 설정하는 방식으로 변경하자.
//        try{
//            int right_index = 0;
//            PetInfo the_pet = new PetInfo(petArray.get(right_index));
//
//        }catch (java.lang.Exception exe){
//
//            Log.d("PetInfo-81", String.valueOf(petArray.size()));
//            Log.d("PetInfo-81",String.valueOf(exe));
//
//        }

        try{
            int num = 0;
            for(ImageView i : images){
                Glide.with(i).load(petArray.get(0).getImage()[num])
                        .into(i);
//            i.setImageResource(R.drawable.dog);
                //petArray.getImage()로 url array(array list 아님!) 받기
                num++;
            }
        }catch (java.lang.Exception ex){
            Log.d("PetInfo-99", String.valueOf(ex));
        }

        try{
            name.setText(petArray.get(0).getName());
            date.setText(petArray.get(0).getDate());
            detail.setText(petArray.get(0).getDetail());
        }catch (java.lang.Exception ex){
            Log.d("PetInfo-103", String.valueOf(ex));
        }






        /*
         *
         *   a ) Click Event
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