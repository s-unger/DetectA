package de.planetcat.detecta

import android.content.Context
import com.google.android.gms.awareness.Awareness

class Snapshot(private val context: Context) {

    var snapshotInformation = "None"

    fun getSnapshot(): String {
        Awareness.getSnapshotClient(context).detectedActivity
            .addOnSuccessListener {
                snapshotInformation = it.toString()
            }
        return snapshotInformation
    }

}