package com.example.fcm_kotlin.viewModel

import androidx.lifecycle.ViewModel
import com.example.fcm_kotlin.model.SignUpModel

class SignUpViewModel(private val signUpModel: SignUpModel) : ViewModel() {

    fun signUpWithEmailAndPassword(email: String, password: String, name: String,mobile:String, callback: (Boolean, String?) -> Unit) {
        signUpModel.signUpWithEmailAndPassword(email, password, name, mobile,callback)
    }
}




