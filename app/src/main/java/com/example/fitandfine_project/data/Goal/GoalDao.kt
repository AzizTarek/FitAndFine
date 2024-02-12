package com.example.fitandfine_project.data.Goal


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface GoalDao {
    @Query("SELECT * from goals ORDER BY active DESC") //Shows the active goal on top
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * from goals WHERE id = :id")
    fun getGoal(id: Int): Flow<Goal>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Goal into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Goal)

    @Update
    suspend fun update(item: Goal)

    @Delete
    suspend fun delete(item: Goal)

    @Query("SELECT * from goals WHERE active = 1")
    fun getActiveGoal(): Flow<Goal>

    @Query("SELECT * from goals WHERE active = 1")
    fun getActive(): Goal

    @Query("UPDATE goals SET steps = 0, progress = 0.0, selectedDate =0, active =0")
    fun resetGoals()






}