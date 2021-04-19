package com.example.readalquran.lib

import android.content.Context
import androidx.appcompat.widget.SearchView
import java.io.IOException

fun getJsonDataFromAsset(context: Context, fileName: String): String? {
    val jsonString: String
    try {
        jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return jsonString
}

fun convertToArabic(value: Int): String {
    return (value.toString() + "")
        .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
        .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
        .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
        .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
        .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
}

//membuat extention function baru untuk ditambahkan ke class "SearchView"
inline fun SearchView.onQueryTextChanged(crossinline listener:(String)->Unit){
    this.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            //orEmpty berfungsi mengembalikan string kosong apabila data null
            listener(newText.orEmpty())
            return true
        }
    })
}

val <T> T.exhause: T
    get() = this