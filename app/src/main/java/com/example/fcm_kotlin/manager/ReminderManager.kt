// ReminderManager.kt
package com.example.fcm_kotlin.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fcm_kotlin.ReminderBroadcastReceiver
import java.text.SimpleDateFormat
import java.util.*

class ReminderManager(private val context: Context) {

    @SuppressLint("SimpleDateFormat")
    fun setReminder(
        timePairs: List<Pair<Int, Pair<Int, Int>>>,
        datesList: List<String>,
        dosePairs: MutableList<Pair<Int, String>>,
        title: String,
        message: String,
        foodBoolean: Boolean
    ): MutableList<Pair<Int, Int>> {
        Log.d("setReminderTag", "$timePairs\n$datesList\n$dosePairs\n$foodBoolean")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCodeList = mutableListOf<Pair<Int, Int>>()

        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val currentTimeMillis = System.currentTimeMillis()

        for (dateString in datesList) {
            val selectedDate = dateFormat.parse(dateString)
            if (selectedDate == null) {
                Log.e("ReminderManager", "Error parsing date: $dateString")
                continue
            }

            val isToday = isToday(selectedDate)

            for (pair in timePairs) {
                val triggerTimeMillis = calculateTriggerTime(pair.second.first, pair.second.second, selectedDate)
                val requestCode = generateRequestCode(pair.second.first, pair.second.second, selectedDate)
                val pairRequestCode = Pair(pair.first, requestCode)
                requestCodeList.add(pairRequestCode)
                val dose = dosePairs.find { it.first == pair.first }?.second ?: ""

                if (isToday) {
                    if (triggerTimeMillis <= currentTimeMillis) {
                        val toastMessage = "Alarm for ${pair.second.first}:${pair.second.second} on $dateString has already passed."
                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        if (checkExactAlarmPermission(alarmManager)) {
                            setAlarm(triggerTimeMillis, title, message, dose, alarmManager, requestCode, foodBoolean)
                        }
                    }
                } else {
                    if (checkExactAlarmPermission(alarmManager)) {
                        setAlarm(triggerTimeMillis, title, message, dose, alarmManager, requestCode, foodBoolean)
                    }
                }
            }
        }

        return requestCodeList
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateTriggerTime(hour: Int, minute: Int, selectedDate: Date?): Long {
        val calendar = Calendar.getInstance().apply {
            time = selectedDate ?: Date()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return calendar.timeInMillis
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(
        triggerTimeMillis: Long,
        title: String,
        message: String,
        dose: String,
        alarmManager: AlarmManager,
        requestCode: Int,
        foodBoolean: Boolean,
    ) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("dose", dose)
            putExtra("foodBoolean", foodBoolean)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }

    private fun checkExactAlarmPermission(alarmManager: AlarmManager): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                showExactAlarmPermissionDialog()
                return false
            }
        }
        return true
    }

    private fun showExactAlarmPermissionDialog() {
        AlertDialog.Builder(context)
            .setTitle("Exact Alarm Permission Needed")
            .setMessage("This app requires permission to set exact alarms. Please allow this permission in the settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openExactAlarmSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun cancelAlarms(requestCodeList: List<Int>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (requestCode in requestCodeList) {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

                
        }
    }

    private fun generateRequestCode(hour: Int, minute: Int, selectedDate: Date?): Int {
        val random = Random(System.currentTimeMillis())
        val randomComponent = random.nextInt(10000)
        val currentDateComponent = selectedDate?.time ?: 0
        val uniqueCode = (hour shl 24) or (minute shl 16) or (currentDateComponent.toInt() shl 8) or randomComponent
        return uniqueCode and Int.MAX_VALUE
    }

    private fun isToday(date: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar1.time = Date()
        calendar2.time = date
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }
}
