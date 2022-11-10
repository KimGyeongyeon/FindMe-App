package com.example.findme.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    lateinit var mAuth: FirebaseAuth

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    init {
        viewModelScope.launch {
            mAuth = FirebaseAuth.getInstance()
        }
    }

    fun loadUserInfo(){
        val result = viewModelScope.launch(Dispatchers.IO) {
            _currentUser.value = mAuth.currentUser
        }
    }
}