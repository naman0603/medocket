package com.example.fcm_kotlin.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.dataClass.Medicine
import com.example.fcm_kotlin.databinding.FragmentHomeBinding
import com.example.fcm_kotlin.manager.DialogBoxManager
import com.example.fcm_kotlin.ui.adapter.HomeAdapter
import com.example.fcm_kotlin.viewModel.HomeViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.github.ybq.android.spinkit.SpinKitView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var context: Context
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout
    private lateinit var btnCalender : ImageView
    private lateinit var selectedDate : TextView
    private lateinit var txtView : TextView
    private lateinit var reminders: String
    private lateinit var medicines: List<Medicine>
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var dialogBoxManager: DialogBoxManager
    private val currentDate = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
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
        setUpViewModel()
        bindView()
        showOverlay(true)
        fetchReminders(currentDate)
        onClickListeners()
    }

    private fun onClickListeners() {
        btnCalender.setOnClickListener {
            dialogBoxManager.showDatePickerDialog("0"){date ->

                fetchReminders(date)
                if (date != currentDate ){
                    selectedDate.text = date
                }else{
                    selectedDate.text = "Today's Reminders"
                }

            }
        }
    }

    private fun setUpRecyclerViews() {
        mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mainRecyclerView.adapter = homeAdapter

        val morningMedicines = filterMedicinesByTime(1)
        val afternoonMedicines = filterMedicinesByTime(2)
        val eveningMedicines = filterMedicinesByTime(3)

        if(morningMedicines.isEmpty() && afternoonMedicines.isEmpty() && eveningMedicines.isEmpty()){
            txtView.visibility = View.VISIBLE
        }else{
            txtView.visibility = View.GONE
        }
        homeAdapter.addMedicines("Morning", morningMedicines)
        homeAdapter.addMedicines("Afternoon", afternoonMedicines)
        homeAdapter.addMedicines("Night", eveningMedicines)



    }

    private fun bindView() {
        mainRecyclerView = binding.mainRecyclerView
        selectedDate = binding.selectedDate
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
        btnCalender = binding.btnCalender
        txtView = binding.txtView
        reminders = ""
    }

    private fun setUpViewModel() {
        homeAdapter = HomeAdapter()
        val viewModelFactory = ViewModelFactory()
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        dialogBoxManager = DialogBoxManager(requireActivity())
    }

    private fun fetchReminders(date: String) {
        medicines = emptyList<Medicine>().also {
            homeAdapter.clearMecicines()
        }
        homeViewModel.getReminders(date) { reminders, errorMessage ->
            if (reminders != null) {
                try {
                    Log.d("reminders","$reminders")
                    val medicineList = mutableListOf<Medicine>()

                    val jsonArray = JSONArray(reminders)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val title = jsonObject.getString("title")
                        val message = jsonObject.getString("message")
                        val imageUrl = jsonObject.getString("imageUrl")
                        val startDate = jsonObject.getString("startDate")
                        val endDate = jsonObject.getString("endDate")
                        val foodBoolean = jsonObject.getBoolean("foodBoolean")
                        val dosePairsJson = jsonObject.getJSONObject("dosePairs")
                        val dosePairs = jsonToMap(dosePairsJson)
                        val requestCodePairsJson = jsonObject.getJSONObject("requestCodePair")
                        val requestCodePairs = jsonToMapInt(requestCodePairsJson)
                        val timePairsJson = jsonObject.getJSONObject("timePair")
                        val timePairs = jsonToMap(timePairsJson)

                        val medicine = Medicine(
                            title,
                            message,
                            imageUrl,
                            startDate,
                            endDate,
                            foodBoolean,
                            dosePairs,
                            requestCodePairs,
                            timePairs
                        )

                        medicineList.add(medicine)
                    }

                    medicines = medicineList
                    setUpRecyclerViews()
                    showOverlay(false)
                } catch (e: JSONException) {
                    showToast("Error parsing JSON data")
                    Log.e("ViewRemindersActivity", "Error parsing JSON data: ${e.message}")
                }
            } else {
                showToast(errorMessage)
                Log.e("ViewRemindersActivity", "Failed to fetch reminders: $errorMessage")
            }
        }
    }

    private fun jsonToMap(jsonObject: JSONObject): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next().toInt()
            val value = jsonObject.getString(key.toString())
            map[key] = value
        }
        return map
    }

    private fun jsonToMapInt(jsonObject: JSONObject): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next().toInt()
            val value = jsonObject.getString(key.toString())
            map[key] = value.toInt()
        }
        return map
    }

    private fun filterMedicinesByTime(timeSlot: Int): List<Medicine> {

        return medicines.filter { it.timePair.keys.contains(timeSlot) }
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
