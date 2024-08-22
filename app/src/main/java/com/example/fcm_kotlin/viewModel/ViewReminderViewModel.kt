// ViewReminderViewModel.kt
package com.example.fcm_kotlin.viewModel

import androidx.lifecycle.ViewModel
import com.example.fcm_kotlin.model.ViewReminderModel

class ViewReminderViewModel(private val viewReminderModel: ViewReminderModel) : ViewModel() {

    fun getReminders(callback: (List<Map<String, Any>>?, String?) -> Unit) {
        viewReminderModel.getReminders(callback)
    }
}
