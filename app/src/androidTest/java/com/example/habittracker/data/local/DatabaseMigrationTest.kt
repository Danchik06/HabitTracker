
package com.example.habittracker.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase

    private val databaseName = "migration_test_database"

    @Before
    fun setup() {
        context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        context.deleteDatabase(databaseName)

        // 1-нұсқадағы ескі деректер базасын құрамыз
        val oldDatabase = context.openOrCreateDatabase(
            databaseName,
            Context.MODE_PRIVATE,
            null
        )

        oldDatabase.execSQL("""
            CREATE TABLE IF NOT EXISTS habits (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                target_days TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                is_completed INTEGER NOT NULL
            )
        """)

        // Ескі базаға тест дерегін қосамыз
        oldDatabase.execSQL("""
            INSERT INTO habits (
                id, title, description, target_days,
                created_at, is_completed
            )
            VALUES (
                1, 'Reading', 'Read 20 pages',
                'MONDAY,TUESDAY', 1730000000000, 0
            )
        """)

        // Базаның нұсқасын 1 деп белгілейміз
        oldDatabase.version = 1
        oldDatabase.close()

        // 2-нұсқадағы Room Database құрамыз
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun migrationFrom1To2PreservesOldDataAndCreatesHabitLogs() =
        runBlocking {

            // База ашылғанда Migration автоматты орындалады
            val habits = database.habitDao()
                .getAllHabits()
                .first()

            // Бұрынғы дерек сақталуы керек
            assertEquals(1, habits.size)
            assertEquals("Reading", habits[0].title)
            assertEquals("Read 20 pages", habits[0].description)
            assertEquals(
                listOf("MONDAY", "TUESDAY"),
                habits[0].targetDays
            )

            // Жаңа habit_logs кестесі жасалғанын тексереміз
            val cursor = database.openHelper.readableDatabase.query(
                """
                SELECT name FROM sqlite_master
                WHERE type = 'table'
                AND name = 'habit_logs'
                """
            )

            val tableExists = cursor.moveToFirst()
            cursor.close()

            assertTrue(
                "habit_logs кестесі жасалмаған",
                tableExists
            )

            // База 2-нұсқаға жаңарғанын тексереміз
            assertEquals(2, database.openHelper.readableDatabase.version)
        }
}
