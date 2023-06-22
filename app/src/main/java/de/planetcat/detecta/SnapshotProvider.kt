package de.planetcat.detecta

import android.content.Context
import android.util.Log
import com.google.android.gms.awareness.Awareness

class SnapshotProvider(private val context: Context, val logger: Logger) {

    var snapshotInformation = "None"

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