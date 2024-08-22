package com.example.fcm_kotlin.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpModel {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun signUpWithEmailAndPassword(email: String, password: String, name: String,mobile:String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "mobile" to mobile
                        )
                        // Save user data in Firestore
                        firestore.collection("User").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                // Send email verification
                                firebaseAuth.currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener { verificationTask ->
                                        if (verificationTask.isSuccessful) {
                                            callback(true, null)
                                        } else {
                                            callback(false, verificationTask.exception?.message)
                                        }
                                    }
                            }
                            .addOnFailureListener { e ->
                                callback(false, e.message)
                            }
                    } else {
                        callback(false, "User ID is null.")
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}


