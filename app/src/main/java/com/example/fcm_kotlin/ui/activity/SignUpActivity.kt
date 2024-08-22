package com.example.fcm_kotlin.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.fcm_kotlin.databinding.ActivitySignUpBinding
import com.example.fcm_kotlin.utils.CommonUtils
import com.example.fcm_kotlin.viewModel.SignUpViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var lottieView : LottieAnimationView
    private lateinit var txtName : TextInputEditText
    private lateinit var txtEmail : TextInputEditText
    private lateinit var txtPassword : TextInputEditText
    private lateinit var txtConfirmPassword : TextInputEditText
    private lateinit var txtMobile : TextInputEditText
    private lateinit var btnSignIn : MaterialButton
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var commonUtils: CommonUtils
    private lateinit var loginTxt : TextView
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setUpModel()
        bindView()
        val msg = "Already have account? <b>Log In</b>"
        loginTxt.text = Html.fromHtml(msg)
        setUpValidation()
        setUpClickListeners()
    }


    private fun setUpClickListeners() {
        btnSignIn.setOnClickListener {
            collapseKeyboard()
            if(validate()){
                showOverlay(true)
                signUp()
            }
        }
        loginTxt.setOnClickListener {
            navigateToLogin()
        }
    }
    private fun collapseKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun signUp() {
        val name = txtName.text.toString()
        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()
        val mobile = txtMobile.text.toString()

        signUpViewModel.signUpWithEmailAndPassword(email, password, name,mobile) { isSuccess, errorMessage ->
            if (isSuccess) {
                navigateToLogin()
                showToast("Sign up successful. Verification email sent.")
            } else {
                showOverlay(false)
                showToast(errorMessage.toString())
            }
        }

    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun validate(): Boolean {
    if (txtName.text.toString().isEmpty()) {
        showToast("Please Enter Name")
        return false
    }
    if (commonUtils.validateEmail(txtEmail.text.toString())) {
        if (commonUtils.validateIndianMobileNumber(txtMobile.text.toString())) {
            if (commonUtils.validatePassword(txtPassword.text.toString())) {
                if (txtConfirmPassword.text.toString() == txtPassword.text.toString()) {
                    return true
                } else {
                    showToast("Passwords Doesn't Match")
                    return false
                }
            } else {
                showToast("Please Enter Valid Password")
                return false
            }
        } else {
            showToast("Please Enter Valid Number")
            return false
        }
    } else {
        showToast("Please Enter Valid Email")
        return false
    }
}



    private fun setUpValidation() {
        commonUtils.setupValidation(this,txtEmail,binding.emailTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validateEmail(input)
            }
        })
        commonUtils.setupValidation(this,txtMobile,binding.mobileTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validateIndianMobileNumber(input)
            }
        })

        commonUtils.setupValidation(this,txtPassword,binding.passwordTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validatePassword(input)
            }
        })
        commonUtils.setupValidation(this,txtConfirmPassword,binding.conPasswordTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validatePassword(input)
            }
        })
    }

    private fun bindView() {
        lottieView = binding.lottieView
        txtName = binding.txtName
        txtEmail = binding.txtEmail
        txtPassword = binding.txtPassword
        txtConfirmPassword = binding.txtConfirmPassword
        txtMobile = binding.txtMobile
        btnSignIn = binding.btnSignIn
        loginTxt = binding.loginTxt
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
    }

    private fun setUpModel() {
        commonUtils = CommonUtils()
        val viewModelFactory = ViewModelFactory()
        signUpViewModel = ViewModelProvider(this, viewModelFactory)[SignUpViewModel::class.java]
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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


}