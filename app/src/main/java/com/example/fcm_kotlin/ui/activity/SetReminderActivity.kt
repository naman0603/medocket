package com.example.fcm_kotlin.ui.activity

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.fcm_kotlin.databinding.ActivitySetReminderBinding
import com.example.fcm_kotlin.manager.DialogBoxManager
import com.example.fcm_kotlin.manager.ReminderManager
import com.example.fcm_kotlin.viewModel.ReminderViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SetReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetReminderBinding
    private lateinit var btnBack : ShapeableImageView
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var txtReminderTitle : AutoCompleteTextView
    private lateinit var txtNoOfDose:AutoCompleteTextView
    private lateinit var txtReminderMessage : TextInputEditText
    private lateinit var btnSelectTime : MaterialButton
    private lateinit var btnSelectDay : MaterialButton
    private lateinit var dialogBoxManager: DialogBoxManager
    private lateinit var btnRadioYes : RadioButton
    private lateinit var btnRadioNo : RadioButton
    private lateinit var btnSetReminder : MaterialButton
    private lateinit var spinKitView: SpinKitView
    private lateinit var txtTimeView1: TextView
    private lateinit var txtPillQuantity1: EditText
    private lateinit var txtTimeView2: TextView
    private lateinit var txtPillQuantity2: EditText
    private lateinit var txtTimeView3: TextView
    private lateinit var txtPillQuantity3: EditText
    private lateinit var txtTimeView4: TextView
    private lateinit var txtPillQuantity4: EditText
    private lateinit var txtTimeView5: TextView
    private lateinit var txtPillQuantity5: EditText
    private lateinit var overlayView: FrameLayout
    private lateinit var reminderManager : ReminderManager
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var medicineList: MutableList<String>
    private lateinit var timePairs: List<Pair<Int, Int>>
    private lateinit var doseList: List<Int>
    private var selectedTimeHour = ""
    private var selectedTimeMinute = ""
    private val selectedDates: MutableList<String> = mutableListOf()
    private var isDateSelected = false
    private var isTimeSelected = false
    private var isRecurring = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        setUpModel()
        bindView()
        fetchMedicineData()
        onClickListeners()
    }

    private fun onClickListeners() {
        btnSelectTime.setOnClickListener {
        }
        btnSelectDay.setOnClickListener {

/*
            dialogBoxManager.showDateRangePickerDialog { dates ->
                // Do something with the selected dates
                // For example, display them using a Toast
                dates.forEach{date ->
                    selectedDates.add(date)

                }
                isDateSelected = true
                val lastIndex = dates.lastIndex
                if(lastIndex == 0){
                    btnSelectDay.text = dates[0]
                }else{
                    val date = dates[0]+" TO "+dates[lastIndex]
                    btnSelectDay.text = date
                }

            }
*/

        }
        txtReminderTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterMedicines(s.toString())
            }
        })
        btnBack.setOnClickListener {
            finish()
        }
        btnSetReminder.setOnClickListener {
            if(validate()){
                showOverlay(true)
                setReminder()
               // setMedicene()
            }
        }


    }
    private fun filterMedicines(query: String) {
        val filteredMedicines = mutableListOf<String>()
        for (medicine in medicineList) {
            if (medicine.toLowerCase().contains(query.toLowerCase())) {
                filteredMedicines.add(medicine)
            }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, filteredMedicines)
        txtReminderTitle.setAdapter(adapter)
        txtReminderTitle.showDropDown()
    }

    private fun setMedicene() {
        val medicines  = listOf(
            "Tablet Acetazolamide 250 mg",
            "Tablet Acyclovir 200 mg",
            "Tablets Albendazole 400 mg",
            "Tablet Alprazolam 0.25 mg",
            "Tablet Amiodarone 200 mg",
            "Tablet Amitriptyline HCL 25 mg",
            "Tablet Amlodipine 5 mg",
            "Tablet Amlodipine 10 mg",
            "Tablet Amoxycillin And Potassium Clavulanate 625 mg",
            "Capsule Amoxycillin 500 mg",
            "Capsule Amoxycillin 250 mg",
            "Tablet Amoxycillin Trihydrate Dispersible 125 mg",
            "Tablet Ascorbic Acid (Chewable) 500 mg",
            "Tablet Atenolol 50 mg",
            "Tablet Azathioprine 50 mg",
            "Tablets Benzhexol (Trihexyphenidyl) 2 mg",
            "Tablets Bisacodyl 5 mg",
            "Tablets Calcium Gluconate 500 mg",
            "Tablet Carbamazepine 200 mg",
            "Tablet Cefadroxil Kid 125 mg",
            "Tablet Chlorine 0.5 gm with ISI mark",
            "Tablet Chloroquine Phosphate 250 mg",
            "Tablet Chloroquine Phosphate 500 mg",
            "Tablet Chlorpheniramine Maleate 4 mg",
            "Tablet Ciprofloxacin 500 mg",
            "Capsule Clindamycin 300 mg",
            "Tablet Clonidine 100 mcg",
            "Clotrimazole Vaginal Tablet 100 mg",
            "Tablet Dexamethasone 0.5 mg",
            "Tablet Diazepam 5 mg",
            "Tablet Diclofenac Sodium 50 mg",
            "Tablet Dicyclomine 20 mg",
            "Tablet Diethylcarbamazine 100 mg",
            "Tablet Digoxin 0.25 mg",
            "Tablet Domperidone 10 mg",
            "Capsule Doxycycline 100 mg",
            "Tablet Enalapril Maleate 5 mg",
            "Tablet Eteophylline with Theophylline (77 mg & 23 mg)",
            "Capsule Fluoxetine 20 mg",
            "Tablet Folic Acid & Ferrous Sulphate (Small)",
            "Tablet Folic Acid 5 mg",
            "Tablet Frusemide 40 mg",
            "Tablet Glipizide 5 mg",
            "Tablet Haloperidol 5 mg",
            "Tablet Hydrochlorthiazide 25 mg",
            "Tablet Ibuprofen 400 mg",
            "Tablet Imipramine HCL 25 mg",
            "Tablet Isosorbide Dinitrate 10 mg",
            "Tablet Isosorbide Mononitrate 10 mg",
            "Tablet Levodopa 100 mg + Carbidopa 10 mg",
            "Tablet Levodopa 250 mg and Carbidopa 25 mg",
            "Tablet Lithium Carbonate 300 mg",
            "Tablet Mefloquine 250 mg",
            "Tablet Metformin HCL SR 500 mg",
            "Tablet Methotrexate 2.5 mg",
            "Tablet Methyldopa 250 mg",
            "Tablet Methylergometrine Maleate 0.125 mg",
            "Tablet Metoclopramide 10 mg",
            "Tablet Metronidazole 400 mg",
            "Tablet Metronidazole 200 mg",
            "Tablet Norethisterone 5 mg",
            "Tablet Norfloxacin 400 mg",
            "Tablet Ofloxacin 200 mg",
            "Capsule Omeprazole 20 mg",
            "Tablet Paracetamol 500 mg",
            "Tablet Phenobarbitone 30 mg",
            "Tablet Phenytoin sodium 100 mg",
            "Tablet Prednisolone 5 mg",
            "Tablet Primaquine Phosphate 2.5 mg",
            "Tablet Primaquine Phosphate 7.5 mg",
            "Tablet Propranolol 40 mg",
            "Tablet Quinine Sulphate 300 mg",
            "Tablet Risperidone 2 mg",
            "Tablet Salbutamol 4 mg",
            "Tablet Sodium Valproate 200 mg",
            "Tablet Sodium Valproate SR/CR 500 mg",
            "Tablet Spironolactone 25 mg",
            "Tablet Trifluoperazine HCL 5 mg",
            "Tablet Verapamil Hydrochloride 40 mg",
            "Capsule Vitamin A 50000 I.U.",
            "Capsule Vitamin A 2 Lac I.U.",
            "Tablet Vitamin B Complex (Therapeutic)",
            "Tablet Fluconazole 50 mg",
            "Tablet Azithromycin 500 mg",
            "Tablet Amoxycillin & Clavulanic Acid Dispersible 228.5 mg",
            "Tablet Atorvastatin 10 mg",
            "Tablet Cefixime 200 mg",
            "Tablet Escitalopram Oxalate 10 mg",
            "Tablet Lorazepam 2 mg",
            "Tablet Metoprolol 25 mg",
            "Tablet Olanzapine 5 mg",
            "Tablet Omeprazole 20 mg",
            "Tablet Pantoprazole 40 mg",
            "Tablet Rosuvastatin 10 mg",
            "Tablet Sertraline 50 mg",
            "Tablet Sildenafil Citrate 50 mg",
            "Tablet Tadalafil 20 mg",
            "Tablet Valsartan 80 mg",
            "Tablet Vardenafil 20 mg",
            "Tablet Venlafaxine 37.5 mg",
            "Capsule Cefixime 200 mg",
            "Capsule Cefuroxime 250 mg",
            "Capsule Cefuroxime 500 mg",
            "Capsule Cephalexin 500 mg",
            "Capsule Co-Trimoxazole 480 mg (Sulphamethoxazole 400 mg + Trimethoprim 80 mg)",
            "Capsule Diclofenac Sodium 50 mg",
            "Capsule Doxycycline 100 mg",
            "Capsule Erythromycin 250 mg",
            "Capsule Erythromycin 500 mg",
            "Capsule Erythropoietin 4000 IU",
            "Capsule Flucanozole 150 mg",
            "Capsule Fluconazole 150 mg",
            "Capsule Fluconazole 200 mg",
            "Capsule Gabapentin 100 mg",
            "Capsule Gabapentin 300 mg",
            "Capsule Ibuprofen 200 mg",
            "Capsule Itraconazole 100 mg",
            "Capsule Itraconazole 200 mg",
            "Capsule Levofloxacin 500 mg",
            "Capsule Omeprazole 20 mg",
            "Capsule Omeprazole 20 mg + Domperidone 10 mg",
            "Capsule Pantoprazole 40 mg",
            "Capsule Pregabalin 75 mg",
            "Capsule Tamsulosin 0.4 mg",
            "Capsule Terbinafine 250 mg",
            "Capsule Vitamin E 200 mg",
            "Syrup Paracetamol 125 mg/5 ml",
            "Syrup Paracetamol 250 mg/5 ml",
            "Syrup Azithromycin 200 mg/5 ml",
            "Syrup Cefixime 50 mg/5 ml",
            "Syrup Co-Trimoxazole 120 mg (Sulphamethoxazole 100 mg + Trimethoprim 20 mg)/5 ml",
            "Syrup Co-Trimoxazole 240 mg (Sulphamethoxazole 200 mg + Trimethoprim 40 mg)/5 ml",
            "Syrup Dextromethorphan HBr 10 mg/5 ml",
            "Syrup Domperidone 1 mg/1 ml",
            "Syrup Iron 100 mg/5 ml",
            "Syrup Iron 50 mg/5 ml",
            "Syrup Paracetamol 125 mg/5 ml",
            "Syrup Zinc 20 mg/5 ml",
            "Eye Drop Bimatoprost 0.3 mg/ml",
            "Eye Drop Brimonidine 0.15 mg",
            "Eye Drop Dorzolamide 20 mg/ml",
            "Eye Drop Flurometholone 1 mg/ml",
            "Eye Drop Ketorolac 0.5 mg",
            "Eye Drop Latanoprost 0.05 mg/ml",
            "Eye Drop Ofloxacin 0.3 %",
            "Eye Drop Timolol Maleate 5 mg/ml",
            "Ointment Aciclovir 5 %",
            "Ointment Chloramphenicol 1 %",
            "Ointment Clotrimazole 1 %",
            "Ointment Fluorometholone 0.1 %",
            "Ointment Fusidic Acid 2 %",
            "Ointment Miconazole Nitrate 2 %",
            "Ointment Povidone Iodine 5 %",
            "Ointment Tacrolimus 0.03 %",
            "Ointment Tobramycin 0.3 %",
            "Injection Amikacin 100 mg",
            "Injection Amikacin 500 mg",
            "Injection Ampicillin 250 mg",
            "Injection Ampicillin 500 mg",
            "Injection Amphotericin B 50 mg",
            "Injection Amphotericin B 50 mg (Liposomal)",
            "Injection Artesunate 60 mg",
            "Injection Artemether 80 mg",
            "Injection Atropine 0.6 mg",
            "Injection Azithromycin 500 mg",
            "Injection Cefepime 1000 mg",
            "Injection Cefotaxime 1000 mg",
            "Injection Ceftriaxone 1000 mg",
            "Injection Ceftriaxone 250 mg",
            "Injection Ceftriaxone 500 mg",
            "Injection Cefuroxime 1500 mg",
            "Injection Cefuroxime 750 mg",
            "Injection Chloroquine 40 mg/ml",
            "Injection Chlorpromazine 50 mg/ml",
            "Injection Diazepam 5 mg/ml",
            "Injection Diclofenac Sodium 25 mg/ml",
            "Injection Diclofenac Sodium 75 mg/ml",
            "Injection Diclofenac Sodium 75 mg/ml",
            "Injection Dicyclomine 10 mg/ml",
            "Injection Digoxin 0.5 mg/ml",
            "Injection Diltiazem 5 mg/ml",
            "Injection Doxycycline 100 mg",
            "Injection Ephedrine 30 mg/ml",
            "Injection Epinephrine 1 mg/ml",
            "Injection Erythromycin 1 gm",
            "Injection Erythromycin 250 mg",
            "Injection Furosemide 20 mg/ml",
            "Injection Gentamicin 40 mg/ml",
            "Injection Haloperidol 5 mg/ml",
            "Injection Hydralazine 20 mg/ml",
            "Injection Hyoscine Butylbromide 20 mg/ml",
            "Injection Hydrocortisone 100 mg/ml",
            "Injection Hydrocortisone 100 mg/ml",
            "Injection Hydrocortisone 500 mg",
            "Injection Hydroxychloroquine 200 mg/ml",
            "Injection Hydroxyzine 25 mg/ml",
            "Injection Iron Sucrose 100 mg",
            "Injection Iron Sucrose 50 mg",
            "Injection Isoprenaline 0.2 mg/ml",
            "Injection Ivermectin 6 mg/ml",
            "Injection Ketoconazole 200 mg",
            "Injection Ketorolac 30 mg/ml",
            "Injection Levofloxacin 500 mg",
            "Injection Lignocaine 2 %",
            "Injection Lignocaine 2 %",
            "Injection Lignocaine 2 %",
            "Injection Lorazepam 2 mg/ml",
            "Injection Magnesium Sulphate 500 mg/ml",
            "Injection Methyl Ergometrine Maleate 0.2 mg/ml",
            "Injection Metoclopramide 10 mg/ml",
            "Injection Metronidazole 500 mg/100 ml",
            "Injection Metronidazole 500 mg/100 ml",
            "Injection Naloxone 0.4 mg/ml",
            "Injection Ondansetron 2 mg/ml",
            "Injection Oxytocin 5 IU/ml",
            "Injection Paracetamol 1000 mg",
            "Injection Paracetamol 150 mg/ml",
            "Injection Phenobarbitone 200 mg/ml",
            "Injection Phenobarbitone 200 mg/ml",
            "Injection Phenytoin sodium 50 mg/ml",
            "Injection Phenytoin sodium 50 mg/ml",
            "Injection Potassium Chloride 15 %",
            "Injection Progesterone 50 mg/ml",
            "Injection Promethazine 25 mg/ml",
            "Injection Rabeprazole 20 mg",
            "Injection Ringer Lactate",
            "Injection Salbutamol 500 mcg/ml",
            "Injection Sodium Bicarbonate 8.4 %",
            "Injection Streptomycin 1 gm",
            "Injection Streptomycin 500 mg",
            "Injection Succinylcholine 50 mg/ml",
            "Injection Terbutaline 1 mg/ml",
            "Injection Tranexamic Acid 100 mg/ml",
            "Injection Vasopressin 20 IU/ml",
            "Injection Vitamin A 100000 I.U.",
            "Injection Vitamin K 10 mg/ml",
            "Injection Vitamin K 2 mg/ml",
            "Injection Vitamin K 2 mg/ml",
            "Injection Zidovudine 10 mg/ml"
        )
        val medicineRef =  firestore.collection("medicines")
        medicines.forEach { medicine ->

           medicineRef.add(mapOf("name" to medicine)).addOnSuccessListener { documentRefrence ->
                val docId = documentRefrence.id
                documentRefrence.update(mapOf("docId" to docId)).addOnSuccessListener {
                }
            }
        }
        showToast("Successfully Added")
        showOverlay(false)
    }

    private fun fetchMedicineData() {
        firestore.collection("medicines")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val medicineName = document.getString("name")
                    if (medicineName != null) {
                        medicineList.add(medicineName)
                    }
                }
                // Notify adapter that data set has changed
                (txtReminderTitle.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching medicines: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setReminder() {
        val title = txtReminderTitle.text.toString()
        val message = txtReminderMessage.text.toString()
//        val hour = selectedTimeHour.toInt()
    //    val minute = selectedTimeMinute.toInt()
        val getSelectedListOfDates = getSelectedListOfDates(selectedDates)
        Log.d("Selected Dates", "$selectedDates,$getSelectedListOfDates")

    }

    private fun getSelectedListOfDates(selectedDates: MutableList<String>): MutableList<Date> {
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val dates: MutableList<Date> = mutableListOf()

        selectedDates.forEach { date ->
            dates.add(sdf.parse(date)!!)
        }
        return dates
    }

    private fun saveInFirebase(title: String, message: String, hour: Int, minute: Int, selectedDays: String, requestCode: List<Int>) {
    }


    private fun validate(): Boolean {
        if(txtReminderTitle.text.toString().isEmpty()||txtReminderMessage.text.toString().isEmpty()){
            showToast("Please Enter Title and Message")
            return false
        }else{
            return if(isTimeSelected && isDateSelected){
                if(btnRadioNo.isChecked || btnRadioYes.isChecked){
                    if(btnRadioYes.isChecked){
                        isRecurring = true
                    }
                    true
                }else{
                    //showToast("Please select whether it is recurring or not")
                    true
                    // change to false when logic for recurring is set
                }
            }else{
                showToast("Please Select Time and Date")
                false
            }
        }
    }


    private fun setUpModel() {
        dialogBoxManager = DialogBoxManager(this)
        reminderManager = ReminderManager(this)
        val viewModelFactory = ViewModelFactory()
        reminderViewModel = ViewModelProvider(this, viewModelFactory)[ReminderViewModel::class.java]
        firestore = FirebaseFirestore.getInstance()
        medicineList = mutableListOf()
        doseList = listOf(1,2,3,4,5)
    }

    private fun bindView() {
        btnBack = binding.btnBack
        lottieAnimationView = binding.lottieView
        txtReminderTitle = binding.txtReminderTitle
        txtReminderMessage = binding.txtReminderMessage
        btnSelectTime = binding.btnSelectTime
        btnSelectDay = binding.btnSelectDay
        btnRadioYes = binding.btnRadioYes
        btnRadioNo = binding.btnRadioNo
        spinKitView = binding.spinKit
        overlayView = binding.overlayView
        btnSetReminder = binding.btnSetReminder
        txtNoOfDose = binding.txtNoOfDose
        txtPillQuantity1 = binding.txtPillQuantity1
        txtPillQuantity2 = binding.txtPillQuantity2
        txtPillQuantity3 = binding.txtPillQuantity3
        txtPillQuantity4 = binding.txtPillQuantity4
        txtPillQuantity5 = binding.txtPillQuantity5
        txtTimeView1 = binding.txtTimeView1
        txtTimeView2 = binding.txtTimeView2
        txtTimeView3 = binding.txtTimeView3
        txtTimeView4 = binding.txtTimeView4
        txtTimeView5 = binding.txtTimeView5
        txtNoOfDose.inputType = InputType.TYPE_NULL

        val adapterMedicine = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, medicineList)
        val adapterDose = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, doseList)
        txtReminderTitle.setAdapter(adapterMedicine)
        txtNoOfDose.setAdapter(adapterDose)
        txtNoOfDose.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position)
            }
    }

    private fun updateTimeView(i: Int) {
        when (i) {
            1 ->{
                binding.linearLayoutScrollView1.visibility =View.VISIBLE
                binding.linearLayoutScrollView2.visibility =View.GONE
                binding.linearLayoutScrollView3.visibility =View.GONE
                binding.linearLayoutScrollView4.visibility =View.GONE
                binding.linearLayoutScrollView5.visibility =View.GONE

                txtTimeView1.text = timePairs[0].first.toString()+":"+timePairs[0].second.toString()
            }
            2 ->{
                binding.linearLayoutScrollView1.visibility =View.VISIBLE
                binding.linearLayoutScrollView2.visibility =View.VISIBLE
                binding.linearLayoutScrollView3.visibility =View.GONE
                binding.linearLayoutScrollView4.visibility =View.GONE
                binding.linearLayoutScrollView5.visibility =View.GONE
                txtTimeView1.text = timePairs[0].first.toString()+":"+timePairs[0].second.toString()
                txtTimeView2.text = timePairs[1].first.toString()+":"+timePairs[1].second.toString()

            }
            3 ->{
            binding.linearLayoutScrollView1.visibility =View.VISIBLE
            binding.linearLayoutScrollView2.visibility =View.VISIBLE
            binding.linearLayoutScrollView3.visibility =View.VISIBLE
            binding.linearLayoutScrollView4.visibility =View.GONE
            binding.linearLayoutScrollView5.visibility =View.GONE
                txtTimeView1.text = timePairs[0].first.toString()+":"+timePairs[0].second.toString()
                txtTimeView2.text = timePairs[1].first.toString()+":"+timePairs[1].second.toString()
                txtTimeView3.text = timePairs[2].first.toString()+":"+timePairs[2].second.toString()
            }
            4 ->{
            binding.linearLayoutScrollView1.visibility =View.VISIBLE
            binding.linearLayoutScrollView2.visibility =View.VISIBLE
            binding.linearLayoutScrollView3.visibility =View.VISIBLE
            binding.linearLayoutScrollView4.visibility =View.VISIBLE
            binding.linearLayoutScrollView5.visibility =View.GONE
                txtTimeView1.text = timePairs[0].first.toString()+":"+timePairs[0].second.toString()
                txtTimeView2.text = timePairs[1].first.toString()+":"+timePairs[1].second.toString()
                txtTimeView3.text = timePairs[2].first.toString()+":"+timePairs[2].second.toString()
                txtTimeView4.text = timePairs[3].first.toString()+":"+timePairs[3].second.toString()
            }
            5 ->{
            binding.linearLayoutScrollView1.visibility =View.VISIBLE
            binding.linearLayoutScrollView2.visibility =View.VISIBLE
            binding.linearLayoutScrollView3.visibility =View.VISIBLE
            binding.linearLayoutScrollView4.visibility =View.VISIBLE
            binding.linearLayoutScrollView5.visibility =View.VISIBLE

                txtTimeView1.text = timePairs[0].first.toString()+":"+timePairs[0].second.toString()
                txtTimeView2.text = timePairs[1].first.toString()+":"+timePairs[1].second.toString()
                txtTimeView3.text = timePairs[2].first.toString()+":"+timePairs[2].second.toString()
                txtTimeView4.text = timePairs[3].first.toString()+":"+timePairs[3].second.toString()
                txtTimeView5.text = timePairs[4].first.toString()+":"+timePairs[4].second.toString()
            }
        }
    }

    private fun showToast(msg: String) {
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