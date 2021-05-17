package by.bsuir.sporttracker.service

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import by.bsuir.sporttracker.R
import by.bsuir.sporttracker.module.TrackBuilder
import by.bsuir.sporttracker.module.TrackedModule
import by.bsuir.sporttracker.module.Tracker
import by.bsuir.sporttracker.view.MainActivity
import com.google.android.gms.location.*
import org.koin.android.ext.android.get

class RequestLocationService : Service() {

    companion object {
        private const val PACKAGE_NAME = "by.bsuir.sporttracker.service"
        private val TAG: String = RequestLocationService::class.java.simpleName
        private const val NOTIFICATION_ID = 12345678
        private const val CHANNEL_ID = "channel_01"
        const val EXTRA_LOCATION: String = "$PACKAGE_NAME.location"
        private const val EXTRA_STARTED_FROM_NOTIFICATION: String =
            "$PACKAGE_NAME.started_from_notification"
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 1000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
        var isRunningAsForeground = false
            private set
    }

    private var mChangingConfiguration = false

    private val mBinder: IBinder = LocalBinder(this)

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mTrackedModule: TrackedModule
    private lateinit var mNotificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                result ?: return
                onNewLocation(result.lastLocation)
            }
        }

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG,"Service started")
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangingConfiguration = true
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i(TAG, "in onBind()")
        when (intent.type) {
            "TRACKER" -> mTrackedModule = get<Tracker>()
            "TRACK_BUILDER" -> mTrackedModule = get<TrackBuilder>()
        }
        isRunningAsForeground = false
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "in onRebind()")
        isRunningAsForeground = false
        stopForeground(true)
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Last client unbound from service")
        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service")
            isRunningAsForeground = true
            /*if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(Intent(
                    this,
                    RequestLocationService.class), NOTIFICATION_ID, getNotification()
                )
            } else {
                startForeground(NOTIFICATION_ID, getNotification())
            }*/
            startForeground(NOTIFICATION_ID, getNotification())
        }
        return true
    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")
        startService(Intent(applicationContext, RequestLocationService::class.java))
        try {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private fun getNotification(): Notification? {
        val intent = Intent(
            this,
            RequestLocationService::class.java
        )
        //val text: CharSequence = Utils.getLocationText(mLocation)

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            true
        )

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        val servicePendingIntent = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), 0
        )
        val builder = NotificationCompat.Builder(this)
            /*.addAction(
                R.drawable.ic_launch, getString(R.string.launch_activity),
                activityPendingIntent
            )
            .addAction(
                R.drawable.ic_cancel, getString(R.string.remove_location_updates),
                servicePendingIntent
            )*/
            //.setContentText(text)
            //.setContentTitle(Utils.getLocationTitle(this))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            //.setTicker(text)
            .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }

    /*private fun getLastLocation() {
        try {
            mFusedLocationClient.lastLocation
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLocation = task.result
                    } else {
                        Log.w(
                            TAG,
                            "Failed to get location."
                        )
                    }
                }
        } catch (unlikely: SecurityException) {
            Log.e(
                TAG,
                "Lost location permission.$unlikely"
            )
        }
    }*/

    private fun onNewLocation(location: Location) {
        Log.i(
            TAG,
            "New location: $location"
        )

        mTrackedModule.updateCurrentLocation(location)

        // Update notification content if running as a foreground service.
        if (isRunningAsForeground) {
            mNotificationManager.notify(
                NOTIFICATION_ID,
                getNotification()
            )
        }
    }

    class LocalBinder(val service: RequestLocationService) : Binder() {
    }

}
