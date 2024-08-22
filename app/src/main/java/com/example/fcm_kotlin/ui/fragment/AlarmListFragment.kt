package com.example.fcm_kotlin.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.R
import com.example.fcm_kotlin.databinding.FragmentAlarmListBinding
import com.example.fcm_kotlin.manager.ReminderManager
import com.example.fcm_kotlin.ui.activity.ReminderSetActivity
import com.example.fcm_kotlin.ui.adapter.ReminderAdapter
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.example.fcm_kotlin.viewModel.ViewReminderViewModel
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class AlarmListFragment : Fragment() {
    private lateinit var binding: FragmentAlarmListBinding
    private lateinit var context: Context
    private lateinit var viewReminderViewModel: ViewReminderViewModel
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderManager: ReminderManager
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAlarmListBinding.inflate(layoutInflater)
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
        bindView()
        setUpViewModel()
        setRecyclerView()
        showOverlay(true)
        fetchReminders()
        setOnClickListeners()
    }

    private fun bindView() {
        recyclerView = binding.recyclerView
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
    }

    private fun setUpViewModel() {
        reminderManager = ReminderManager(requireActivity())
        val viewModelFactory = ViewModelFactory()
        viewReminderViewModel = ViewModelProvider(this, viewModelFactory)[ViewReminderViewModel::class.java]
    }

    private fun fetchReminders() {
        viewReminderViewModel.getReminders { reminders, errorMessage ->
            if (reminders != null) {
                Log.e("REMINDERS_LIST",reminders.toString())
                reminderAdapter.updateReminders(reminders)
                showOverlay(false)
            } else {
                showToast(errorMessage)
                Log.e("ViewRemindersActivity", "Failed to fetch reminders: $errorMessage")
            }
        }
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        reminderAdapter = ReminderAdapter(context,emptyList())
        recyclerView.adapter = reminderAdapter
    }

    private fun setOnClickListeners() {

    }

    private fun showToast(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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