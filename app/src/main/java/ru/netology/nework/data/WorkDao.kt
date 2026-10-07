package ru.netology.nework.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkDao {

    @Insert
    suspend fun insert(work: WorkEntity): Long

    @Query("SELECT * FROM works WHERE userId = :userId ORDER BY id DESC")
    suspend fun getByUserId(userId: Long): List<WorkEntity>

    @Delete
    suspend fun delete(work: WorkEntity)

    @Query("DELETE FROM works WHERE id = :workId")
    suspend fun deleteById(workId: Long)
}