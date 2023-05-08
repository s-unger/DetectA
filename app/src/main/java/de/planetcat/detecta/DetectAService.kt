package de.planetcat.detecta

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent


class DetectAService : AccessibilityService() {

    override fun onServiceConnected() {

        val info = AccessibilityServiceInfo()
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED or
                AccessibilityEvent.TYPE_VIEW_SELECTED or
                AccessibilityEvent.TYPE_VIEW_FOCUSED or
                AccessibilityEvent.TYPE_VIEW_FOCUSED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED or
                AccessibilityEvent.TYPE_ANNOUNCEMENT or
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START or
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END or
                AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                AccessibilityEvent.TYPE_SPEECH_STATE_CHANGE
        }

        this.serviceInfo = info

    }


    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.w("DetectAService", "Interrupt Happened")
    }


    /*
    Message-Format:
    AE [EventType] t[EventTime] p[PackageName] a[Action]*
    * Only if Action is not zero.
     */
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        var message = "AE "
        if (p0 != null) {
            if (p0.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                message += (p0.eventType.toString()+" t"+p0.eventTime.toString() + " p"+p0.packageName)
                if (p0.action != 0) {
                    message += (" a")
                }


            } else {
                message += p0.toString()
            }
        }
        Logger.log(message)
    }
}