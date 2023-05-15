package de.planetcat.detecta

import android.content.Context
import com.google.android.gms.awareness.Awareness

class SnapshotProvider(private val context: Context, val logger: Logger) {

    var snapshotInformation = "None"

    fun log() {
        val newSnapshotInformation = getSnapshot()
        if (newSnapshotInformation != snapshotInformation) {
            logger.log("SS $newSnapshotInformation")
            snapshotInformation = newSnapshotInformation
        }
    }

    fun getSnapshot(): String {
        Awareness.getSnapshotClient(context).detectedActivity
            .addOnSuccessListener {
                snapshotInformation = it.toString()
            }
        return snapshotInformation
    }

}