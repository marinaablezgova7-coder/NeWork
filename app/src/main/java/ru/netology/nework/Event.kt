package ru.netology.nework

data class Event(
    val id: Long,
    val author: String,
    val authorId: Long = 0L,
    val published: String,
    val content: String,
    val eventDate: String,
    val eventType: String,
    val likes: Int,
    val likedByMe: Boolean = false,
    val participants: Int = 0,
    val imageUrl: String? = null
)