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