package com.example.fcm_kotlin.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ViewReminderModel {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getReminders(callback: (List<Map<String, Any>>?, String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        val authId = currentUser?.uid

        if (authId != null) {
            val reminders = mutableListOf<Map<String, Any>>()

            firestore.collection("User Reminders").document(authId)
                .collection("Reminders")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val reminderData = document.data
                        reminderData?.let {
                            reminders.add(it)
                        }
                    }
                    callback(reminders, null)
                }
                .addOnFailureListener { e ->
                    callback(null, e.message)
                }
        } else {
            callback(null, "User not authenticated")
        }
    }
}



