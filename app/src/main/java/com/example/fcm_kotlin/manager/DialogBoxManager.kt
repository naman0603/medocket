package com.example.fcm_kotlin.manager

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.fcm_kotlin.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DialogBoxManager(private val context: FragmentActivity) {

    fun showTimePickerDialog(
        timeConstraint: Int,
        lastSelectedHour: Int?,
        lastSelectedMinute: Int?,
        onTimeSelected: (hourOfDay: Int, minute: Int) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val hour = lastSelectedHour ?: calendar.get(Calendar.HOUR_OF_DAY)
        val minute = lastSelectedMinute ?: calendar.get(Calendar.MINUTE)

        val startTime: Pair<Int, Int>
        val endTime: Pair<Int, Int>

        when (timeConstraint) {
            1 -> {
                startTime = Pair(6, 0)
                endTime = Pair(11, 59)
            }
            2 -> {
                startTime = Pair(12, 0)
                endTime = Pair(17, 59)
            }
            3 -> {
                startTime = Pair(18, 0)
                endTime = Pair(23, 59)
            }
            else -> {
                startTime = Pair(0, 0)
                endTime = Pair(23, 59)
            }
        }

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                if ((hourOfDay > startTime.first || (hourOfDay == startTime.first && minute >= startTime.second))
                    && (hourOfDay < endTime.first || (hourOfDay == endTime.first && minute <= endTime.second))
                ) {
                    onTimeSelected(hourOfDay, minute)
                } else {
                    when(timeConstraint){
                        1 ->{
                            onTimeSelected(12,0)
                            showToast("Select between 06:00 and 11:59")
                        }
                        2 ->{
                            onTimeSelected(12,0)
                            showToast("Select between 12:00 and 17:59")
                        }
                        3 ->{
                            onTimeSelected(12,0)
                            showToast("Select between 18:00 and 05:59")
                        }
                    }
                }
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
    fun showDatePickerDialog(minDate: String, onDateSelected: (date: String) -> Unit) {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        val datePickerDialog = DatePickerDialog(
            context,
            R.style.ThemeMaterialCalender,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val formattedDate = sdf.format(selectedDate.time)
                onDateSelected(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        if (minDate == "0") {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        } else {
            val minDateMillis = sdf.parse(minDate)?.time ?: 0L
            val cal = Calendar.getInstance().apply {
                timeInMillis = minDateMillis
                add(Calendar.DAY_OF_MONTH, 1) // Adding one day
            }
            datePickerDialog.datePicker.minDate = cal.timeInMillis
        }

        datePickerDialog.show()
    }

    fun showCustomSetMessageDialog(title: String, hour: Int, minute: Int, days: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_confirm_msg_layout, null)


        val dialogMessage : TextView = dialogView.findViewById(R.id.textView2)

        val msg = "Reminder is set for <b>$title</b> at <b>$hour:$minute</b> on <b>$days</b>"
        dialogMessage.text = Html.fromHtml(msg)
        dialogBuilder.setView(dialogView).setCancelable(false)
        dialogBuilder.setPositiveButton("OK") { _, _ ->
         context.finish()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
    fun showAddMedicineDialogBox( onMedicineAdded: (added: Boolean) -> Unit){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.custom_add_medicine_dialog_box, null)
        val medicinetype : MaterialAutoCompleteTextView = dialogView.findViewById(R.id.medicineTypeMedicineDialogBox)
        val medicineName : TextInputEditText = dialogView.findViewById(R.id.txtMedicineNameMedicineDialog)
        val medicinePower : TextInputEditText = dialogView.findViewById(R.id.txtMedicinePowerMedicineDialog)
        val medicineButton : MaterialButton = dialogView.findViewById(R.id.btnAddMedicineMedicineDialog)

        val typeList = listOf("Capsule","Eye Drop","Tablet","Injection","Syrup","Ointment")
        val adapterType = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, typeList)
        medicinetype.setAdapter(adapterType)
        medicinetype.isFocusable = false
        val alertDialog = dialogBuilder.setView(dialogView).setCancelable(true).create()
        alertDialog.show()
        medicineButton.setOnClickListener {
            if(validateMedicineDialog(medicinetype.text.toString(),medicineName.text.toString(),medicinePower.text.toString())){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Confirm Details")
                builder.setMessage("Once added medicine details cannot be changed")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { _, _ ->
                    val added = addMedicine(medicinetype.text.toString(),medicineName.text.toString(),medicinePower.text.toString())
                    if (added) {
                        onMedicineAdded(true)
                        alertDialog.dismiss() // Dismiss the dialog after successful addition

                    }

                }
               builder.setNegativeButton("No"){ _,_->

               }
                val dialog = builder.create()
                dialog.show()
            }
        }
        dialogBuilder.setView(dialogView).setCancelable(false)

    }

    fun showGetPasswordDialogBox(onGetPassword : (password: String) -> Unit){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.custom_password_dialog, null)

        val dialogPassword : TextView = dialogView.findViewById(R.id.txtDialogPassword)
        val dialogBtnSubmit : MaterialButton = dialogView.findViewById(R.id.btnSubmitDialog)

        dialogBuilder.setView(dialogView).setCancelable(false)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogBtnSubmit.setOnClickListener {
            if(dialogPassword.text.toString().isEmpty()){
                showToast("Please enter password")

            }else {
                onGetPassword(dialogPassword.text.toString())
                alertDialog.dismiss() // Dismiss the dialog after successful addition
            }
        }
    }

    private fun addMedicine(medicineType: String, medicineName: String, medicinePower: String): Boolean {

        val medicineData = hashMapOf(
            "medicineType" to medicineType,
            "medicineName" to medicineName,
            "medicinePower" to medicinePower

        )
         val medicineRef =  FirebaseFirestore.getInstance().collection("medicines")
       medicineRef.add(medicineData).addOnSuccessListener { documentReference ->
           val docId = documentReference.id
           documentReference.update(mapOf("docId" to docId)).addOnSuccessListener {
               Log.d("docId",docId)
               showToast("Medicine Added")
           }
       }.also {
           return true
       }

        /*val medicines = listOf(
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Acetazolamide",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Acyclovir",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablets",
                "medicineName" to "Albendazole",
                "medicinePower" to "400 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Alprazolam",
                "medicinePower" to "0.25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Amiodarone",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Amitriptyline HCL",
                "medicinePower" to "25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Amlodipine",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Amlodipine",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Amoxycillin + Potassium Clavulanate",
                "medicinePower" to "625 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Amoxycillin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Amoxycillin",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Amoxycillin Trihydrate Dispersible",
                "medicinePower" to "125 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Ascorbic Acid (Chewable)",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Atenolol",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Azathioprine",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablets",
                "medicineName" to "Benzhexol (Trihexyphenidyl)",
                "medicinePower" to "2 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablets",
                "medicineName" to "Bisacodyl",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablets",
                "medicineName" to "Calcium Gluconate",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Carbamazepine",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Cefadroxil Kid",
                "medicinePower" to "125 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Chlorine",
                "medicinePower" to "0.5 gm with ISI mark"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Chloroquine Phosphate",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Chloroquine Phosphate",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Chlorpheniramine Maleate",
                "medicinePower" to "4 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Ciprofloxacin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Clindamycin",
                "medicinePower" to "300 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Clonidine",
                "medicinePower" to "100 mcg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Clotrimazole",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Dexamethasone",
                "medicinePower" to "0.5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Diazepam",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Diclofenac Sodium",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Dicyclomine",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Diethylcarbamazine",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Digoxin",
                "medicinePower" to "0.25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Domperidone",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Doxycycline",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Enalapril Maleate",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Eteophylline + Theophylline",
                "medicinePower" to "77 mg + 23 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Fluoxetine",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Folic Acid & Ferrous Sulphate",
                "medicinePower" to "Small"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Folic Acid",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Frusemide",
                "medicinePower" to "40 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Glipizide",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Haloperidol",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Hydrochlorthiazide",
                "medicinePower" to "25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Ibuprofen",
                "medicinePower" to "400 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Imipramine HCL",
                "medicinePower" to "25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Isosorbide Dinitrate",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Isosorbide Mononitrate",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Levodopa + Carbidopa",
                "medicinePower" to "100 mg + 10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Levodopa + Carbidopa",
                "medicinePower" to "250 + 25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Ciprofloxacin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Clindamycin",
                "medicinePower" to "300 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Clonidine",
                "medicinePower" to "100 mcg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Clotrimazole Vaginal",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Dexamethasone",
                "medicinePower" to "0.5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Diazepam",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Diclofenac Sodium",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Dicyclomine",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Diethylcarbamazine",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Digoxin",
                "medicinePower" to "0.25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Domperidone",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Doxycycline",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Enalapril Maleate",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Eteophylline + Theophylline",
                "medicinePower" to "77 mg + 23 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Fluoxetine",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Folic Acid & Ferrous Sulphate",
                "medicinePower" to "Small"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Folic Acid",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Frusemide",
                "medicinePower" to "40 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Glipizide",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Haloperidol",
                "medicinePower" to "5 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Hydrochlorthiazide",
                "medicinePower" to "25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Ibuprofen",
                "medicinePower" to "400 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Imipramine HCL",
                "medicinePower" to "25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Isosorbide Dinitrate",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Isosorbide Mononitrate",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Levodopa + Carbidopa",
                "medicinePower" to "100 mg + 10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Levodopa + Carbidopa",
                "medicinePower" to "250 mg + 25 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Omeprazole",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Pantoprazole",
                "medicinePower" to "40 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Rosuvastatin",
                "medicinePower" to "10 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Sertraline",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Sildenafil Citrate",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Tadalafil",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Valsartan",
                "medicinePower" to "80 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Vardenafil",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Tablet",
                "medicineName" to "Venlafaxine",
                "medicinePower" to "37.5 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Cefixime",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Cefuroxime",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Cefuroxime",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Cephalexin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Co-Trimoxazole + Sulphamethoxazole + Trimethoprim",
                "medicinePower" to "(480 mg + 400 mg + 80 mg)"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Diclofenac Sodium",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Doxycycline",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Erythromycin",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Erythromycin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Erythropoietin",
                "medicinePower" to "4000 IU"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Flucanozole",
                "medicinePower" to "150 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Fluconazole",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Gabapentin",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Gabapentin",
                "medicinePower" to "300 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Ibuprofen",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Itraconazole",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Itraconazole",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Levofloxacin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Omeprazole",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Omeprazole + Domperidone",
                "medicinePower" to "20 mg + 10 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Pantoprazole",
                "medicinePower" to "40 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Pregabalin",
                "medicinePower" to "75 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Tamsulosin",
                "medicinePower" to "0.4 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Terbinafine",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Capsule",
                "medicineName" to "Vitamin E",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Paracetamol",
                "medicinePower" to "125 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Paracetamol",
                "medicinePower" to "250 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Azithromycin",
                "medicinePower" to "200 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Cefixime",
                "medicinePower" to "50 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Co-Trimoxazole + Sulphamethoxazole + Trimethoprim",
                "medicinePower" to "(120 mg + 100 mg +  20 mg)/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Co-Trimoxazole + Sulphamethoxazole + Trimethoprim",
                "medicinePower" to "(240 mg + 200 mg +  40 mg)/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Dextromethorphan HBr",
                "medicinePower" to "10 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Domperidone",
                "medicinePower" to "1 mg/1 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Iron",
                "medicinePower" to "100 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Iron",
                "medicinePower" to "50 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Paracetamol",
                "medicinePower" to "125 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Syrup",
                "medicineName" to "Zinc",
                "medicinePower" to "20 mg/5 ml"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Bimatoprost",
                "medicinePower" to "0.3 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Brimonidine",
                "medicinePower" to "0.15 mg"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Dorzolamide",
                "medicinePower" to "20 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Flurometholone",
                "medicinePower" to "1 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Ketorolac",
                "medicinePower" to "0.5 mg"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Latanoprost",
                "medicinePower" to "0.05 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Ofloxacin",
                "medicinePower" to "0.3 %"
            ),
            hashMapOf(
                "medicineType" to "Eye Drop",
                "medicineName" to "Timolol Maleate",
                "medicinePower" to "5 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Aciclovir",
                "medicinePower" to "5 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Chloramphenicol",
                "medicinePower" to "1 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Clotrimazole",
                "medicinePower" to "1 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Fluorometholone",
                "medicinePower" to "0.1 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Fusidic Acid",
                "medicinePower" to "2 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Miconazole Nitrate",
                "medicinePower" to "2 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Povidone Iodine",
                "medicinePower" to "5 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Tacrolimus",
                "medicinePower" to "0.03 %"
            ),
            hashMapOf(
                "medicineType" to "Ointment",
                "medicineName" to "Tobramycin",
                "medicinePower" to "0.3 %"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Amikacin",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Amikacin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ampicillin",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ampicillin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Amphotericin B",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Amphotericin B",
                "medicinePower" to "50 mg (Liposomal)"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Artesunate",
                "medicinePower" to "60 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Artemether",
                "medicinePower" to "80 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Atropine",
                "medicinePower" to "0.6 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Azithromycin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Cefepime",
                "medicinePower" to "1000 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Cefotaxime",
                "medicinePower" to "1000 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ceftriaxone",
                "medicinePower" to "1000 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ceftriaxone",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ceftriaxone",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Cefuroxime",
                "medicinePower" to "1500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Cefuroxime",
                "medicinePower" to "750 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Chloroquine",
                "medicinePower" to "40 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Chlorpromazine",
                "medicinePower" to "50 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Diazepam",
                "medicinePower" to "5 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Diclofenac Sodium",
                "medicinePower" to "25 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Diclofenac Sodium",
                "medicinePower" to "75 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Diclofenac Sodium",
                "medicinePower" to "75 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Dicyclomine",
                "medicinePower" to "10 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Digoxin",
                "medicinePower" to "0.5 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Diltiazem",
                "medicinePower" to "5 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Doxycycline",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ephedrine",
                "medicinePower" to "30 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Epinephrine",
                "medicinePower" to "1 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Erythromycin",
                "medicinePower" to "1 gm"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Erythromycin",
                "medicinePower" to "250 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Furosemide",
                "medicinePower" to "20 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Gentamicin",
                "medicinePower" to "40 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Haloperidol",
                "medicinePower" to "5 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hydralazine",
                "medicinePower" to "20 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hyoscine Butylbromide",
                "medicinePower" to "20 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hydrocortisone",
                "medicinePower" to "100 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hydrocortisone",
                "medicinePower" to "100 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hydrocortisone",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hydroxychloroquine",
                "medicinePower" to "200 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Hydroxyzine",
                "medicinePower" to "25 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Iron Sucrose",
                "medicinePower" to "100 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Iron Sucrose",
                "medicinePower" to "50 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Isoprenaline",
                "medicinePower" to "0.2 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ivermectin",
                "medicinePower" to "6 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ketoconazole",
                "medicinePower" to "200 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ketorolac",
                "medicinePower" to "30 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Levofloxacin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Lignocaine",
                "medicinePower" to "2 %"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Lignocaine",
                "medicinePower" to "2 %"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Lignocaine",
                "medicinePower" to "2 %"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Lorazepam",
                "medicinePower" to "2 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Magnesium Sulphate",
                "medicinePower" to "500 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Methyl Ergometrine Maleate",
                "medicinePower" to "0.2 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Metoclopramide",
                "medicinePower" to "10 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Metronidazole",
                "medicinePower" to "500 mg/100 ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Metronidazole",
                "medicinePower" to "500 mg/100 ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Naloxone",
                "medicinePower" to "0.4 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ondansetron",
                "medicinePower" to "2 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Oxytocin",
                "medicinePower" to "5 IU/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Paracetamol",
                "medicinePower" to "1000 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Paracetamol",
                "medicinePower" to "150 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Phenobarbitone",
                "medicinePower" to "200 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Phenobarbitone",
                "medicinePower" to "200 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Phenytoin sodium",
                "medicinePower" to "50 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Phenytoin sodium",
                "medicinePower" to "50 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Potassium Chloride",
                "medicinePower" to "15 %"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Progesterone",
                "medicinePower" to "50 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Promethazine",
                "medicinePower" to "25 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Rabeprazole",
                "medicinePower" to "20 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Ringer Lactate",
                "medicinePower" to ""
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Salbutamol",
                "medicinePower" to "500 mcg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Sodium Bicarbonate",
                "medicinePower" to "8.4 %"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Streptomycin",
                "medicinePower" to "1 gm"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Streptomycin",
                "medicinePower" to "500 mg"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Succinylcholine",
                "medicinePower" to "50 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Terbutaline",
                "medicinePower" to "1 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Tranexamic Acid",
                "medicinePower" to "100 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Vasopressin",
                "medicinePower" to "20 IU/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Vitamin A",
                "medicinePower" to "100000 I.U."
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Vitamin K",
                "medicinePower" to "10 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Vitamin K",
                "medicinePower" to "2 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Vitamin K",
                "medicinePower" to "2 mg/ml"
            ),
            hashMapOf(
                "medicineType" to "Injection",
                "medicineName" to "Zidovudine",
                "medicinePower" to "10 mg/ml"
            )
        )
        medicines.forEach { medicine ->
            val medicineRef =  FirebaseFirestore.getInstance().collection("medicines")
            medicineRef.add(medicine).addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                documentReference.update(mapOf("docId" to docId)).addOnSuccessListener {
                    Log.d("docId",docId)
                }
            }
        }
        return true*/

    }

    private fun validateMedicineDialog(medicinetype: String, medicineName: String, medicinePower: String): Boolean{
        if(medicinetype.isEmpty() || medicineName.isEmpty() || medicinePower.isEmpty()){
            showToast("Please fill all the fields")
            return false
        }
        return true
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
