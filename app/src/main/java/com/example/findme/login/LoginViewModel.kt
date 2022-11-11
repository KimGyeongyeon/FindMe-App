package com.example.findme.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
    
    // 추후 생성자로 빼서 Hilt로 주입하기
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    fun loadUserInfo(){
        viewModelScope.launch() {
            _currentUser.value = mAuth.currentUser
        }
    }

    fun signIn(email: String, password: String, onSignEndListener: OnSignEndListener){
        if (email.isEmpty() || password.isEmpty()) {
            onSignEndListener.onFail()
            return
        }
        val loginJob = viewModelScope.launch(dispatcherIO) {
            /* 백그라운드 전환 여부 확인 필요 */
            mAuth.signInWithEmailAndPassword(email, password) // null
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    Log.d("K001", task.toString())
                    if (task.isSuccessful) {
                        Log.d("K001", "성공")
                        onSignEndListener.onSignSuccess()
                    } else {
                        Log.d("K001", "실패")
                        onSignEndListener.onFail()
                    }
                }
        }
        viewModelScope.launch {
            delay(TIME_LIMIT)
            loginJob.cancel()
            onSignEndListener.onFail()
        }
    }

    companion object {
        private const val TIME_LIMIT = 5000L
    }

}