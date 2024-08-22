package com.example.fcm_kotlin.viewModel

import androidx.lifecycle.ViewModel
import com.example.fcm_kotlin.model.HomeModel

class HomeViewModel(private val homeModel: HomeModel) : ViewModel() {

    fun getReminders(date:String,callback: (List<Map<String, Any>>?, String?) -> Unit) {
        homeModel.getReminders(date,callback)
    }
}