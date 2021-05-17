package by.bsuir.sporttracker.module

import android.content.Context
import android.content.Intent
import android.location.Location
import by.bsuir.sporttracker.service.RequestLocationService

class Tracker(private val context: Context): TrackedModule {
    private var locations: ArrayList<Location> = ArrayList()
    private var isInProgress: Boolean = false
    private val intent = Intent(context, RequestLocationService::class.java)
    fun startTrackBuilding(){
        intent.type = "TRACKER"
        context.startService(intent)
        isInProgress = true
    }
    fun endTrackBuilding():ArrayList<Location>{
        context.stopService(intent)
        isInProgress = false
        return locations
    }
    override fun updateCurrentLocation(location: Location){
        if (isInProgress){
            locations.add(location)
        }
    }
}