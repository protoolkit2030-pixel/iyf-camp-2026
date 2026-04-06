package com.iyf.camp2026.di

import android.content.Context
import androidx.room.Room
import com.iyf.camp2026.data.local.AppDatabase
import com.iyf.camp2026.data.local.InscriptionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideInscriptionDao(database: AppDatabase): InscriptionDao {
        return database.inscriptionDao()
    }
}
