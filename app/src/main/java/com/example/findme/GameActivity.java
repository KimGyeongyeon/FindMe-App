package com.example.findme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    private long backKeyPressedTime  = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
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
}