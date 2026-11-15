package com.example.habittracker.di

import android.content.Context
import androidx.room.Room
import com.example.habittracker.data.local.AppDatabase
import com.example.habittracker.data.local.HabitDao
import com.example.habittracker.data.local.HabitLogDao
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
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideHabitDao(
        database: AppDatabase
    ): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideHabitLogDao(
        database: AppDatabase
    ): HabitLogDao {
        return database.habitLogDao()
    }
}