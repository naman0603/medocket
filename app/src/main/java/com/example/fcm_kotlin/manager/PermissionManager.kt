// PermissionManager.kt
package com.example.fcm_kotlin.manager

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: Activity) {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    fun requestPermission(permission: String) {
        if (isPermissionGranted(permission)) {
            // Permission already granted
            return
        }
        requestPermissionDirectly(permission)
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionDirectly(permission: String) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), PERMISSION_REQUEST_CODE)
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                // Open settings if the user denied the permission and it's crucial
                openAppSettings()
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)
    }

    // New function to handle exact alarm permissions
    fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                showExactAlarmPermissionDialog()
            }
        }
    }

    private fun showExactAlarmPermissionDialog() {
        AlertDialog.Builder(activity)
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
            activity.startActivity(intent)
        }
    }
}
