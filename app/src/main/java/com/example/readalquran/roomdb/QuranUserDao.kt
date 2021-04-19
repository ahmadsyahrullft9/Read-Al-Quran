package com.example.readalquran.roomdb

import androidx.room.*
import com.example.readalquran.models.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranUserDao {

    @Query("SELECT * FROM bookmark")
    fun selectAllBookmark(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmark WHERE bookmark.id = :id")
    fun selectBookmark(id: Int): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark)

    @Update
    suspend fun updateBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmark")
    suspend fun deleleAllBookmark()
}