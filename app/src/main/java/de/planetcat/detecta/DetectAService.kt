package de.planetcat.detecta

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.*
import androidx.work.*
import com.google.gson.Gson


class DetectAService : AccessibilityService() {

    lateinit var eventBuffer: EventBuffer

    fun getDataObjectId():Int {
        val sharedPreferences = this.getSharedPreferences("preferences", Activity.MODE_PRIVATE)
        val dataObjectID = sharedPreferences.getInt("doi", -1)
        return dataObjectID
    }

    fun increaseDataObjectId() {
        val sharedPreferences = this.getSharedPreferences("preferences", Activity.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putInt("doi", (getDataObjectId()+1))
            apply()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        this.eventBuffer = EventBuffer(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.w("DetectAService", "Interrupt Happened")
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        if (p0 != null) {
            if (eventBuffer.isMajorEvent(p0, getDataObjectId())) {
                increaseDataObjectId()
            }
        }
    }

    class EventBuffer(private val detectAService: Context) {
        var pBuffer:MutableList<AccessibilityEvent> = mutableListOf()
        var activityNameBuffer:CharSequence = "Start"
        var activityTimeBuffer:Long = System.currentTimeMillis()
        var dataObjectList:MutableList<DataObject> = mutableListOf()
        val locationProvider = LocationProvider(detectAService)
        val snapshot = Snapshot(detectAService)

        fun isMajorEvent(event: AccessibilityEvent, doi: Int):Boolean {
            if ( event.eventType == TYPE_WINDOW_STATE_CHANGED &&
                event.className.toString().startsWith(event.packageName) &&
                event.className != activityNameBuffer) {
                val dataObject = DataObject(activityNameBuffer.toString(), doi+1, doi, activityTimeBuffer, pBuffer, locationProvider.getLocation(), NetworkProvider(detectAService).getNetworkName(), snapshot.getSnapshot())
                Log.w("DetectAService", dataObject.toString())
                dataObjectList.add(dataObject)
                if (dataObjectList.size > 30) {
                    val uploadData = Data.Builder()
                        .putString("data", Gson().toJson(dataObjectList))
                        .build()
                    val uploadDataConstraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                    val uploadWorkRequest = OneTimeWorkRequestBuilder<DataUploader>()
                        .setInputData(uploadData)
                        .setConstraints(uploadDataConstraints)
                        .build()
                    WorkManager.getInstance(detectAService).enqueue(uploadWorkRequest)
                    dataObjectList = mutableListOf<DataObject>()
                }
                activityNameBuffer = event.className.toString()
                activityTimeBuffer = System.currentTimeMillis()
                pBuffer = mutableListOf<AccessibilityEvent>()
                return true
            } else {
                if (event.eventType > 0) {
                    //Log.w("DetectAService", "Added following Event to Buffer: " + event.toString())
                    pBuffer.add(event)
                }
                return false
            }
        }
    }
}