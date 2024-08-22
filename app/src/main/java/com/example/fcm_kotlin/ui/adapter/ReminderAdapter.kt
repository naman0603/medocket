// ReminderAdapter.kt
package com.example.fcm_kotlin.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fcm_kotlin.R
import com.example.fcm_kotlin.ui.activity.MainActivity

class ReminderAdapter(private val context: Context, private var reminders: List<Map<String, Any>> ) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    object ReminderDataHolder {
        var reminderData: Map<String, Any>? = null
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item_layout, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.bind(reminder)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            ReminderDataHolder.reminderData = reminder
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    fun updateReminders(newReminders: List<Map<String, Any>>) {
        reminders = newReminders
        notifyDataSetChanged()
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val tvMedicineName: TextView = itemView.findViewById(R.id.viewMedicineName)
        private val tvMedicineDescription: TextView = itemView.findViewById(R.id.viewMedicineDescription)
        private val tvStartDate: TextView = itemView.findViewById(R.id.viewStartDate)
        private val tvEndDate: TextView = itemView.findViewById(R.id.viewEndDate)
        private val tvDoseTime: TextView = itemView.findViewById(R.id.viewDoseTime)
        private val tvFoodStatus: TextView = itemView.findViewById(R.id.viewFoodStatus)
        fun bind(reminder: Map<String, Any>) {
            val title = reminder["title"] as String
            val message = reminder["message"] as String
            val imageUrl = reminder["imageUrl"] as String
            val startDate = reminder["startDate"] as String
            val endDate = reminder["endDate"] as String
            val foodBoolean = reminder["foodBoolean"] as Boolean
            val timePair = reminder["timePair"] as Map<String, String>
            val dosePairs = reminder["dosePairs"] as Map<String, String>
            val requestCodePair = reminder["requestCodePair"] as Map<String, Int>

            tvMedicineName.text = title
            tvMedicineDescription.text = message
            tvStartDate.text = startDate
            tvEndDate.text = endDate
            tvDoseTime.text = getTime(timePair)
            if(foodBoolean){
                tvFoodStatus.text = "Should be taken after food"
            }else{
                tvFoodStatus.text = "Should be taken before food"
            }

        }

    }

    private fun getTime(timePair: Map<String, String>): CharSequence {
        var timeString = ""
        timePair.values.forEachIndexed { index, time ->
            timeString += time
            if (index < timePair.size - 1) {
                timeString += ", "
            }
        }
        return timeString
    }

}

