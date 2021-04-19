package com.example.readalquran.repositories

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.readalquran.lib.DataBaseHelper
import com.example.readalquran.lib.getJsonDataFromAsset
import com.example.readalquran.models.Ayat
import com.example.readalquran.models.QuranData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class QuranRepository(val dataBaseHelper: DataBaseHelper) {

    private val TAG = "QuranRepository"

    init {
        dataBaseHelper.createDatabase()
    }

    fun provideListAyat(indexSura: Int = 1, page: Int = 0, limit: Int = 10): List<Ayat> {
        val ayatBismillah = dataBaseHelper.getBismillah()
        val ayaList = ArrayList<Ayat>()
        dataBaseHelper.openDatabase()
        val db: SQLiteDatabase = dataBaseHelper.database
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM quran_text WHERE sura LIKE $indexSura LIMIT $limit OFFSET $page",
            null
        )
        var i = 0
        if (cursor.moveToFirst()) {
            do {
                val index = cursor.getString(cursor.getColumnIndex("index")).toInt()
                var text = cursor.getString(cursor.getColumnIndex("text"))
                if (indexSura != 1 && ayatBismillah !== null)
                    text = text.replace(ayatBismillah.text, "", true)

                val ayat = Ayat(
                    index,
                    cursor.getString(cursor.getColumnIndex("sura")).toInt(),
                    cursor.getString(cursor.getColumnIndex("aya")).toInt(),
                    text
                )
                i++
                ayaList.add(ayat)
            } while (cursor.moveToNext());
            cursor.close();
        }
        Log.d(TAG, "provideListAyat: $i")
        dataBaseHelper.closeDataBase()
        return ayaList
    }

    fun provideQuranData(context: Context): QuranData {
        val jsonString = getJsonDataFromAsset(context, "metadata.json")
        Log.i("metadata", jsonString.toString())
        val gson = Gson()
        val quranDataType = object : TypeToken<QuranData>() {}.type
        val quranDataObj = gson.fromJson<QuranData>(jsonString, quranDataType)
        return quranDataObj
    }

}