package com.example.fitandfine_project.data.Goal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id : Int=0,
    @ColumnInfo (name="name")
    var name: String,
    @ColumnInfo (name="active")
    var active: Boolean,
    @ColumnInfo (name="steps")
    var steps: Int,
    @ColumnInfo (name="target")
    val target: Int,
    @ColumnInfo (name="progress")
    val progress: Float,
    @ColumnInfo (name="selectedDate")
    var selectedDate: Int

    ) {

}
