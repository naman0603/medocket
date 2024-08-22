package com.example.fcm_kotlin.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.fcm_kotlin.model.ReminderModel

class ReminderViewModel(private val reminderModel: ReminderModel) : ViewModel() {

    fun saveReminder(title: String, message: String, timePair: MutableList<Pair<Int,Pair<Int, Int>>>, dosePairs: MutableList<Pair<Int, String>>, imageUrl: String, startDate: String, endDate: String, foodBoolean: Boolean, requestCodePair:  MutableList<Pair<Int, Int>>, callback: (Boolean, String?) -> Unit ) {
        reminderModel.saveReminder(title, message, timePair, dosePairs, imageUrl, startDate, endDate, foodBoolean, requestCodePair,callback)
    }

    fun uploadImageToFirebase(uri: Uri, callback: (Boolean, Uri?) -> Unit) {
        reminderModel.uploadImageToFirebase(uri, callback)
    }

}