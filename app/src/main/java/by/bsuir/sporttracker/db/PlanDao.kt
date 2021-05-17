package by.bsuir.sporttracker.db

import androidx.room.*
import by.bsuir.sporttracker.model.Plan

@Dao
interface PlanDao {
    @Insert
    fun insertPlan(plan: Plan): Long

    @Update
    fun updatePlan(plan: Plan)

    @Delete
    fun deletePlan(vararg plans: Plan)

    @Query("SELECT * FROM `plan` WHERE trackId = :trackId")
    fun getPlansByTrackId(trackId: Long): List<Plan>
}