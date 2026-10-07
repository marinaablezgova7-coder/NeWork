package ru.netology.nework

data class Work(
    val id: Long,
    val userId: Long,
    val company: String,
    val start: String,
    val finish: String?,
    val position: String
)