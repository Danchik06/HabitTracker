package com.example.habittracker.di

import com.example.habittracker.data.local.HabitDao
import com.example.habittracker.data.local.HabitLogDao
import com.example.habittracker.data.repository.HabitLogRepositoryImpl
import com.example.habittracker.data.repository.HabitRepositoryImpl
import com.example.habittracker.domain.repository.HabitLogRepository
import com.example.habittracker.domain.repository.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitDao: HabitDao
    ): HabitRepository {
        return HabitRepositoryImpl(habitDao)
    }

    @Provides
    @Singleton
    fun provideHabitLogRepository(
        habitLogDao: HabitLogDao
    ): HabitLogRepository {
        return HabitLogRepositoryImpl(habitLogDao)
    }
}