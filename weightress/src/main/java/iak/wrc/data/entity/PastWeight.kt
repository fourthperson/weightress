package iak.wrc.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "past_weight")
data class PastWeight(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "notes") val notes: String,
    @ColumnInfo(name = "date") val date: Int
)
