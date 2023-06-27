package de.planetcat.detecta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.ContextCompat

class IntentProvider (val context: Context, val logger: Logger) {
    private var broadcastReceiver: MyBroadcastReceiver
    init {
        broadcastReceiver = MyBroadcastReceiver(logger)
        // Register Intents
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SHUTDOWN)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_HEADSET_PLUG)
            // Add more actions if needed
        }

        // Register the broadcast receiver with the intent filter
        ContextCompat.registerReceiver(context, broadcastReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED)
    }

    fun deinit () {
        context.unregisterReceiver(broadcastReceiver)
    }

    class MyBroadcastReceiver(private val logger: Logger) : BroadcastReceiver() {
        var lastBatteryLog = ""
        override fun onReceive(context: Context, intent: Intent) {
            // Handle the received intent here
            when (intent.action) {
                Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                    val isEnabled = intent.extras?.getBoolean("state")
                    logger.log("IN AIRPLANE_MODE_CHANGED $isEnabled")
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)

                    val charging = when (plugged) {
                        BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                        BatteryManager.BATTERY_PLUGGED_DOCK -> "Dock"
                        BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
                        else -> "NO"
                    }
                    val batteryLog = "IN BATTERY_CHANGED $charging $level/$scale"
                    if (batteryLog != lastBatteryLog) {
                        lastBatteryLog = batteryLog
                        logger.log(batteryLog)
                    }
                }
                Intent.ACTION_BOOT_COMPLETED -> {
                    logger.log("IN BOOT_COMPLETED")
                }
                Intent.ACTION_SCREEN_OFF -> {
                    logger.log("IN SCREEN_OFF")
                }
                Intent.ACTION_SCREEN_ON -> {
                    logger.log("IN SCREEN_ON")
                }
                Intent.ACTION_SHUTDOWN -> {
                    logger.log("IN SHUTDOWN")
                }
                Intent.ACTION_USER_PRESENT -> {
                    // User unlocked the device
                    logger.log("IN USER_PRESENT")
                }
                Intent.ACTION_HEADSET_PLUG -> {
                    val state = intent.getIntExtra("state", -1)
                    val hasMicrophone = intent.getIntExtra("microphone", -1) == 1
                    val name = intent.getStringExtra("name")

                    val isHeadsetPluggedIn = state == 1

                    if (isHeadsetPluggedIn) {
                        logger.log("Headset name: $name")
                        logger.log("Has microphone: $hasMicrophone")
                        logger.log("IN HEADSET_PLUG $isHeadsetPluggedIn M $hasMicrophone N $name")
                    } else {
                        logger.log("IN HEADSET_PLUG $isHeadsetPluggedIn")
                    }

                }
                // Add more cases for other intents if needed

            }
        }
    }
}