package de.planetcat.detecta

import android.location.Location
import android.view.accessibility.AccessibilityEvent


class DataObject(
    private val name: String,
    private val dataObjectID: Int,
    private val previousDataObjectID: Int,
    private val timestamp: Long, //Achtung! Timestamp springt!
    private val eventList: MutableList<AccessibilityEvent>,
    private val location: Location?,
    private val network: String) {

    override fun toString ():String {
        var string = "-----DataObject: " + this.name + "-----\nDataObjectID: " + dataObjectID + "\nPreviousDataObjectID: " +
                previousDataObjectID + "\nTimeStamp: " + timestamp.toString() + "\nLocation: " + location.toString() + "\nNetwork: " + network
        for (event in this.eventList) {
            string += "\n" + event.toString()
        }
        return string
    }
}