package com.example.fcm_kotlin.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import com.example.fcm_kotlin.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class CommonUtils {
    private val EMAIL_REGEX = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    private val PASSWORD_REGEX =
        Pattern.compile("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{6,}")
    private val INDIAN_MOBILE_REGEX = Pattern.compile("^[6-9][0-9]{9}$")

    fun validateEmail(email: String?): Boolean {
        return EMAIL_REGEX.matcher(email!!).matches()
    }

    fun validatePassword(password: String?): Boolean {
        return PASSWORD_REGEX.matcher(password!!).matches()
    }

    fun validateIndianMobileNumber(mobileNumber: String?): Boolean {
        return INDIAN_MOBILE_REGEX.matcher(mobileNumber!!).matches()
    }

    fun setupValidation(
        context: Context, editText: TextInputEditText,
        layout: TextInputLayout, validator: Validator,
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val input = s.toString()
                val isValid = validator.isValid(input)
                if (!isValid) {
                    setInvalidBorder(context, layout)
                } else {
                    setValidBorder(context, layout)
                }
            }
        })
    }
    fun getDates(startDateStr: String, endDateStr: String): MutableList<String> {
        val dates = mutableListOf<String>()
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        val startDate = sdf.parse(startDateStr)
        val endDate = sdf.parse(endDateStr)

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time <= endDate) {
            dates.add(sdf.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }

        return dates
    }

    private fun setInvalidBorder(context: Context, layout: TextInputLayout) {
        val invalidColor = ContextCompat.getColor(context, R.color.invalid_input)
        layout.boxStrokeColor = invalidColor
    }

    private fun setValidBorder(context: Context, layout: TextInputLayout) {
        val validColor = ContextCompat.getColor(context, R.color.black)
        layout.boxStrokeColor = validColor
    }

    interface Validator {
        fun isValid(input: String?): Boolean
    }
}
