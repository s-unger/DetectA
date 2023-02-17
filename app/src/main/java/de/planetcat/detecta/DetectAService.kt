package de.planetcat.detecta

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Environment
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.*
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class DetectAService : AccessibilityService() {

    var eventBuffer = EventBuffer()
    var userId:Int = -1
    var dataObjectID:Long = -1

    override fun onServiceConnected() {
        val sharedPreferences = getSharedPreferences("DetectAService", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("UserID")) {
            with (sharedPreferences.edit()) {
                putInt("UserID", getUniqueUserID())
                apply()
            }
        }
        if (!sharedPreferences.contains("DataObjectID")) {
            with(sharedPreferences.edit()) {
                putLong("DataObjectID", 0)
                apply()
            }
        }
        userId = sharedPreferences.getInt("UserID", -1)
        dataObjectID = sharedPreferences.getLong("DataObjectID", -1)
        super.onServiceConnected()
    }

    fun getUniqueUserID():Int {
        //TODO: Just a Mock, Ask server later.
        return (0..255).random()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val sharedPreferences = getSharedPreferences("DetectAService", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putLong("DataObjectID", dataObjectID)
            apply()
        }
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.w("DetectAService", "Interrupt Happened")
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        if (p0 != null) {
            if (eventBuffer.isMajorEvent(p0, dataObjectID, userId, this)) {
                dataObjectID += 1
            }
        }
    }

    class EventBuffer() {
        var pBuffer:MutableList<AccessibilityEvent> = mutableListOf<AccessibilityEvent>()
        var activityNameBuffer:CharSequence = "Start"
        var activityTimeBuffer:Long = System.currentTimeMillis()
        var dataObjectList:MutableList<DataObject> = mutableListOf<DataObject>()

        fun isMajorEvent(event: AccessibilityEvent, previousObjectID:Long, userID:Int, service:AccessibilityService):Boolean {
            if ( event.eventType == TYPE_WINDOW_STATE_CHANGED &&
                event.className.startsWith(event.packageName) &&
                event.className != activityNameBuffer) {
                val dataObject = DataObject(activityNameBuffer.toString(), previousObjectID+1, previousObjectID, activityTimeBuffer, pBuffer)
                Log.w("DetectAService", dataObject.toString())
                dataObjectList.add(dataObject)
                if (dataObjectList.size > 99) {
                    val path = service.getExternalFilesDir(null).toString() + "/DetectA/DetectA-" + userID + "-" + previousObjectID + "-" + System.currentTimeMillis() + ".json"
                    val data = Gson().toJson(dataObjectList)
                    try {
                        File(service.getExternalFilesDir(null).toString() + "/DetectA/").mkdirs()
                        val file = File(path)
                        file.createNewFile()
                        val fileOutputStream = FileOutputStream(File(path))
                        fileOutputStream.write(data.toByteArray())
                        Log.w("DetectAService", "=====> FILE WRITTEN TO: " + path)
                        dataObjectList = mutableListOf<DataObject>()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
                activityNameBuffer = event.className
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