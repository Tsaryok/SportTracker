package by.bsuir.sporttracker.db

import androidx.room.*
import by.bsuir.sporttracker.model.Track

@Dao
interface TrackDao {
    @Insert
    fun insertTrack(track: Track): Long

    @Update
    fun updateTrack(track: Track)

    @Delete
    fun deleteTrack(vararg tracks: Track)

    @Query("SELECT * FROM track")
    fun getAll(): List<Track>
}