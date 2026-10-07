package ru.netology.nework.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: Long,
    val author: String,
    val authorId: Long,
    val published: String,
    val content: String,
    val eventDate: String,
    val eventType: String,
    val likes: Int,
    val likedByMe: Boolean,
    val participants: Int,
    val imageUrl: String?
)
