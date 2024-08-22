package com.example.fcm_kotlin.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fcm_kotlin.databinding.FragmentAccountBinding
import com.example.fcm_kotlin.manager.DialogBoxManager
import com.example.fcm_kotlin.manager.SharedPreferenceManager
import com.example.fcm_kotlin.ui.activity.LoginActivity
import com.example.fcm_kotlin.utils.CommonUtils
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var context: Context
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var dialogBoxManager: DialogBoxManager
    private lateinit var btnLogOut: ShapeableImageView
    private lateinit var txtName: TextInputEditText
    private lateinit var txtEmail: TextInputEditText
    private lateinit var txtMobile: TextInputEditText
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout
    private lateinit var commonUtils: CommonUtils
    private lateinit var currentEmail: String
    private val editProfileText = "Edit Profile"
    private val saveProfileText = "Save Changes"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setUpModel()
        bindView()
        setUpValidation()
        updateUiAndViews()
        onClickListeners()
    }

    private fun setUpValidation() {
        commonUtils.setupValidation(context,txtEmail,binding.emailTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validateEmail(input)
            }
        })
        commonUtils.setupValidation(context,txtMobile,binding.mobileTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validateIndianMobileNumber(input)
            }
        })
    }

    private fun onClickListeners() {
        btnEditProfile.setOnClickListener {
            val text  = btnEditProfile.text.toString()
            if (text == editProfileText){
                updateUiForEditProfile()
            }

            if(text == saveProfileText){
                saveProfile()
            }

        }
        btnLogOut.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        startActivity(Intent(context, LoginActivity::class.java)).also {
            sharedPreferenceManager.setLoggedIn(false)
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun validate(): Boolean {
        if (txtName.text.toString().isEmpty()) {
            showToast("Please Enter Name")
            return false
        }
        if (commonUtils.validateEmail(txtEmail.text.toString())) {
            if (commonUtils.validateIndianMobileNumber(txtMobile.text.toString())) {
               return true
            } else {
                showToast("Please Enter Valid Number")
                return false
            }
        } else {
            showToast("Please Enter Valid Email")
            return false
        }
    }

    private fun saveInFirebase(callback: (Boolean) -> Unit) {
        val userId = firebaseAuth.currentUser?.uid
        val userMap = hashMapOf(
            "name" to txtName.text.toString(),
            "email" to txtEmail.text.toString(),
            "mobile" to txtMobile.text.toString(),
        )

        if (userId != null) {
            firestore.collection("User").document(userId)
                .update(userMap as Map<String, Any>)
                .addOnSuccessListener {
                    callback.invoke(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase Error Account Fragment", "Error updating document", exception)
                    callback.invoke(false)
                }
        } else {
            showToast("Something went wrong")
            Log.e("Firebase Error Account Fragment", "User Id is null")
            callback.invoke(false)
        }
    }

    private fun saveProfile() {
        if(validate()){
            val email = binding.txtEmail.text.toString()
            if(email != currentEmail){
                showToast("Sorry Email id cannot be changed")
            }else{
                showOverlay(true)
                saveInFirebase { success ->
                    if (success){
                        showToast("Profile updated")
                        logout()
                        showOverlay(false)
                    }
                }

            }
        }
    }

    private fun updateEmail(callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = firebaseAuth.currentUser?.uid
        if (userId!= null) {

            firebaseAuth.currentUser?.updateEmail(txtEmail.text.toString())?.addOnSuccessListener { onSuccess ->
                currentUser!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Firebase Error Account Fragment", "Verification email sent.")
                            callback.invoke(true)

                        } else {
                            Log.w("Firebase Error Account Fragment", "Failed to send verification email.", task.exception)
                            callback.invoke(false)

                        }
                    }
            }?.addOnFailureListener { exception ->
                showToast("Error in updating mail \n Please try again ")
                Log.e("Firebase Error Account Fragment", "Error updating document", exception)
                callback.invoke(false)
            }
        } else {
            showToast("Something went wrong")
            Log.e("Firebase Error Account Fragment", "User Id is null")
            callback.invoke(false)
        }
    }

    private fun updateUiForEditProfile() {
        txtName.text!!.clear()
        txtMobile.text!!.clear()

        txtMobile.isFocusableInTouchMode = true
        txtMobile.requestFocus()

        txtName.isFocusableInTouchMode = true
        txtName.requestFocus()

        btnEditProfile.text = saveProfileText
    }

    private fun updateUiAndViews() {
        val name = sharedPreferenceManager.getName().toString()
        currentEmail = sharedPreferenceManager.getEmail().toString()
        val mobile = sharedPreferenceManager.getMobile().toString()
        txtName.setText(name)
        txtEmail.setText(currentEmail)
        txtMobile.setText(mobile)
    }

    private fun bindView() {
        btnLogOut = binding.btnLogOut
        txtName = binding.txtName
        txtEmail = binding.txtEmail
        txtMobile = binding.txtMobile
        btnEditProfile = binding.btnEditProfile
        overlayView = binding.overlayView
        spinKitView = binding.spinKit

    }

    private fun setUpModel() {
        sharedPreferenceManager = SharedPreferenceManager(requireActivity())
        commonUtils = CommonUtils()
        dialogBoxManager = DialogBoxManager(requireActivity())
    }
    private fun showOverlay(show: Boolean) {
        if (show) {
            overlayView.visibility = View.VISIBLE
            spinKitView.visibility = View.VISIBLE

        } else {
            spinKitView.visibility = View.GONE
            overlayView.visibility = View.GONE
        }
    }
    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}