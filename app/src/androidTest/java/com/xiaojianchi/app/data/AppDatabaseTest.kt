package com.xiaojianchi.app.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AppDatabaseTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: HabitRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = HabitRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun todayCannotBeDuplicatedAndUndoKeepsHabit() =
        runBlocking {
            val id = repository.saveHabit(HabitEntity(name = "读书", icon = "book", color = 0xFFB87452.toLong()))
            val today = LocalDate.now()
            assertTrue(repository.toggleToday(id, today))
            repository.setChecked(id, today, backfill = false)
            assertEquals(
                1,
                repository
                    .observeActiveHabits()
                    .first()
                    .single()
                    .checkIns.size,
            )
            assertEquals(false, repository.toggleToday(id, today))
            assertEquals(
                0,
                repository
                    .observeActiveHabits()
                    .first()
                    .single()
                    .checkIns.size,
            )
            assertEquals(
                "读书",
                repository
                    .observeActiveHabits()
                    .first()
                    .single()
                    .habit.name,
            )
        }

    @Test
    fun archivingPreservesPastCheckIns() =
        runBlocking {
            val id = repository.saveHabit(HabitEntity(name = "冥想", icon = "lotus", color = 0xFFB2934F.toLong()))
            repository.setChecked(id, LocalDate.now().minusDays(1), backfill = true)
            repository.saveNote(id, LocalDate.now().minusDays(1), "安静的五分钟")
            repository.archiveHabit(id)
            assertTrue(repository.observeActiveHabits().first().isEmpty())
            assertEquals("安静的五分钟", database.habitDao().checkIn(id, LocalDate.now().minusDays(1))?.note)
        }
}
