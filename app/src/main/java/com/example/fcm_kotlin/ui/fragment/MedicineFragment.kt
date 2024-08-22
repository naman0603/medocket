
package com.example.fcm_kotlin.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.R
import com.example.fcm_kotlin.dataClass.MedicineData
import com.example.fcm_kotlin.databinding.FragmentMedicineBinding
import com.example.fcm_kotlin.manager.DialogBoxManager
import com.example.fcm_kotlin.ui.adapter.MedicineAdapter
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class MedicineFragment : Fragment() {
    private lateinit var binding:FragmentMedicineBinding
    private lateinit var context: Context
    private lateinit var searchView : SearchView
    private lateinit var recyclerView : RecyclerView
    private lateinit var btnAdd : FloatingActionButton
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout
    private lateinit var medicineList: MutableList<MedicineData>
    private lateinit var filteredMedicineList: MutableList<MedicineData>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: MedicineAdapter
    private lateinit var dialogBoxManager: DialogBoxManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMedicineBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        showOverlay(true)
        fetchMedicineData()
    }

    private fun initView() {
        setUpModels()
        bindViews()
        setUpRecyclerView()
        setUpSearchView()
        onClickListeners()
    }

    private fun onClickListeners() {
        btnAdd.setOnClickListener {
            showOverlay(true)
            dialogBoxManager.showAddMedicineDialogBox { added ->
                if (added) {
                    fetchMedicineData()
                }
            }
            showOverlay(false)
        }
    }

    private fun setUpModels() {
        firestore = FirebaseFirestore.getInstance()
        medicineList = mutableListOf()
        filteredMedicineList = mutableListOf()
        dialogBoxManager = DialogBoxManager(requireActivity())
    }

    private fun bindViews() {
        searchView = binding.searchView
        recyclerView = binding.recyclerView
        btnAdd = binding.btnAdd
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MedicineAdapter(filteredMedicineList)
        recyclerView.adapter = adapter
    }

    private fun setUpSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterMedicines(it) }
                return true
            }
        })
    }

    private fun fetchMedicineData() {
        firestore.collection("medicines")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val medicineType = document.getString("medicineType")
                    val medicineName = document.getString("medicineName")
                    val medicinePower = document.getString("medicinePower")

                    Log.d("medicineList", "$medicineName")
                    if (medicineName != null && medicinePower != null && medicineType != null) {
                        val medicine = MedicineData(medicineName, medicinePower, medicineType)
                        medicineList.add(medicine)
                        filteredMedicineList.add(medicine)
                    }
                }
                showOverlay(false)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching medicines: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun filterMedicines(query: String) {
        filteredMedicineList.clear()
        for (medicine in medicineList) {
            if (medicine.medicineName.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                medicine.medicinePower.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                medicine.medicineType.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            ) {
                filteredMedicineList.add(medicine)
            }
        }
        adapter.notifyDataSetChanged()
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