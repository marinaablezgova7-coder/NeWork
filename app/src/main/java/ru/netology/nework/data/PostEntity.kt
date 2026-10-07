package ru.netology.nework.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: Long,
    val author: String,
    val authorId: Long,
    val published: String,
    val content: String,
    val likes: Int,
    val likedByMe: Boolean,
    val imageUrl: String?,
    val videoUrl: String?,
    val link: String?,
    val mentionedUserIds: String
)
