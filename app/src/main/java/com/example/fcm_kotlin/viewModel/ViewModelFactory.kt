package com.example.fcm_kotlin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fcm_kotlin.model.HomeModel
import com.example.fcm_kotlin.model.LoginModel
import com.example.fcm_kotlin.model.ReminderModel
import com.example.fcm_kotlin.model.SignUpModel
import com.example.fcm_kotlin.model.ViewReminderModel

class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val loginModel = LoginModel()
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginModel) as T
        } else if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            val signUpModel = SignUpModel()
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(signUpModel) as T
        }else if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            val reminderModel = ReminderModel()
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(reminderModel) as T
        }else if (modelClass.isAssignableFrom(ViewReminderViewModel::class.java)) {
            val viewReminderModel = ViewReminderModel()
            @Suppress("UNCHECKED_CAST")
            return ViewReminderViewModel(viewReminderModel) as T
        }else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val homeModel = HomeModel()
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(homeModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
