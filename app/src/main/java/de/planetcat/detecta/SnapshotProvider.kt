package de.planetcat.detecta

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.location.*

class SnapshotProvider(private val context: Context, val logger: Logger) {

    var snapshotInformation = "None"

    init {
        val transitions = getTransitions()
        val request = ActivityTransitionRequest(transitions)

        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val task = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("DetectAService", "PERMISSION NOT ENABLED")
        } else {
            val intent = Intent(context, ActivityTransitionReceiver::class.java)
            val pendingIntent =
            PendingIntent.getActivity(context, 122, intent, PendingIntent.FLAG_IMMUTABLE
            )
            val task = ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, pendingIntent)

            task.addOnSuccessListener {
                Log.w("DetectAService", "Successful Registration")
            }

            task.addOnFailureListener { e: Exception ->
                Log.e("DetectAService", "Unsuccessful Registration: ${e.message}", e)
            }

        }


    }

    private fun getTransitions(): MutableList<ActivityTransition> {
        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        return transitions
    }

    fun log() {
        Log.w("DetectAService", "Snapshot START")
        Awareness.getSnapshotClient(context).detectedActivity
            .addOnSuccessListener {
                val newSnapshotInformation = it.toString()
                Log.w("DetectAService", "SS $newSnapshotInformation")
                if (newSnapshotInformation != snapshotInformation) {
                    logger.log("SS $newSnapshotInformation")
                    snapshotInformation = newSnapshotInformation
                    Log.w("DetectAService", "Snapshot LOGGED")
                }
            }.addOnFailureListener { exception ->
                Log.e("DetectAService", "Snapshot failed: ${exception.message}", exception)
            }.addOnCanceledListener {
                Log.w("DetectAService", "Snapshot CANCELED")
            }
    }

}