package de.planetcat.detecta

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class DetectAService : AccessibilityService() {
    override fun onInterrupt() {
        Log.w("DetectAService", "Interrupt Happened")
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        Log.w("DetectAService", "AccessibilityEvent Captured")
    }
}