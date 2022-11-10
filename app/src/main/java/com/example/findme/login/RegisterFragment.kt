package com.example.findme.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.findme.R;
import com.example.findme.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class RegisterFragment extends Fragment {

    private ActivityRegisterBinding binding;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String uid;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        binding.registerButton.setOnClickListener( clickedView -> {CreateNewAccount();});

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void SendUserToLoginActivity() {
        Fragment registerFragment = new LoginFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.login_host_fragment, registerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void SendUserToMainPage() {
        try{
            OnSignEndListener signEndCallback = (LoginActivity) getActivity();
            signEndCallback.onSignEnd();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void CreateNewAccount() {
        String email = binding.registerEmail.getText().toString();
        String password = binding.registerPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(getContext(), "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else {
//            ProgressDialog loadingBar = new ProgressDialog(getContext());
//            loadingBar.setTitle("Creating New Account");
//            loadingBar.setMessage("Please wait, we are creating new account for you ... ");
//            loadingBar.setCanceledOnTouchOutside(true);
//            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Log.d("Register", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Post user information.
//                        db = FirebaseFirestore.getInstance();
//                        userRef = db.collection("user");
//                        Map<String, Object> data = new HashMap<>();
//                        String[] temp = email.split("@");
//                        data.put("NickName", temp[0]);
//                        data.put("Score", 0);
//                        data.put("Uid", user.getUid());
//                        data.put("gameCount", 0);
//                        userRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d("FIREBASE", "create account");
//                                SendUserToLoginActivity();
//                            }
//                        });
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(getContext(), "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
//                    loadingBar.dismiss();
                }
            });
        }
    }
}
