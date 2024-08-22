package com.example.fcm_kotlin.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fcm_kotlin.constructor.CustomDropdown
import com.example.fcm_kotlin.databinding.FragmentAddBinding
import com.example.fcm_kotlin.manager.DialogBoxManager
import com.example.fcm_kotlin.manager.PermissionManager
import com.example.fcm_kotlin.manager.ReminderManager
import com.example.fcm_kotlin.utils.CommonUtils
import com.example.fcm_kotlin.viewModel.ReminderViewModel
import com.example.fcm_kotlin.viewModel.ViewModelFactory
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.Locale


class AddFragment : Fragment() {
    private lateinit var binding:FragmentAddBinding
    private lateinit var context: Context
    private lateinit var txtReminderTitle : AutoCompleteTextView
    private lateinit var txtReminderMessage : TextInputEditText
    private lateinit var checkBoxMorning : CheckBox
    private lateinit var checkBoxAfterNoon : CheckBox
    private lateinit var checkBoxNight : CheckBox
    private lateinit var txtMorningTime : MaterialTextView
    private lateinit var txtAfterNoonTime : MaterialTextView
    private lateinit var txtEveningTime : MaterialTextView
    private lateinit var txtMorningDose : AutoCompleteTextView
    private lateinit var txtAfterNoonDose : AutoCompleteTextView
    private lateinit var txtNightDose : AutoCompleteTextView
    private lateinit var btnBeforeLunch : RadioButton
    private lateinit var btnAfterLunch : RadioButton
    private lateinit var btnSingleTime: SwitchCompat
    private lateinit var txtStartDate : MaterialTextView
    private lateinit var btnStartDate : MaterialButton
    private lateinit var txtEndDate : MaterialTextView
    private lateinit var btnEndDate : MaterialButton
    private lateinit var imageView: ShapeableImageView
    private lateinit var btnCaptureImage : MaterialButton
    private lateinit var btnSetReminder : MaterialButton
    private lateinit var spinKitView: SpinKitView
    private lateinit var overlayView: FrameLayout
    private lateinit var reminderManager : ReminderManager
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var firestore: FirebaseFirestore
    private lateinit var medicineList: MutableList<String>
    private lateinit var dialogBoxManager: DialogBoxManager
    private lateinit var permissionManager: PermissionManager
    private lateinit var commonUtils: CommonUtils
    private  var datesList : MutableList<String> = emptyList<String>().toMutableList()
    private var foodBoolean = false
    private var morningTimeBoolean = false
    private var afternoonTimeBoolean = false
    private var nightTimeBoolean = false
    private var imageSelectedBoolean = false
    private  var timePairs: MutableList<Pair<Int,Pair<Int, Int>>> = emptyList<Pair<Int,Pair<Int, Int>>>().toMutableList()
    private  var dosePairs: MutableList<Pair<Int, String>> = emptyList<Pair<Int, String>>().toMutableList()
    private  var startDate : String = ""
    private  var endDate : String = ""
    private  var imageUri:Uri = Uri.parse("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater)
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
        val currentTimeMillis = System.currentTimeMillis()
        Log.d("currentTimeMillis", "$currentTimeMillis")
        setUpModel()
        bindViews()
        fetchMedicineData()
        onClickListeners()

    }

    private fun onClickListeners() {
        txtReminderTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterMedicines(s.toString())
            }
        })

        checkBoxMorning.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.morningLinearLayout.visibility = View.VISIBLE
            }else{
                binding.morningLinearLayout.visibility = View.GONE
            }
        }
        checkBoxNight.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.nightLinearLayout.visibility = View.VISIBLE

            }else{
                binding.nightLinearLayout.visibility = View.GONE

            }
        }
        checkBoxAfterNoon.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.afterNoonLinearLayout.visibility = View.VISIBLE

            }else{
                binding.afterNoonLinearLayout.visibility = View.GONE

            }
        }
        btnSingleTime.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.linearLayout6.visibility = View.GONE
                btnStartDate.text = "Select Date"
            }else{
                binding.linearLayout6.visibility = View.VISIBLE
                btnStartDate.text = "Start Date"
            }
        }
        btnBeforeLunch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                foodBoolean = false
            }
        }
        btnAfterLunch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                foodBoolean = true
            }
        }
        btnStartDate.setOnClickListener {
            dialogBoxManager.showDatePickerDialog("0"){ date ->
                txtStartDate.text = date
                startDate = date
                txtEndDate.text = ""
            }
        }
        btnEndDate.setOnClickListener {
            if(!btnSingleTime.isChecked && startDate!=""){
                dialogBoxManager.showDatePickerDialog(startDate){ date ->
                    txtEndDate.text = date
                    endDate = date
                }
            }else{
                showToast("Please select Start Date First")
            }
        }
        btnCaptureImage.setOnClickListener {
            val permission = Manifest.permission.CAMERA
            if(permissionManager.isPermissionGranted(permission)){
                openCameraOrGallery()
            }else{
                permissionManager.requestPermission(permission)
            }
        }
        txtMorningTime.setOnClickListener {
            val timeText = txtMorningTime.text.toString()
            val parts = timeText.split(":")
            val hours = parts[0].toIntOrNull() ?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

            dialogBoxManager.showTimePickerDialog(1,hours,minute){ hour,min ->
                txtMorningTime.text = "$hour:$min"
                morningTimeBoolean = true
            }
        }
        txtAfterNoonTime.setOnClickListener {
            val timeText = txtMorningTime.text.toString()
            val parts = timeText.split(":")
            val hours = parts[0].toIntOrNull() ?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            dialogBoxManager.showTimePickerDialog(2,hours,minute){ hour,min ->
                txtAfterNoonTime.text = "$hour:$min"
                afternoonTimeBoolean = true
            }
        }
        txtEveningTime.setOnClickListener {
            val timeText = txtMorningTime.text.toString()
            val parts = timeText.split(":")
            val hours = parts[0].toIntOrNull() ?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            dialogBoxManager.showTimePickerDialog(3,hours,minute){ hour,min ->
                txtEveningTime.text = "$hour:$min"
                nightTimeBoolean = true
            }
        }
        btnSetReminder.setOnClickListener {
            getTimePairs()
            getDatesList()
            getDoseList()
            if(validation()){
                showOverlay(true)
                saveInFirebase()
                resetFragment()
                //reloadFragment()
                showToast("Reminder Set")
            }
        }


    }
    private fun resetFragment() {
        txtReminderTitle.text = null
        txtReminderMessage.text = null

        checkBoxMorning.isChecked = false
        checkBoxAfterNoon.isChecked = false
        checkBoxNight.isChecked = false

        binding.morningLinearLayout.visibility = View.GONE
        binding.afterNoonLinearLayout.visibility = View.GONE
        binding.nightLinearLayout.visibility = View.GONE

        txtMorningTime.text = "00:00"
        txtAfterNoonTime.text = "00:00"
        txtEveningTime.text = "00:00"

        txtMorningDose.setText("1 dose", false)
        txtAfterNoonDose.setText("1 dose", false)
        txtNightDose.setText("1 dose", false)

        btnBeforeLunch.isChecked = false
        btnAfterLunch.isChecked = false
        btnSingleTime.isChecked = false

        txtStartDate.text = ""
        txtEndDate.text = ""

        imageView.visibility = View.GONE
        imageUri = Uri.parse("")
        imageSelectedBoolean = false

        startDate = ""
        endDate = ""

        // Reset any other necessary variables
        timePairs.clear()
        dosePairs.clear()
        datesList.clear()
        morningTimeBoolean = false
        afternoonTimeBoolean = false
        nightTimeBoolean = false
        foodBoolean = false
    }
    private fun reloadFragment() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.detach(this).attach(this).commit()
    }

    private fun saveInFirebase() {
        if (imageSelectedBoolean){
            reminderViewModel.uploadImageToFirebase(imageUri){isSuccess,url ->
                if(isSuccess){
                    val requestCodePair = reminderManager.setReminder(timePairs, datesList, dosePairs, txtReminderTitle.text.toString(), txtReminderMessage.text.toString(), foodBoolean)
                    reminderViewModel.saveReminder(txtReminderTitle.text.toString(), txtReminderMessage.text.toString(),timePairs,dosePairs,url.toString(),startDate,endDate,foodBoolean,requestCodePair){ onSuccess,msg ->
                        Log.d("onSuccess","$onSuccess")
                        if(onSuccess){

                            showOverlay(false).also {
                                onViewCreated(view!!, null) // Reset the view
                            }
                        }else{
                            showToast(msg.toString())
                        }
                    }
                }else{
                    showToast("Error!!!!\nPlease try again")
                    showOverlay(false)
                }
            }
        }else{
            val requestCodePair = reminderManager.setReminder(timePairs, datesList, dosePairs, txtReminderTitle.text.toString(), txtReminderMessage.text.toString(), foodBoolean)
            reminderViewModel.saveReminder(txtReminderTitle.text.toString(), txtReminderMessage.text.toString(),timePairs,dosePairs,"No Photo Selected",startDate,endDate,foodBoolean,requestCodePair){ onSuccess,msg ->
                if(onSuccess){
                    showOverlay(false).also {
                        onViewCreated(view!!, null) // Reset the view
                    }
                }else{
                    showToast(msg.toString())
                }
            }
        }
        Log.d("ImageURI", "$imageUri")
    }

    private fun validation(): Boolean {
        val reminderTitle = txtReminderTitle.text.toString().trim()
        val reminderMessage = txtReminderMessage.text.toString().trim()

        if(reminderTitle.isEmpty()){
            showToast("Please choose medicine name ")
            return false
        }else{
            if(reminderMessage.isEmpty()){
                showToast("Write medicine description")
                return false
            }else{
                if (checkBoxMorning.isChecked || checkBoxAfterNoon.isChecked || checkBoxNight.isChecked) {
                    return if(startDate==""){
                        showToast("Please select a start date")
                        false
                    }else{
                        if(!btnSingleTime.isChecked && endDate==""){
                            showToast("Please select a end date")
                            false
                        }else{
                            if (checkBoxMorning.isChecked && !morningTimeBoolean) {
                                showToast("Please select morning time")
                                return false
                            }
                            if (checkBoxAfterNoon.isChecked && !afternoonTimeBoolean) {
                                showToast("Please select afternoon time")
                                return false
                            }
                            if (checkBoxNight.isChecked && !nightTimeBoolean) {
                                showToast("Please select night time")
                                return false
                            }
                            true
                        }
                    }
                }else{
                    showToast("Please select at least one time")
                    return false
                }
            }
        }

    }


    private fun getDoseList() {
        dosePairs.clear()
        if(checkBoxNight.isChecked){
            val dose  = txtNightDose.text.toString()
            dosePairs.add(Pair(3,dose))
        }
        if(checkBoxMorning.isChecked){
            val dose = txtMorningDose.text.toString()
            dosePairs.add(Pair(1,dose))
        }
        if (checkBoxAfterNoon.isChecked){
            val dose  = txtAfterNoonDose.text.toString()
            dosePairs.add(Pair(2,dose))
        }
    }

    private fun getDatesList() {
        datesList.clear()
        if(!btnSingleTime.isChecked){
            if(startDate!="" && endDate!=""){
                datesList = commonUtils.getDates(startDate, endDate)
            }
        }else{
            datesList.add(startDate)
        }
    }

    private fun getTimePairs() {
        timePairs.clear()
        if(checkBoxMorning.isChecked){
            val timeText = txtMorningTime.text.toString()
            val parts = timeText.split(":")
            val hours = parts[0].toIntOrNull() ?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val pair = Pair(hours, minute)
            timePairs.add(Pair(1,pair))
        }
        if(checkBoxAfterNoon.isChecked){
            val timeText = txtAfterNoonTime.text.toString()
            val parts = timeText.split(":")
            val hours = parts[0].toIntOrNull()?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull()?: 0
            val pair = Pair(hours, minute)
            timePairs.add(Pair(2,pair))
        }
        if(checkBoxNight.isChecked){
            val timeText = txtEveningTime.text.toString()
            val parts = timeText.split(":")
            val hours = parts[0].toIntOrNull()?: 0
            val minute = parts.getOrNull(1)?.toIntOrNull()?: 0
            val pair = Pair(hours, minute)
            timePairs.add(Pair(3,pair))
        }
    }

    private fun openCameraOrGallery() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        val chooserIntent = Intent.createChooser(pickPhotoIntent, "Select Image Source")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))

        startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE)
    }


    private fun filterMedicines(query: String) {
        val filteredMedicines = mutableListOf<String>()
        for (medicine in medicineList) {
            if (medicine.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))) {
                filteredMedicines.add(medicine)
            }
        }
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, filteredMedicines)
        txtReminderTitle.setAdapter(adapter)
    }

    private fun fetchMedicineData() {
        firestore.collection("medicines")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val medicineName = document.getString("medicineName")
                    val medicinePower = document.getString("medicinePower")
                    val medicineType = document.getString("medicineType")
                    val name = " $medicineName $medicinePower"
                    Log.d("medicineList", name)
                    medicineList.add(name)

                }
                val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, medicineList)
                // Notify adapter that data set has changed
                txtReminderTitle.setAdapter(adapter)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching medicines: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setUpModel() {
        dialogBoxManager = DialogBoxManager(requireActivity())
        reminderManager = ReminderManager(requireActivity())
        permissionManager = PermissionManager(requireActivity())
        commonUtils = CommonUtils()
        val viewModelFactory = ViewModelFactory()
        reminderViewModel = ViewModelProvider(this, viewModelFactory)[ReminderViewModel::class.java]
        firestore = FirebaseFirestore.getInstance()
        medicineList = mutableListOf()

    }

    private fun bindViews() {
        txtReminderTitle = binding.txtReminderTitle
        txtReminderMessage = binding.txtReminderMessage
        checkBoxMorning = binding.checkBoxMorning
        checkBoxAfterNoon = binding.checkBoxAfterNoon
        checkBoxNight = binding.checkBoxNight
        txtMorningTime = binding.txtMorningTime
        txtAfterNoonTime = binding.txtAfterNoonTime
        txtEveningTime = binding.txtEveningTime
        txtMorningDose = binding.txtMorningDose
        txtAfterNoonDose = binding.txtAfterNoonDose
        txtNightDose = binding.txtNightDose
        btnBeforeLunch = binding.btnBeforeLunch
        btnAfterLunch = binding.btnAfterLunch
        btnSingleTime = binding.btnSingleTime
        txtStartDate = binding.txtStartDate
        btnStartDate = binding.btnStartDate
        txtEndDate = binding.txtEndDate
        btnEndDate = binding.btnEndDate
        imageView = binding.imageView
        btnCaptureImage = binding.btnCaptureImage
        btnSetReminder = binding.btnSetReminder
        spinKitView = binding.spinKit
        overlayView = binding.overlayView

        val doseList = listOf("1 dose","2 dose","3 dose","4 dose","5 dose")
        val adapterDose = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, doseList)

        //set default value
        txtMorningDose.setText(doseList[0], false)
        txtAfterNoonDose.setText(doseList[0], false)
        txtNightDose.setText(doseList[0], false)

        txtMorningDose.setAdapter(adapterDose)
        txtAfterNoonDose.setAdapter(adapterDose)
        txtNightDose.setAdapter(adapterDose)

        // Disable typing and keep dropdown items visible and selectable
        txtMorningDose.isFocusable = false
        txtAfterNoonDose.isFocusable = false
        txtNightDose.isFocusable = false

    }
    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (data != null && data.data != null) {
                    val selectedImageUri: Uri = data.data!!
                    imageView.visibility = View.VISIBLE
                    imageView.setImageURI(selectedImageUri)
                    imageUri = selectedImageUri
                    imageSelectedBoolean = true
                } else {
                    val extras: Bundle? = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap
                    imageView.visibility = View.VISIBLE
                    imageView.setImageBitmap(imageBitmap)
                    imageUri = getImageUri(imageBitmap)
                    imageSelectedBoolean = true
                }
            }
        }
    }
    private fun showToast(msg: String) {
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
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}

