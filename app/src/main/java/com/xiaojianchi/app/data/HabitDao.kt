package com.xiaojianchi.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {
    @Transaction
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY sortOrder ASC, id ASC")
    fun observeActiveHabits(): Flow<List<HabitWithCheckIns>>

    @Insert
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun habitById(id: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long

    @Update
    suspend fun updateCheckIn(checkIn: CheckInEntity)

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND date = :date")
    suspend fun checkIn(
        habitId: Long,
        date: LocalDate,
    ): CheckInEntity?

    @Query("DELETE FROM check_ins WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCheckIn(
        habitId: Long,
        date: LocalDate,
    )
}
