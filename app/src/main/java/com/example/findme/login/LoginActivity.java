package com.example.findme.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.findme.MainActivity;
import com.example.findme.R;
import com.example.findme.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements OnSignEndListener {

//    private FirebaseUser currentUser;
//    private FirebaseAuth mAuth;
//    private ProgressDialog loadingBar;
//
//    private Button LoginButton;
//    private EditText UserEmail, UserPassword;
//    private TextView NeedNewAccountLink, ForgetPasswordLink;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        LoginFragment loginFragment = new LoginFragment();
        fragmentTransaction.add(R.id.login_host_fragment, loginFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onSignEnd() {
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
        finish(); // 결과를 부모 activity로 전달
    }
}