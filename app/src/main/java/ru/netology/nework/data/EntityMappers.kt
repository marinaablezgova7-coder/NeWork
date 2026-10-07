package ru.netology.nework.data

import ru.netology.nework.Event
import ru.netology.nework.Post

fun Post.toEntity(): PostEntity =
    PostEntity(
        id = id,
        author = author,
        authorId = authorId,
        published = published,
        content = content,
        likes = likes,
        likedByMe = likedByMe,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        link = link,
        mentionedUserIds = mentionedUserIds.joinToString(",")
    )

fun PostEntity.toModel(): Post =
    Post(
        id = id,
        author = author,
        published = published,
        authorId = authorId,
        content = content,
        likes = likes,
        likedByMe = likedByMe,
        imageUrl = imageUrl,
        videoUrl = videoUrl,
        link = link,
        mentionedUserIds = mentionedUserIds
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toLongOrNull() }
    )

fun Event.toEntity(): EventEntity =
    EventEntity(
        id = id,
        author = author,
        authorId = authorId,
        published = published,
        content = content,
        eventDate = eventDate,
        eventType = eventType,
        likes = likes,
        likedByMe = likedByMe,
        participants = participants,
        imageUrl = imageUrl
    )

fun EventEntity.toModel(): Event =
    Event(
        id = id,
        author = author,
        authorId = authorId,
        published = published,
        content = content,
        eventDate = eventDate,
        eventType = eventType,
        likes = likes,
        likedByMe = likedByMe,
        participants = participants,
        imageUrl = imageUrl
    )
