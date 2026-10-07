package ru.netology.nework.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "works")
data class WorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val company: String,
    val start: String,
    val finish: String?,
    val position: String
)