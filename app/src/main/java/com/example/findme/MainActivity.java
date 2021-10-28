package com.example.findme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = getIntent();
        String username = intent.getStringExtra("NAME");
        Toast toast = Toast.makeText(getBaseContext(), "username is " + username, Toast.LENGTH_LONG);
        toast.show();
    }
}