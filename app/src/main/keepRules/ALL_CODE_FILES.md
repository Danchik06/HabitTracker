Файл 1: AppDatabase.kt
---
    package com.example.habittracker.data.local
    
    import android.content.Context
    import androidx.room.Database
    import androidx.room.Room
    import androidx.room.RoomDatabase
    import androidx.room.TypeConverters
    import androidx.room.migration.Migration
    import androidx.sqlite.db.SupportSQLiteDatabase
    
    @Database(
        entities = [
            HabitEntity::class,
            HabitLogEntity::class
        ],
        version = 2,
        exportSchema = false
    )
    @TypeConverters(Converters::class)
    abstract class AppDatabase : RoomDatabase() {
    
        abstract fun habitDao(): HabitDao
    
        abstract fun habitLogDao(): HabitLogDao
    
        companion object {
    
            @Volatile
            private var INSTANCE: AppDatabase? = null
    
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
    
                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `habit_logs` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `habit_id` INTEGER NOT NULL,
                            `date` TEXT NOT NULL,
                            `completed_count` INTEGER NOT NULL,
                            `is_completed` INTEGER NOT NULL,
                            FOREIGN KEY(`habit_id`)
                            REFERENCES `habits`(`id`)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                        )
                    """)
    
                    db.execSQL("""
                        CREATE UNIQUE INDEX IF NOT EXISTS
                        `index_habit_logs_habit_id_date`
                        ON `habit_logs` (`habit_id`, `date`)
                    """)
                }
            }
    
            fun getInstance(context: Context): AppDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "habit_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
    
                    INSTANCE = instance
                    instance
                }
            }
        }
    }
Файл 2: Converters.kt
---
    package com.example.habittracker.data.local

    import androidx.room.TypeConverter
    
    class Converters {
        @TypeConverter
        fun fromListToString(list: List<String>): String {
            return list.joinToString(",")
        }
        @TypeConverter
        fun toListFromString(data: String): List<String> {
            return if (data.isEmpty()) emptyList() else data.split(",")
        }
    }
Файл 3: HabitDao.kt
---
    package com.example.habittracker.data.local

    import androidx.room.Dao
    import androidx.room.Delete
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query
    import androidx.room.Update
    import kotlinx.coroutines.flow.Flow
    
    @Dao
    interface HabitDao {
    
        // 1. Барлық әдетті алу (жаңадан құрылғанынан бастап сұрыптау)
        // Flow қолданылғандықтан, реактивті түрде деректер өзгерсе автоматты жаңарады
        @Query("SELECT * FROM habits ORDER BY created_at DESC")
        fun getAllHabits(): Flow<List<HabitEntity>>
    
        // 2. Жаңа әдет қосу
        // OnConflictStrategy.REPLACE — егер ID қайталанса, ескісін жаңасымен алмастырады
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertHabit(habit: HabitEntity)
    
        // 3. Әдетті өшіру
        @Delete
        suspend fun deleteHabit(habit: HabitEntity)
    
        // 4. Әдетті жаңарту
        @Update
        suspend fun updateHabit(habit: HabitEntity)
    }
Файл 4: HabitEntity.kt
---
    package com.example.habittracker.data.local
    
    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.PrimaryKey
    
    @Entity(tableName = "habits")
    data class HabitEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0,
    
        @ColumnInfo(name = "title")
        val title: String,
    
        @ColumnInfo(name = "description")
        val description: String,
    
        @ColumnInfo(name = "target_days")
        val targetDays: List<String>,
    
        @ColumnInfo(name = "created_at")
        val createdAt: Long,
    
        @ColumnInfo(name = "is_completed")
        val isCompleted: Boolean = false
    )
Файл 5: HabitMapper.kt
---
    package com.example.habittracker.data.mapper
    
    import com.example.habittracker.data.local.HabitEntity
    import com.example.habittracker.domain.model.Habit
    
    // HabitEntity -> Habit (Domain)
    fun HabitEntity.toDomain(): Habit {
        return Habit(
            id = id,
            title = title,
            description = description,
            targetDays = targetDays,
            createdAt = createdAt,
            isCompleted = isCompleted
        )
    }
    
    // Habit (Domain) -> HabitEntity
    fun Habit.toEntity(): HabitEntity {
            return HabitEntity(
            id = id,
            title = title,
            description = description,
            targetDays = targetDays,
            createdAt = createdAt,
            isCompleted = isCompleted
        )
    }
Файл 6: HabitRepositoryImpl.kt
---
    package com.example.habittracker.data.repository

    import com.example.habittracker.data.local.HabitDao
    import com.example.habittracker.data.mapper.toDomain
    import com.example.habittracker.data.mapper.toEntity
    import com.example.habittracker.domain.model.Habit
    import com.example.habittracker.domain.repository.HabitRepository
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map
    
    class HabitRepositoryImpl(
        private val habitDao: HabitDao
    ) : HabitRepository {
    
        // 1. DAO-дан ағынды алып, Entity тізімін Domain тізіміне айналдырамыз
        override fun getAllHabits(): Flow<List<Habit>> {
            return habitDao.getAllHabits().map { entityList ->
                entityList.map { entity -> entity.toDomain() }
            }
        }
    
        // 2. Жаңа әдетті қосу командасын DAO-ға жіберу (Domain -> Entity)
        override suspend fun insertHabit(habit: Habit) {
            habitDao.insertHabit(habit.toEntity())
        }
    
        // 3. Әдетті өшіру командасы (Domain -> Entity)
        override suspend fun deleteHabit(habit: Habit) {
            habitDao.deleteHabit(habit.toEntity())
        }
    
        // 4. Әдетті жаңарту командасы (Domain -> Entity)
        override suspend fun updateHabit(habit: Habit) {
            habitDao.updateHabit(habit.toEntity())
        }
    }
Файл 7: Habit.kt
---
    package com.example.habittracker.domain.model

    data class Habit(
        val id: Long = 0,
        val title: String,
        val description: String,
        val targetDays: List<String>,
        val createdAt: Long,
        val isCompleted: Boolean = false
    )
Файл 8: HabitRepository.kt
---
    package com.example.habittracker.domain.repository

    import com.example.habittracker.domain.model.Habit
    import kotlinx.coroutines.flow.Flow
    
    interface HabitRepository {
    
        // Барлық әдеттерді бақылау (Domain моделінің тізімін қайтарады)
        fun getAllHabits(): Flow<List<Habit>>
    
        // Жаңа әдет қосу
        suspend fun insertHabit(habit: Habit)
    
        // Әдетті өшіру
        suspend fun deleteHabit(habit: Habit)
    
        // Әдетті жаңарту
        suspend fun updateHabit(habit: Habit)
    }
Файл 9: build.gradle.kts
---
    plugins {
        alias(libs.plugins.android.application)
        id("com.google.devtools.ksp")
    }
    
    android {
        namespace = "com.example.habittracker"
        compileSdk {
            version = release(37)
        }
        
        defaultConfig {
            applicationId = "com.example.habittracker"
            minSdk = 24
            targetSdk = 37
            versionCode = 1
            versionName = "1.0"
    
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    
        buildTypes {
            release {
                optimization {
                    enable = true
                    packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
                }
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }
        
    dependencies {
        implementation(libs.androidx.activity.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.core.ktx)
        implementation(libs.material)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation("androidx.test.ext:junit:1.2.1")
        androidTestImplementation("androidx.test:runner:1.6.2")
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        ksp(libs.androidx.room.compiler)
    }
Файл 10: HabitLogDao.kt
---
    package com.example.habittracker.data.local
    
    import androidx.room.Dao
    import androidx.room.Delete
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query
    import androidx.room.Update
    import kotlinx.coroutines.flow.Flow
    
    @Dao
    interface HabitLogDao {
    
        @Query("""
            SELECT * FROM habit_logs
            WHERE habit_id = :habitId
            ORDER BY date DESC
        """)
        fun getLogsForHabit(
            habitId: Long
        ): Flow<List<HabitLogEntity>>
    
        @Query("""
            SELECT * FROM habit_logs
            WHERE habit_id = :habitId
            AND date = :date
            LIMIT 1
        """)
        fun getLogForDate(
            habitId: Long,
            date: String
        ): Flow<HabitLogEntity?>
    
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertLog(log: HabitLogEntity): Long
    
        @Update
        suspend fun updateLog(log: HabitLogEntity)
    
        @Delete
        suspend fun deleteLog(log: HabitLogEntity)
    }

Файл 11: HabitLogEntity.kt
---
    package com.example.habittracker.data.local
    
    import androidx.room.ColumnInfo
    import androidx.room.Entity
    import androidx.room.ForeignKey
    import androidx.room.Index
    import androidx.room.PrimaryKey
    
    @Entity(
        tableName = "habit_logs",
        foreignKeys = [
            ForeignKey(
                entity = HabitEntity::class,
                parentColumns = ["id"],
                childColumns = ["habit_id"],
                onDelete = ForeignKey.CASCADE
            )
        ],
        indices = [
            Index(
                value = ["habit_id", "date"],
                unique = true
            )
        ]
    )
    data class HabitLogEntity(
    
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0,
    
        @ColumnInfo(name = "habit_id")
        val habitId: Long,
    
        // Күн форматы: yyyy-MM-dd
        @ColumnInfo(name = "date")
        val date: String,
    
        @ColumnInfo(name = "completed_count")
        val completedCount: Int = 0,
    
        @ColumnInfo(name = "is_completed")
        val isCompleted: Boolean = false
    )

package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

class GetAllHabitsUseCase(
private val repository: HabitRepository
) {
operator fun invoke(): Flow<List<Habit>> {
return repository.getAllHabits()
}
}

package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository

class AddHabitUseCase(
private val repository: HabitRepository
) {
suspend operator fun invoke(habit: Habit) {
repository.insertHabit(habit)
}
}

package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository

class UpdateHabitUseCase(
private val repository: HabitRepository
) {
suspend operator fun invoke(habit: Habit) {
repository.updateHabit(habit)
}
}

package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.Habit
import com.example.habittracker.domain.repository.HabitRepository

class DeleteHabitUseCase(
private val repository: HabitRepository
) {
suspend operator fun invoke(habit: Habit) {
repository.deleteHabit(habit)
}
}

package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.first

class CompleteHabitUseCase(
private val repository: HabitLogRepository
) {
suspend operator fun invoke(
habitId: Long,
date: String
) {
val existingLog = repository
.getLogForDate(habitId, date)
.first()

        if (existingLog == null) {
            repository.insertLog(
                HabitLog(
                    habitId = habitId,
                    date = date,
                    completedCount = 1,
                    isCompleted = true
                )
            )
        } else if (!existingLog.isCompleted) {
            repository.updateLog(
                existingLog.copy(
                    isCompleted = true,
                    completedCount = maxOf(
                        existingLog.completedCount,
                        1
                    )
                )
            )
        }
    }
}

package com.example.habittracker.domain.usecase

import com.example.habittracker.domain.model.HabitLog
import com.example.habittracker.domain.repository.HabitLogRepository
import kotlinx.coroutines.flow.Flow

class GetHabitHistoryUseCase(
private val repository: HabitLogRepository
) {
operator fun invoke(habitId: Long): Flow<List<HabitLog>> {
return repository.getLogsForHabit(habitId)
}
}
