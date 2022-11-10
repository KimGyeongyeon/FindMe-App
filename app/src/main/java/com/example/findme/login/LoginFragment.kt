package com.example.findme.login

import com.google.firebase.auth.FirebaseAuth
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import android.app.ProgressDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.AuthResult
import com.example.findme.R
import com.example.findme.databinding.ActivityLogin2Binding
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.*

class LoginFragment : Fragment() {
    private var _binding: ActivityLogin2Binding? = null
    private val binding get() = _binding!!

    private var mAuth: FirebaseAuth? = null
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityLogin2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 로그인 확인되면 메인 페이지로 이동 */
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                SendUserToMainPage()
            }
        }

        /* 리스너 설정 */
        binding.newAccountLink.setOnClickListener { it: View? -> SendUserToRegister() }
        binding.loginButton.setOnClickListener { it: View? -> allowUserToLogin() }
        watchTextValidity()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun watchTextValidity() {
        binding.loginEmail.apply {
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    binding.loginButton.isClickable = false
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        binding.loginButton.isClickable = false
                        this@apply.error = ID_WARNING
                    } else {
                        binding.loginButton.isClickable = true
                        this@apply.error = null
                    }
                }
            })
        }

        binding.loginPassword.apply {
            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    binding.loginButton.isClickable = false
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        binding.loginButton.isClickable = false
                        this@apply.error = PW_WARNING
                    } else {
                        binding.loginButton.isClickable = true
                        this@apply.error = null
                    }
                }
            })
        }

    }

    private fun allowUserToLogin() {
        val email = binding.loginEmail.editText?.text.toString()
        val password = binding.loginPassword.editText?.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Please enter email...", Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext(), "Please enter password...", Toast.LENGTH_SHORT).show()
        } else {
            val loadingBar = ProgressDialog(requireContext())
            loadingBar.apply {
                setTitle("Sign In")
                setMessage("Please wait...")
                setCanceledOnTouchOutside(true)
            }.show()

            mAuth!!.signInWithEmailAndPassword(email, password) // null
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        SendUserToMainPage()
                        Toast.makeText(requireContext(), "Logged in Successful", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val message = Objects.requireNonNull(task.exception).toString()
                        Toast.makeText(requireContext(), "Error : $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                    loadingBar.dismiss()
                }
        }
    }

    private fun SendUserToMainPage() {
        try {
            val signEndCallback: OnSignEndListener? = activity as KtLoginActivity?
            signEndCallback!!.onSignEnd()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun SendUserToRegister() {
        val registerFragment: Fragment = RegisterFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.login_host_fragment, registerFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        private const val ID_WARNING = "아이디를 입력해주세요"
        private const val PW_WARNING = "비밀번호를 입력해주세요"
    }
}