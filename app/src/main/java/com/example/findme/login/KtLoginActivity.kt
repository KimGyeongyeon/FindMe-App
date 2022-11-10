package com.example.findme.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.findme.MainActivity
import com.example.findme.R
import com.example.findme.databinding.ActivityLoginBinding

class KtLoginActivity: AppCompatActivity(), OnSignEndListener {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoginFragment()
    }

    override fun onSignEnd() {
        val loginIntent = Intent(this, MainActivity::class.java)
        startActivity(loginIntent)
        finish() // 결과를 부모 activity로 전달
    }

    private fun setLoginFragment(){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.login_host_fragment, LoginFragment())
        fragmentTransaction.commit()
    }

}