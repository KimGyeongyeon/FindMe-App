package com.example.findme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button loginButton = findViewById(R.id.loginbtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText)findViewById(R.id.logintext);
                String username = editText.getText().toString();
//                Toast toast = Toast.makeText(getBaseContext(), "username is " + username, Toast.LENGTH_LONG);
//                toast.show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("NAME", username);
                startActivity(intent);
            }
        });
    }
}