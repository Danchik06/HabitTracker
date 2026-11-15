package com.example.habittracker.di

import com.example.habittracker.domain.repository.HabitLogRepository
import com.example.habittracker.domain.repository.HabitRepository
import com.example.habittracker.domain.usecase.AddHabitUseCase
import com.example.habittracker.domain.usecase.CompleteHabitUseCase
import com.example.habittracker.domain.usecase.DeleteHabitUseCase
import com.example.habittracker.domain.usecase.GetAllHabitsUseCase
import com.example.habittracker.domain.usecase.GetHabitHistoryUseCase
import com.example.habittracker.domain.usecase.UpdateHabitUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetAllHabitsUseCase(
        repository: HabitRepository
    ) = GetAllHabitsUseCase(repository)

    @Provides
    fun provideAddHabitUseCase(
        repository: HabitRepository
    ) = AddHabitUseCase(repository)

    @Provides
    fun provideUpdateHabitUseCase(
        repository: HabitRepository
    ) = UpdateHabitUseCase(repository)

    @Provides
    fun provideDeleteHabitUseCase(
        repository: HabitRepository
    ) = DeleteHabitUseCase(repository)

    @Provides
    fun provideCompleteHabitUseCase(
        repository: HabitLogRepository
    ) = CompleteHabitUseCase(repository)

    @Provides
    fun provideGetHabitHistoryUseCase(
        repository: HabitLogRepository
    ) = GetHabitHistoryUseCase(repository)
}