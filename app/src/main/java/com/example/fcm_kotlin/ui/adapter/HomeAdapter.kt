package com.example.fcm_kotlin.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.R
import com.example.fcm_kotlin.dataClass.Medicine

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SECTION_VIEW_TYPE = 0
    private val MEDICINE_VIEW_TYPE = 1

    private val sections: MutableList<Section> = mutableListOf()

    fun addMedicines(sectionTitle: String, medicines: List<Medicine>) {
        if (medicines.isNotEmpty()) {
            sections.add(Section(sectionTitle, medicines))
            notifyDataSetChanged()
        }
    }
    fun clearMecicines(){
        sections.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SECTION_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
                SectionViewHolder(view)
            }
            MEDICINE_VIEW_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicine, parent, false)
                MedicineViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sectionIndex = getSectionIndex(position)
        val itemIndexInSection = getItemIndexInSection(position)

        when (holder) {
            is SectionViewHolder -> {
                holder.bind(sections[sectionIndex].title)
            }
            is MedicineViewHolder -> {
                val sectionTitle = sections[sectionIndex].title
                val medicine = sections[sectionIndex].medicines[itemIndexInSection]
                holder.bind(sectionTitle, medicine)
            }
        }
    }

    override fun getItemCount(): Int {
        return sections.sumBy { it.medicines.size + 1 } // +1 for each section header
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSectionHeader(position)) {
            SECTION_VIEW_TYPE
        } else {
            MEDICINE_VIEW_TYPE
        }
    }

    private fun isSectionHeader(position: Int): Boolean {
        var itemCountSoFar = 0
        for (section in sections) {
            if (position == itemCountSoFar) {
                return true
            }
            itemCountSoFar += section.medicines.size + 1
        }
        return false
    }

    private fun getSectionIndex(position: Int): Int {
        var itemCountSoFar = 0
        for ((index, section) in sections.withIndex()) {
            if (position <= itemCountSoFar + section.medicines.size) {
                return index
            }
            itemCountSoFar += section.medicines.size + 1
        }
        throw IllegalArgumentException("Invalid position")
    }

    private fun getItemIndexInSection(position: Int): Int {
        var itemCountSoFar = 0
        val sectionIndex = getSectionIndex(position)
        for ((index, section) in sections.withIndex()) {
            if (index == sectionIndex) {
                return position - itemCountSoFar - 1
            }
            itemCountSoFar += section.medicines.size + 1
        }
        throw IllegalArgumentException("Invalid position")
    }

    private data class Section(val title: String, val medicines: List<Medicine>)

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTextView: TextView = itemView.findViewById(R.id.txtHeader)

        fun bind(title: String) {
            headerTextView.text = title
        }
    }

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMedicineName: TextView = itemView.findViewById(R.id.viewItemMedicineName)
        private val tvDose: TextView = itemView.findViewById(R.id.viewItemDose)
        private val tvDoseTime: TextView = itemView.findViewById(R.id.viewItemDoseTime)
        private val tvFoodStatus: TextView = itemView.findViewById(R.id.viewItemFoodStatus)

        fun bind(sectionTitle: String, medicine: Medicine) {
            tvMedicineName.text = medicine.title
            tvDose.text = getDose(sectionTitle, medicine.dosePairs)
            tvDoseTime.text = getTime(sectionTitle, medicine.timePair)
            if (medicine.foodBoolean) {
                tvFoodStatus.text = "Should be taken after food"
            } else {
                tvFoodStatus.text = "Should be taken before food"
            }
        }
    }

    private fun getDose(title: String, dosePairs: Map<Int, String>): CharSequence? {
        return when (title) {
            "Morning" -> dosePairs[1] ?: ""
            "Afternoon" -> dosePairs[2] ?: ""
            "Night" -> dosePairs[3] ?: ""
            else -> ""
        }
    }

    private fun getTime(sectionTitle: String, timePair: Map<Int, String>): String {
        val time24Hour = when (sectionTitle) {
            "Morning" -> timePair[1]
            "Afternoon" -> timePair[2]
            "Night" -> timePair[3]
            else -> null
        }

        return time24Hour?.let { convertTo12HourFormat(it) } ?: ""
    }

    private fun convertTo12HourFormat(time24Hour: String): String {
        val hourMinute = time24Hour.split(":")
        val hour = hourMinute[0].toInt()
        val minute = hourMinute[1]
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12
        return String.format("%02d:%s %s", hour12, minute, amPm)
    }

}
