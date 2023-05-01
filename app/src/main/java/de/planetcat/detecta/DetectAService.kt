package de.planetcat.detecta

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent


class DetectAService : AccessibilityService() {

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
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.w("DetectAService", "Interrupt Happened")
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        if (p0 != null) {
            Logger.log(p0.toString())
        }
    }
}