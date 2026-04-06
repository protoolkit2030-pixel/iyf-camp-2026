package com.iyf.camp2026.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [InscriptionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inscriptionDao(): InscriptionDao

    companion object {
        const val DATABASE_NAME = "iyf_camp_2026.db"
    }
}
