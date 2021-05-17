package by.bsuir.sporttracker.module

import android.location.Location

interface TrackedModule {
    fun updateCurrentLocation(location: Location)
}