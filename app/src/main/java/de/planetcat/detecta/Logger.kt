package de.planetcat.detecta

import android.app.Activity
import android.util.Log
import androidx.work.*
import de.planetcat.detecta


object Logger {
    var content: String
    var linenumber = 0

    init{
        content = "[" + getPackageId().toString() + "]"
        linenumber = 0
    }

    fun getPackageId():Int {
        val sharedPreferences = detecta.getAppContext().getSharedPreferences("preferences", Activity.MODE_PRIVATE)
        val packageId = sharedPreferences.getInt("pid", 0)
        with (sharedPreferences.edit()) {
            putInt("pid", (packageId+1))
            apply()
        }
        return packageId
    }

    fun log(data: String) {
        val logline = "\n" + linenumber.toString() + " | " + System.currentTimeMillis().toString() + " | " + data
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
            WorkManager.getInstance(detecta.getAppContext()).enqueue(uploadWorkRequest)
            Log.w("DetectAService", "Upload Queued.")
            Log.w("DetectAService", content)
            content = "[" + getPackageId().toString() + "]"
            linenumber = 0
        }
    }

}