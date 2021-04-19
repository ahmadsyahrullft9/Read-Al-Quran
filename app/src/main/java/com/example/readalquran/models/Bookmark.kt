package com.example.readalquran.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "bookmark")
@Parcelize
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "date_created")
    val date_created: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "index_sura")
    val index_sura: Int,
    @ColumnInfo(name = "name_sura")
    val name_sura: String,
    @ColumnInfo(name = "index_aya")
    val index_aya: Int
) : Parcelable