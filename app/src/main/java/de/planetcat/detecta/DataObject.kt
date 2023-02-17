package de.planetcat.detecta

import android.view.accessibility.AccessibilityEvent


class DataObject(
    private val name: String,
    private val dataObjectID: Long,
    private val previousDataObjectID: Long,
    private val timestamp: Long, //Achtung! Timestamp springt!
    private val eventList: MutableList<AccessibilityEvent>) {

    override fun toString ():String {
        var string = "-----DataObject: " + this.name + "-----\nDataObjectID: " + dataObjectID + "\nPreviousDataObjectID: " +
                previousDataObjectID + "\nTimeStamp: " + timestamp.toString()
        for (event in this.eventList) {
            string += "\n" + event.toString()
        }
        return string
    }
}