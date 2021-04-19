package com.example.readalquran.paging

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.readalquran.models.Ayat
import com.example.readalquran.models.Conf
import com.example.readalquran.repositories.QuranRepository

class AyatDataSource(
    val quranRepository: QuranRepository,
    val indexSura: Int,
    val indexAyat: Int
) :
    PageKeyedDataSource<Int, Ayat>() {

    val TAG = "AyatDataSource"

    var limit = Conf.POST_PER_PAGE
    var page = 0

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Ayat>
    ) {
        val tmpLimit = limit
        if (indexAyat > 0) {
            page = 0
            limit = indexAyat
        }
        Log.d(TAG, "loadInitial: page = $page, limit = $limit")
        val ayatList =
            quranRepository.provideListAyat(indexSura = indexSura, page = page, limit = limit)
        if (ayatList.isNotEmpty()) {
            limit = tmpLimit
            callback.onResult(ayatList, null, page + ayatList.size)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Ayat>) {
        TODO("Not yet implemented")
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Ayat>) {
        val ayatList =
            quranRepository.provideListAyat(indexSura = indexSura, page = params.key, limit = limit)
        if (ayatList.isNotEmpty()) {
            callback.onResult(ayatList, params.key + ayatList.size)
        }
    }

}