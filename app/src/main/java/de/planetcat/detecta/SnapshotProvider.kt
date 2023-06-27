package de.planetcat.detecta

import android.content.Context
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity

class SnapshotProvider(private val context: Context, val logger: Logger) {

    var snapshotInformation = "None"

    fun log() {
        Log.w("DetectAService", "Snapshot START")
        Awareness.getSnapshotClient(context).detectedActivity
            .addOnSuccessListener {
                val activityResult = it.activityRecognitionResult
                val activity = activityResult.mostProbableActivity
                val info = "SS " + activity.toString() + " " + activity.confidence
                if (info != snapshotInformation) {
                    logger.log("SS $info")
                    snapshotInformation = info
                    Log.w("DetectAService", "Snapshot LOGGED")
                }
            }.addOnFailureListener { exception ->
                Log.e("DetectAService", "Snapshot failed: ${exception.message}", exception)
            }.addOnCanceledListener {
                Log.w("DetectAService", "Snapshot CANCELED")
            }
    }

    fun toActivityString(activity: Int): String {
        return when (activity) {
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.WALKING -> "WALKING"
            DetectedActivity.IN_VEHICLE -> "IN VEHICLE"
            DetectedActivity.RUNNING -> "RUNNING"
            else -> "UNKNOWN"
        }
    }

    fun toTransitionType(transitionType: Int): String  {
        return when (transitionType) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "ENTER"
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "EXIT"
            else -> "UNKNOWN"
        }
    }

}