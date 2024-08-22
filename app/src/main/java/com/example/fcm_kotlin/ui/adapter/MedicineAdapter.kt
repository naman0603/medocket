// MedicineAdapter.kt

package com.example.fcm_kotlin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.R
import com.example.fcm_kotlin.dataClass.MedicineData
import com.google.android.material.imageview.ShapeableImageView

class MedicineAdapter(private val medicineList: List<MedicineData>) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicine_item_layout, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(medicineList[position])
    }

    override fun getItemCount(): Int {
        return medicineList.size
    }

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineNameTextView: TextView = itemView.findViewById(R.id.medicineViewMedicineName)
        private val medicineViewImage: ShapeableImageView = itemView.findViewById(R.id.medicineViewImage)
        private val medicineViewMedicinePower: TextView = itemView.findViewById(R.id.medicineViewMedicinePower)

        fun bind(medicine: MedicineData) {
            medicineNameTextView.text = medicine.medicineName
            medicineViewMedicinePower.text = medicine.medicinePower
            val type = medicine.medicineType
            when (type) {
                "Capsule" -> {
                    medicineViewImage.setImageResource(R.drawable.ic_capsule)
                }
                "Eye Drop" -> {
                    medicineViewImage.setImageResource(R.drawable.ic_eye_drop)
                }
                "Tablet" -> {
                    medicineViewImage.setImageResource(R.drawable.ic_pill)
                }
                "Injection" -> {
                    medicineViewImage.setImageResource(R.drawable.ic_injection)
                }
                "Syrup" -> {
                    medicineViewImage.setImageResource(R.drawable.ic_syrup)
                }
                "Ointment" -> {
                    medicineViewImage.setImageResource(R.drawable.ic_ointment)
                }
            }
        }
    }
}