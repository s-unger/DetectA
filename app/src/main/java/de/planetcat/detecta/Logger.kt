package de.planetcat.detecta

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.work.*


class Logger (private val context: Context) {
    private lateinit var content: String
    private var linenumber = 0

    fun init() {
        content = "[" + getPackageId().toString() + "]"
        linenumber = 0
    }

    fun getPackageId():Int {
        val sharedPreferences = context.getSharedPreferences("preferences", Activity.MODE_PRIVATE)
        val packageId = sharedPreferences.getInt("pid", 0)
        with (sharedPreferences.edit()) {
            putInt("pid", (packageId+1))
            apply()
        }
        return packageId
    }

    fun log(data: String) {
        val logline =
            if (data.length > 500) {
                "\n" + linenumber.toString() + " | " + System.currentTimeMillis()
                    .toString() + " | " + data.substring(0, 500) + "(SHO)"
            } else {
                "\n" + linenumber.toString() + " | " + System.currentTimeMillis().toString() + " | " + data
            }
        Log.w("DetectAService", logline)
        val combined = content + logline
        try {
            Data.Builder()
                .putString("data", combined)
                .build()
            content += logline
            linenumber++
        } catch (e: java.lang.IllegalStateException) {
            val uploadData = Data.Builder()
                .putString("data", content)
                .build()
            val uploadDataConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build()
            val uploadWorkRequest = OneTimeWorkRequestBuilder<DataUploader>()
                .setInputData(uploadData)
                .setConstraints(uploadDataConstraints)
                .build()
            WorkManager.getInstance(context).enqueue(uploadWorkRequest)
            Log.w("DetectAService", "Upload Queued.")
            Log.w("DetectAService", content)
            content = "[" + getPackageId().toString() + "]"
            linenumber = 0
            content += logline
            linenumber++
        }
    }

}