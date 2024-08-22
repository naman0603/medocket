package com.example.fcm_kotlin.viewModel

import androidx.lifecycle.ViewModel
import com.example.fcm_kotlin.model.LoginModel

class LoginViewModel(private val loginModel: LoginModel) : ViewModel() {

    fun loginWithEmailAndPassword(email: String, password: String, callback: (Boolean, String?, String?,String?) -> Unit) {
        loginModel.loginWithEmailAndPassword(email, password) { isSuccess, errorMessage, name , mobile ->
            callback(isSuccess, errorMessage, name,mobile)
        }
    }
    fun logout(callback: (Boolean, String?) -> Unit) {
        loginModel.logout { isSuccess, errorMessage ->
            callback(isSuccess, errorMessage)
        }
    }
}
