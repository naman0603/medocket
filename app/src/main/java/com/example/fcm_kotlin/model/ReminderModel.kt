package com.example.fcm_kotlin.model

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ReminderModel {
    private  val authId = FirebaseAuth.getInstance().currentUser?.uid

    fun saveReminder(title: String, message: String, timePair: MutableList<Pair<Int,Pair<Int, Int>>>, dosePairs: MutableList<Pair<Int, String>>, imageUrl: String, startDate: String, endDate: String, foodBoolean: Boolean, requestCodePair:  MutableList<Pair<Int, Int>> , callback: (Boolean, String?) -> Unit) {
        val timePairMap = timePair.associate { it.first.toString() to "${it.second.first}:${it.second.second}" }
        val dosePairsMap = dosePairs.associate { it.first.toString() to it.second }
        val requestCodePairMap = requestCodePair.associate { it.first.toString() to it.second }

        val reminderData = hashMapOf(
            "title" to title,
            "message" to message,
            "timePair" to timePairMap,
            "dosePairs" to dosePairsMap,
            "imageUrl" to imageUrl,
            "startDate" to startDate,
            "endDate" to endDate,
            "foodBoolean" to foodBoolean,
            "requestCodePair" to requestCodePairMap
        )

        val collectionRef = Firebase.firestore.collection("User Reminders").document(authId!!)
            .collection("Reminders")

        collectionRef
            .add(reminderData)
            .addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                // Save the document ID within the document itself
                documentReference.update("docId", docId)
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener { e ->
                        callback(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }
    fun uploadImageToFirebase(uri: Uri, callback: (Boolean,Uri?) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference.child(authId!!)
        val imageRef = storageReference.child(UUID.randomUUID().toString())
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { url ->
                    callback(true,url) // Pass URI to the callback
                }.addOnFailureListener {
                    callback(false,null) // Pass null if download URL retrieval fails
                }
            } else {
                callback(false,null) // Pass null if upload task fails
            }
        }
    }
}