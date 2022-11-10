package com.example.findme.login

import com.google.firebase.auth.FirebaseAuth
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.findme.R
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.findme.databinding.ActivityRegisterBinding
import java.lang.Exception

class RegisterFragment : Fragment() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding.registerButton.setOnClickListener { clickedView: View? -> CreateNewAccount() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun SendUserToLoginActivity() {
        val registerFragment: Fragment = LoginFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.login_host_fragment, registerFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun SendUserToMainPage() {
        try {
            val signEndCallback: OnSignEndListener? = activity as KtLoginActivity?
            signEndCallback!!.onSignEnd()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun CreateNewAccount() {
        Log.d("K001", "버튼클릭")
        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Please enter email...", Toast.LENGTH_SHORT).show()
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Please enter password...", Toast.LENGTH_SHORT).show()
        } else {
//            ProgressDialog loadingBar = new ProgressDialog(getContext());
//            loadingBar.setTitle("Creating New Account");
//            loadingBar.setMessage("Please wait, we are creating new account for you ... ");
//            loadingBar.setCanceledOnTouchOutside(true);
//            loadingBar.show();
            Log.d("K001", "조건 검사 통과")
            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("K001", "createUserWithEmail:success")
                        val user = mAuth!!.currentUser
                        SendUserToLoginActivity()
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
                    } else {
                        val message = task.exception.toString()
                        Toast.makeText(context, "Error : $message", Toast.LENGTH_SHORT).show()
                    }
                    //                    loadingBar.dismiss();
                }
        }
    }
}