package com.example.findme.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.findme.R;
import com.example.findme.databinding.ActivityLogin2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private ActivityLogin2Binding binding;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityLogin2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        /* 통신은 백그라운드로 빼야함 */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainPage();
        }

        /* 리스너 설정 */
        binding.newAccountLink.setOnClickListener((it) ->
                {
                    SendUserToRegister();
                }
        );

        binding.loginButton.setOnClickListener((it) ->
                {
                    AllowUserToLogin();
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void AllowUserToLogin() {
        String email = binding.loginEmail.getText().toString();
        String password = binding.loginPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Please enter password...", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialog loadingBar = new ProgressDialog(requireContext());
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((task) ->
                    {
                        if (task.isSuccessful()) {
                            SendUserToMainPage();
                            Toast.makeText(requireContext(), "Logged in Successful", Toast.LENGTH_SHORT).show();

                        } else {
                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(requireContext(), "Error : " + message, Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();

                    }
            );
        }
    }

    private void SendUserToMainPage() {
        try{
            OnSignEndListener signEndCallback = (LoginActivity) getActivity();
            signEndCallback.onSignEnd();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void SendUserToRegister() {
        Fragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.login_host_fragment, registerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
