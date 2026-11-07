package com.xiaojianchi.app.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val reminderMinutes: Int? = null,
    val createdDate: LocalDate = LocalDate.now(),
    val sortOrder: Int = 0,
    val archived: Boolean = false,
)

@Entity(
    tableName = "check_ins",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["habitId", "date"], unique = true)],
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: LocalDate,
    val checkedAt: Long = System.currentTimeMillis(),
    val note: String = "",
    val backfill: Boolean = false,
)

data class HabitWithCheckIns(
    @Embedded val habit: HabitEntity,
    @Relation(parentColumn = "id", entityColumn = "habitId")
    val checkIns: List<CheckInEntity>,
)
