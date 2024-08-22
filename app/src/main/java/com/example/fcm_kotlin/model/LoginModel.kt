package com.example.fcm_kotlin.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginModel {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun loginWithEmailAndPassword(email: String, password: String, callback: (Boolean, String?, String?,String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user!!.isEmailVerified) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            firestore.collection("User").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val name = document.getString("name")
                                        val mobile = document.getString("mobile")
                                        callback(true, null, name,mobile)
                                    } else {
                                        callback(false, "User data not found", null,null)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    callback(false, e.message, null,null)
                                }
                        } else {
                            callback(false, "User ID is null", null,null)
                        }
                    }else{
                        firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                            callback(
                                false,
                                "Email is not verified \n Verification Email is Sent.",
                                null,null
                            )
                        }.addOnFailureListener {
                            callback(
                                false,
                                it.message,
                                null,null
                            )
                        }
                    }
                } else {
                    callback(false, task.exception?.message, null,null)
                }
            }

    }


    fun logout(callback: (Boolean, String?) -> Unit) {
    val user = firebaseAuth.currentUser
    if (user != null) {
        firebaseAuth.signOut()
        val newUser = firebaseAuth.currentUser
        if (newUser == null) {
            callback(true, null)
        } else {
            callback(false, "Sign out failed")
        }
    } else {
        callback(true, null)
    }
}
}
