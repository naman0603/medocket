// ViewRemindersActivity.kt
package com.example.fcm_kotlin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.ui.adapter.ReminderAdapter
import com.example.fcm_kotlin.databinding.ActivityViewRemindersBinding
import com.example.fcm_kotlin.manager.ReminderManager
import com.example.fcm_kotlin.viewModel.ViewReminderViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ViewRemindersActivity : AppCompatActivity(){
    private lateinit var binding: ActivityViewRemindersBinding
    private lateinit var viewReminderViewModel: ViewReminderViewModel
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var btnBack: ShapeableImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var reminderManager: ReminderManager
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        bindView()
        setUpViewModel()
        setRecyclerView()
        fetchReminders()
        setOnClickListeners()
    }

    private fun bindView() {
        btnBack = binding.btnBack
        recyclerView = binding.recyclerView
        btnAdd = binding.btnAdd
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
    }

    private fun setUpViewModel() {
        reminderManager = ReminderManager(this)
        val viewModelFactory = ViewModelFactory()
        viewReminderViewModel = ViewModelProvider(this, viewModelFactory)[ViewReminderViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        fetchReminders()
    }

    private fun fetchReminders() {
        viewReminderViewModel.getReminders { reminders, errorMessage ->
            if (reminders != null) {
                // Reminders fetched successfully, update RecyclerView
                Log.e("REMINDERS_LIST",reminders.toString())
                reminderAdapter.updateReminders(reminders)
            } else {
                // Failed to fetch reminders, handle error
                showToast(errorMessage)
                Log.e("ViewRemindersActivity", "Failed to fetch reminders: $errorMessage")
            }
        }
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        reminderAdapter = ReminderAdapter(this,emptyList())
        recyclerView.adapter = reminderAdapter
    }

    private fun setOnClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
        btnAdd.setOnClickListener {
            val intent = Intent(this, ReminderSetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
