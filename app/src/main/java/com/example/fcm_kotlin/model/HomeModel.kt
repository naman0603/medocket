package com.example.fcm_kotlin.model

import android.util.Log
import com.example.fcm_kotlin.utils.CommonUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class HomeModel {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getReminders(date: String, callback: (List<Map<String, Any>>?, String?) -> Unit) {
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
                            if(inRange(it,date)){
                                Log.d("Range Check ", "True")
                                reminders.add(it)
                            }else{
                                Log.d("Range Check ", "False")
                            }
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

    private fun inRange(it: Map<String, Any>, date: String): Boolean {
        val startDate = it["startDate"] as String
        val endDate = it["endDate"] as String

         if(endDate != ""){
            val dates = CommonUtils().getDates(startDate,endDate)
            Log.d("dates","$dates")
             return dates.contains(date)
        }else{
            return startDate==date
        }

    }
}