package com.example.fcm_kotlin.ui.activity

import android.Manifest
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fcm_kotlin.R
import com.example.fcm_kotlin.databinding.ActivityMainBinding
import com.example.fcm_kotlin.manager.PermissionManager
import com.example.fcm_kotlin.manager.ReminderManager
import com.example.fcm_kotlin.manager.SharedPreferenceManager
import com.example.fcm_kotlin.ui.fragment.AccountFragment
import com.example.fcm_kotlin.ui.fragment.AddFragment
import com.example.fcm_kotlin.ui.fragment.AlarmListFragment
import com.example.fcm_kotlin.ui.fragment.HomeFragment
import com.example.fcm_kotlin.ui.fragment.MedicineFragment
import com.example.fcm_kotlin.viewModel.LoginViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var permissionManager: PermissionManager
    private lateinit var reminderManager: ReminderManager
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fragmentContainer : FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setUpModel()
        bindView()
        replaceFragment(HomeFragment())
        askPermission()
        onClickListeners()
    }



    private fun askPermission() {
        permissionManager.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun setUpModel() {
        val viewModelFactory = ViewModelFactory()
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        permissionManager = PermissionManager(this)
        sharedPreferenceManager = SharedPreferenceManager(this)
        reminderManager = ReminderManager(this)
    }

    private fun onClickListeners() {
        bottomNavigation.setOnItemSelectedListener {itemId ->
            when(itemId.itemId){
                R.id.home ->replaceFragment(HomeFragment())
                R.id.medicine -> replaceFragment(MedicineFragment())
                R.id.add -> replaceFragment(AddFragment())
                R.id.account -> replaceFragment(AccountFragment())
                R.id.alarm -> replaceFragment(AlarmListFragment())
                else -> replaceFragment(HomeFragment())

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
           .replace(fragmentContainer.id, fragment)
           .commit()
    }
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun bindView() {
        bottomNavigation = binding.bottomNavigation
        fragmentContainer = binding.fragmentContainer


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionManager.onRequestPermissionsResult(requestCode, grantResults)
    }
}
