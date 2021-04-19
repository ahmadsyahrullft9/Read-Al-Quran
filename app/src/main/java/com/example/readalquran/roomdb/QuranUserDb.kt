package com.example.readalquran.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.readalquran.models.Bookmark

@Database(entities = [Bookmark::class], version = 1)
abstract class QuranUserDb : RoomDatabase() {

    abstract fun quranUserDao(): QuranUserDao
}