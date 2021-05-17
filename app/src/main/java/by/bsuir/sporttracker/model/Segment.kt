package by.bsuir.sporttracker.model

import android.location.Location
import android.util.Log
import by.bsuir.sporttracker.view.tracklist.TrackAdapter

class Segment(val startLocation: LocationDB, val endLocation: LocationDB) {

    companion object{
        private val TAG: String = Segment::class.java.simpleName
    }

    var distanceInMeters: Float
        private set
    init {
        Log.i(TAG, "Segment init")
        Log.i(TAG, "Distance between to locations: [${startLocation.latitude},${startLocation.longitude}] and [${endLocation.latitude},${endLocation.longitude}]")
        val result = floatArrayOf(0F)
        Location.distanceBetween(
            startLocation.latitude, startLocation.longitude,
            endLocation.latitude, endLocation.longitude, result
        )
        distanceInMeters = result[0]
        Log.i(TAG, "Segment initialized")
    }
}