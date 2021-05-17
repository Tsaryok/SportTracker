package by.bsuir.sporttracker.model

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity
class Plan(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    val trackId: Long,
    var sectionString: String
) {

    companion object{
        private val TAG: String = Plan::class.java.simpleName
        private val gson = Gson()
    }

    @Ignore
    var sections: ArrayList<Section> = if (sectionString != "")
                ArrayList(gson.fromJson(sectionString, Array<Section>::class.java).toList())
            else
                arrayListOf()
        private set
    @Ignore
    var timeMilliseconds = 0
        private set
    @Ignore
    var distanceInMeters = 0F
        private set

    init {
        updateDistance()
        updateTime()
        Log.i(TAG, "Plan is initialized")
    }

    constructor(track: Track):this(0, "", track.id, ""){
        if (track.points.size > 1) {
            val segments: ArrayList<Segment> = arrayListOf()
            val iterator = track.points.iterator()
            var temp = iterator.next()
            while (iterator.hasNext()) {
                val cur = iterator.next()
                segments.add(Segment(temp, cur))
                temp = cur
            }
            sections = arrayListOf(Section(segments))
            distanceInMeters = sections.last().distanceInMeters
            timeMilliseconds = sections.last().timeMilliseconds
            updateSectionString()
        }else{
            sections = arrayListOf()
        }
    }

    fun addCheckpoint(id: Int){
        var segments = 0
        Log.i(TAG, "Adding checkpoint: $sections")
        for (section in sections){
            segments += section.segments.size
            if (segments > id){
                sections.add(sections.indexOf(section),section.splitSection(id - (segments - section.segments.size)))
                break;
            }else if (segments == id){
                break;
            }
        }
        Log.i(TAG, "After adding checkpoint: $sections")
        updateTime()
        updateDistance()
        updateSectionString()
    }

    fun deleteCheckpoint(id: Int){
        var segments = 0
        for (section in sections){
            segments += section.segments.size
            if (segments == id){
                val nextIndex = sections.indexOf(section) + 1
                section.uniteSections(sections[nextIndex])
                sections.remove(sections[nextIndex])
                break;
            }
        }
        updateTime()
        updateDistance()
        updateSectionString()
    }

    private fun updateTime(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeMilliseconds = sections.stream().map { it.timeMilliseconds }.reduce { d1, d2 -> d1 + d2 }.get()
        }else{
            var tempTime = 0
            for (section in sections){
                tempTime += section.timeMilliseconds
            }
            timeMilliseconds = tempTime
        }
    }

    private fun updateDistance(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            distanceInMeters = sections.stream().map { it.distanceInMeters }.reduce { d1, d2 -> d1 + d2 }.get()
        }else{
            var tempDistance = 0F
            for (section in sections){
                tempDistance += section.distanceInMeters
            }
            distanceInMeters = tempDistance
        }
    }

    private fun updateSectionString(){
        sectionString = gson.toJson(sections)
    }
}