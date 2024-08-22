package com.example.fcm_kotlin.manager

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(private val context: Context) {

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_MOBILE = "mobile"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FCM_TOKEN = "fcm_token"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserData(name: String, email: String,mobile:String) {
        sharedPreferences.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_MOBILE,mobile)
            apply()
        }
    }
    fun setFCMtoken(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_FCM_TOKEN, token)
            apply()
        }
    }
    fun getFCMtoken(): String? {
        return sharedPreferences.getString(KEY_FCM_TOKEN,null)
    }
    fun getName(): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }
    fun getMobile(): String? {
        return sharedPreferences.getString(KEY_MOBILE, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()
    }


    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}

