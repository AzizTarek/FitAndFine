package com.example.fitandfine_project.data.History

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id : Int=0,
    @ColumnInfo (name="name")
    var name: String,
    val progress: Float,
    var steps: Int,
    val target: Int,
    @ColumnInfo (name = "date")
    var date: Int
) {

}
