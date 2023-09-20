package iak.wrc.data.source.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import iak.wrc.data.entity.PastWeight

@Dao
interface PastWeightDao {
    @Query("select * from past_weight order by id desc")
    fun getAll(): List<PastWeight>

    @Insert
    fun insert(weight: PastWeight)
}