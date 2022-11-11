package com.example.findme.login

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.app.ProgressDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.findme.R
import com.example.findme.databinding.ActivityLogin2Binding
import java.lang.Exception

class LoginFragment : Fragment(), OnSignEndListener {
    private var _binding: ActivityLogin2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var loadingBar: ProgressDialog

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
        viewModel.loadUserInfo()
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it != null) {
                sendUserToMainPage()
            }
        }

        /* 리스너 설정 */
        binding.newAccountLink.setOnClickListener { it: View? -> sendUserToRegister() }
        binding.loginButton.setOnClickListener { it: View? -> allowUserToLogin() }
        watchTextValidity()
        loadingBar = ProgressDialog(requireContext())
    }

    private fun allowUserToLogin() {
        loadingBar.apply {
            setTitle("Sign In")
            setMessage("Please wait...")
            setCanceledOnTouchOutside(true)
        }.show()

        val email = binding.loginEmail.editText?.text.toString()
        val password = binding.loginPassword.editText?.text.toString()

        viewModel.signIn(email, password, this)
    }

    override fun onSignSuccess() {
        loadingBar.dismiss()
        if(activity is OnSignEndListener){
            (activity as OnSignEndListener).onSignSuccess()
        }

    }

    override fun onFail() {
        loadingBar.dismiss()
        if(activity is OnSignEndListener){
            (activity as OnSignEndListener).onFail()
        }
    }

    private fun sendUserToMainPage() {
        try {
            val signEndCallback: OnSignEndListener = activity as OnSignEndListener
            signEndCallback.onSignSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendUserToRegister() {
        val registerFragment: Fragment = RegisterFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.login_host_fragment, registerFragment)
        transaction.addToBackStack(null)
        transaction.commit()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ID_WARNING = "아이디를 입력해주세요"
        private const val PW_WARNING = "비밀번호를 입력해주세요"
    }

}