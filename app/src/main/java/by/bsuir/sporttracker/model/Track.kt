package by.bsuir.sporttracker.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


@Entity
class Track(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    val pointString: String,
    val isLooped: Boolean
){
    companion object{
        private val gson = Gson()
    }
    @Ignore
    val points: List<LocationDB> =
        gson.fromJson(pointString, Array<LocationDB>::class.java).toList()
    constructor(id: Long, name: String, points: List<LocationDB>, isLooped: Boolean ):
            this(id, name, gson.toJson(points), isLooped)

    fun getLatLngList(): List<LatLng>{
        return points.map {LatLng(it.latitude, it.longitude)}
    }
}