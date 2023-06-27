package de.planetcat.detecta

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DetectAService : AccessibilityService() {

    private lateinit var logger: Logger
    private lateinit var locationProvider: LocationProvider
    private lateinit var networkProvider: NetworkProvider
    private lateinit var snapshotProvider: SnapshotProvider
    private lateinit var intentProvider: IntentProvider

    override fun onServiceConnected() {
        this.logger = Logger(this)
        this.logger.init()

        val info = AccessibilityServiceInfo()
        info.apply {
            // Set the type of events that this service wants to listen to. Others
            // won't be passed to this service.
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED or
                AccessibilityEvent.TYPE_VIEW_SELECTED or
                AccessibilityEvent.TYPE_VIEW_SCROLLED or
                AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED or
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_FOCUSED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_ANNOUNCEMENT
        }

        this.serviceInfo = info

        locationProvider = LocationProvider(this, logger)
        networkProvider = NetworkProvider(this, logger)
        snapshotProvider = SnapshotProvider(this, logger)
        intentProvider = IntentProvider(this, logger)

    }


    override fun onUnbind(intent: Intent?): Boolean {
        intentProvider.deinit()
        return super.onUnbind(intent)
    }

    override fun onInterrupt() {
        Log.w("DetectAService", "Interrupt Happened")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        logMeta()
        var message = "AE"
        if (event != null) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val text = source?.text?.toString()
                    val contentDescription = source?.contentDescription?.toString()

                    message += " VC P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    if (!text.isNullOrEmpty()) {
                        message += " T:$text"
                    }
                    if (!contentDescription.isNullOrEmpty()) {
                        message += " CD:$contentDescription"
                    }
                }
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val text = source?.text?.toString()
                    val contentDescription = source?.contentDescription?.toString()

                    message += " LC P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    if (!text.isNullOrEmpty()) {
                        message += " T:$text"
                    }
                    if (!contentDescription.isNullOrEmpty()) {
                        message += " CD:$contentDescription"
                    }
                }
                AccessibilityEvent.TYPE_VIEW_SELECTED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val text = source?.text?.toString()
                    val contentDescription = source?.contentDescription?.toString()
                    val isSelected = source?.isSelected

                    message += " VS P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    if (!text.isNullOrEmpty()) {
                        message += " T:$text"
                    }
                    if (!contentDescription.isNullOrEmpty()) {
                        message += " CD:$contentDescription"
                    }
                    if (isSelected != null) {
                        message += " I:$isSelected"
                    }
                }
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val scrollX = event.scrollX
                    val scrollY = event.scrollY
                    val maxScrollX = event.maxScrollX
                    val maxScrollY = event.maxScrollY

                    message += " VS P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    message += " SX:$scrollX SY:$scrollY MSX:$maxScrollX MSY:$maxScrollY"
                }
                AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val text = source?.text?.toString()
                    val contentDescription = source?.contentDescription?.toString()

                    message += " VCC P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    if (!text.isNullOrEmpty()) {
                        message += " T:$text"
                    }
                    if (!contentDescription.isNullOrEmpty()) {
                        message += " CD:$contentDescription"
                    }
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val beforeText = event.beforeText?.toString()
                    val afterText = event.text.toString()

                    message += " VTC P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    if (!beforeText.isNullOrEmpty()) {
                        message += " BT:$beforeText"
                    }
                    if (afterText.isNotEmpty()) {
                        message += " AT:$afterText"
                    }
                }
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    val source = event.source
                    val packageName = source?.packageName
                    val className = source?.className
                    val viewId = source?.viewIdResourceName
                    val text = source?.text?.toString()
                    val contentDescription = source?.contentDescription?.toString()

                    message += " VF P:$packageName C:$className"
                    if (!viewId.isNullOrEmpty()) {
                        message += " V:$viewId"
                    }
                    if (!text.isNullOrEmpty()) {
                        message += " T:$text"
                    }
                    if (!contentDescription.isNullOrEmpty()) {
                        message += " CD:$contentDescription"
                    }
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val packageName = event.packageName?.toString()
                    val className = event.className?.toString()
                    val text = event.text.toString()
                    val contentDescription = event.contentDescription?.toString()

                    message += " WSC"
                    message += " P:$packageName C:$className"
                    if (text.isNotEmpty()) {
                        message += " T:$text"
                    }
                    if (!contentDescription.isNullOrEmpty()) {
                        message += " CD:$contentDescription"
                    }
                }
                AccessibilityEvent.TYPE_ANNOUNCEMENT -> {
                    val packageName = event.packageName?.toString()
                    val text = event.text.toString()

                    message += " A P:$packageName"
                    if (text.isNotEmpty()) {
                        message += " T:$text"
                    }
                }
                else -> {
                    message += " ET:${event.eventType}"
                }
            }
            logger.log(message)
        }
    }

    private var isCoroutineRunning = false

    private fun logMeta () {
        if (!isCoroutineRunning) {
            isCoroutineRunning = true
            CoroutineScope(Dispatchers.IO).launch {
                delay(1000L)
                locationProvider.log()
                delay(2000L)
                networkProvider.log()
                delay(2000L)
                snapshotProvider.log()
                delay(10000L)
                isCoroutineRunning = false
            }
        }
    }
}