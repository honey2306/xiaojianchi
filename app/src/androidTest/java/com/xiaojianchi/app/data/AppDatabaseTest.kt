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
    fun savingWithPriorDaysKeepsTheStreakSoFar() =
        runBlocking {
            val today = LocalDate.now()
            val id =
                repository.saveHabit(
                    HabitEntity(name = "跑步", icon = "move", color = 0xFF5C8371.toLong(), createdDate = today.minusDays(30)),
                    priorDays = 3,
                    today = today,
                )
            val item = repository.observeActiveHabits().first().single()
            assertEquals(
                setOf(today.minusDays(1), today.minusDays(2), today.minusDays(3)),
                item.checkIns.map { it.date }.toSet(),
            )
            assertEquals(HabitStats(total = 3, current = 3, longest = 3), calculateHabitStats(item.checkIns, today))
            assertEquals(id, item.habit.id)
        }

    @Test
    fun priorDaysAreClampedBeforeTheStartDate() =
        runBlocking {
            val today = LocalDate.now()
            repository.saveHabit(
                HabitEntity(name = "冥想", icon = "lotus", color = 0xFFB2934F.toLong(), createdDate = today.minusDays(2)),
                priorDays = 10,
                today = today,
            )
            assertEquals(
                2,
                repository
                    .observeActiveHabits()
                    .first()
                    .single()
                    .checkIns.size,
            )
        }

    @Test(expected = IllegalArgumentException::class)
    fun datesBeforeTheStartDateCannotBeCheckedIn() {
        runBlocking {
            val today = LocalDate.now()
            val id = repository.saveHabit(HabitEntity(name = "早睡", icon = "spark", color = 0xFF4E7E91.toLong()))
            repository.setChecked(id, today.minusDays(30), backfill = true)
        }
    }

    @Test
    fun archivingPreservesPastCheckIns() =
        runBlocking {
            val today = LocalDate.now()
            val id =
                repository.saveHabit(
                    HabitEntity(name = "冥想", icon = "lotus", color = 0xFFB2934F.toLong(), createdDate = today.minusDays(1)),
                )
            repository.setChecked(id, today.minusDays(1), backfill = true)
            repository.saveNote(id, today.minusDays(1), "安静的五分钟")
            repository.archiveHabit(id)
            assertTrue(repository.observeActiveHabits().first().isEmpty())
            assertEquals("安静的五分钟", database.habitDao().checkIn(id, today.minusDays(1))?.note)
        }

    @Test(expected = IllegalArgumentException::class)
    fun oldStartsDoNotWidenTheBackfillWindow() {
        runBlocking {
            val today = LocalDate.now()
            val id =
                repository.saveHabit(
                    HabitEntity(name = "拉伸", icon = "lotus", color = 0xFFB2934F.toLong(), createdDate = today.minusDays(30)),
                )
            repository.setChecked(id, today.minusDays(8), backfill = true)
        }
    }
}
