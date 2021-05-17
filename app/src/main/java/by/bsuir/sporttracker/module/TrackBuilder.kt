package by.bsuir.sporttracker.module

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import by.bsuir.sporttracker.model.LocationDB
import by.bsuir.sporttracker.service.RequestLocationService

class TrackBuilder(private val context: Context): TrackedModule {
    private var locations: ArrayList<LocationDB> = ArrayList()
    private var isInProgress: Boolean = false
    private var mService: RequestLocationService? = null
    private var mBound = false

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: RequestLocationService.LocalBinder = service as RequestLocationService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    fun startTrackBuilding(){
        if (!isInProgress) {
            locations.clear()
            mService?.requestLocationUpdates()
            isInProgress = true
        }
    }

    fun pauseTrackBuilding(){
        if (isInProgress) {
            mService?.removeLocationUpdates()
        }
    }

    fun resumeTrackBuilding(){
        if (isInProgress)
            mService?.requestLocationUpdates()
    }

    fun endTrackBuilding():ArrayList<LocationDB>{
        if (isInProgress) {
            mService?.removeLocationUpdates()
            isInProgress = false
        }
        return locations
    }
    override fun updateCurrentLocation(location: Location){
        if (isInProgress){
            locations.add(LocationDB(location))
        }
    }

    fun onStart(){
        val intent = Intent(context, RequestLocationService::class.java)
        intent.type = "TRACK_BUILDER"
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    fun onStop(){
        if (mBound) {
            context.unbindService(mServiceConnection)
            mBound = false
        }
    }
}