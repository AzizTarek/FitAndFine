package com.example.fitandfine_project.data.History


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitandfine_project.data.Goal.Goal
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface HistoryDao {
    @Query("SELECT * from history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<History>>

    @Query("SELECT * from history ")
    fun getAll(): List<History>

    @Query("SELECT * from history WHERE id = :id")
    fun getHistory(id: Int): Flow<History>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing History into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: History)

    @Update
    suspend fun update(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("DELETE from history")
    fun clearAllHistory()




}