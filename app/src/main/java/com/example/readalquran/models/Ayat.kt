package com.example.readalquran.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ayat(
    val index: Int,
    val sura: Int,
    val aya: Int,
    val text: String
) : Parcelable
