package by.bsuir.sporttracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import by.bsuir.sporttracker.model.Plan
import by.bsuir.sporttracker.model.Track

@Database(entities = [Track::class, Plan::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun planDao(): PlanDao
}