package com.example.fcm_kotlin.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.fcm_kotlin.databinding.ActivityLoginBinding
import com.example.fcm_kotlin.manager.SharedPreferenceManager
import com.example.fcm_kotlin.utils.CommonUtils
import com.example.fcm_kotlin.viewModel.LoginViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Objects

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var txtEmail : TextInputEditText
    private lateinit var txtPassword : TextInputEditText
    private lateinit var btnLogIn : MaterialButton
    private lateinit var signUpTxt : TextView
    private lateinit var lottieView : LottieAnimationView
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferencesManager: SharedPreferenceManager
    private lateinit var commonUtils: CommonUtils
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setUpModel()
        bindView()
        val msg = "Don't have account? <b>Sign Up</b>"
        signUpTxt.text = Html.fromHtml(msg)
        setUpValidation()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        btnLogIn.setOnClickListener {
            collapseKeyboard()
            if(validate()){
                showOverlay(true)
                login()
            }
        }
        signUpTxt.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun collapseKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun login() {
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()

        loginViewModel.loginWithEmailAndPassword(email, password) { isSuccess, errorMessage, name, mobile ->
            if (isSuccess) {
                sharedPreferencesManager.saveUserData(name!!, email,mobile!!)
                sharedPreferencesManager.setLoggedIn(true)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
               }
            else {
                showOverlay(false)
                showToast(errorMessage.toString())
            }
        }
    }

    private fun validate(): Boolean {
        return if (commonUtils.validateEmail(
                Objects.requireNonNull<Editable>(
                    txtEmail.text
                ).toString()
            )
        ) {
            if (commonUtils.validatePassword(
                    Objects.requireNonNull<Editable?>(txtPassword.text).toString()
                )
            ) {
                true
            } else {
                showToast("Please Enter Valid Password")
                false
            }
        } else {
            showToast("Please Enter Valid Number")
            false
        }
    }


    private fun setUpValidation() {
        commonUtils.setupValidation(this,txtEmail,binding.emailTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validateEmail(input)
            }
        })
        commonUtils.setupValidation(this,txtPassword,binding.passwordTextInputLayout,object : CommonUtils.Validator{
            override fun isValid(input: String?): Boolean {
                return commonUtils.validatePassword(input)
            }
        })
    }

    private fun setUpModel() {
        val viewModelFactory = ViewModelFactory()
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        sharedPreferencesManager = SharedPreferenceManager(this)
        commonUtils = CommonUtils()
    }

    private fun bindView() {
        txtEmail = binding.txtEmail
        txtPassword = binding.txtPassword
        btnLogIn = binding.btnLogIn
        signUpTxt = binding.signUpTxt
        lottieView = binding.lottieView
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
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