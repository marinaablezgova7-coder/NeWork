package ru.netology.nework

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val authorId: Long = 0,
    val content: String,
    val likes: Int,
    val likedByMe: Boolean = false,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val link: String? = null,

    // Пользователи, упомянутые в посте
    val mentionedUserIds: List<Long> = emptyList()
)