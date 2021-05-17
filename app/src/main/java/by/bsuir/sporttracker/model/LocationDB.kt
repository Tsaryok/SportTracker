package by.bsuir.sporttracker.model

import android.location.Location

data class LocationDB(val latitude: Double, var longitude: Double, var altitude: Double ){
    constructor(locationDB: LocationDB) : this(locationDB.latitude, locationDB.longitude, locationDB.altitude)
    constructor(location: Location) : this(location.latitude, location.longitude, location.altitude)
}
