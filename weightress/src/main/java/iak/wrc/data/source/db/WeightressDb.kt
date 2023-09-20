package iak.wrc.data.source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import iak.wrc.data.entity.PastWeight

@Database(entities = [PastWeight::class], version = 1)
abstract class WeightressDb : RoomDatabase() {
    abstract fun pasWeightDao(): PastWeightDao

    companion object {
        @Volatile
        private var instance: WeightressDb? = null

        fun getInstance(context: Context): WeightressDb {
            return instance ?: synchronized(this) {
                instance ?: buildDb(context).also { instance = it }
            }
        }

        private fun buildDb(context: Context): WeightressDb {
            return Room.databaseBuilder(context, WeightressDb::class.java, "weightress-db")
                .addCallback(object : Callback() {}).build()
        }
    }
}