package com.example.readalquran.viewmodels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.readalquran.datastores.DataStorePreff
import com.example.readalquran.datastores.HistoryReadQuran
import com.example.readalquran.models.Ayat
import com.example.readalquran.models.Bookmark
import com.example.readalquran.models.Conf
import com.example.readalquran.models.QuranData
import com.example.readalquran.paging.AyatDataSource
import com.example.readalquran.repositories.QuranRepository
import com.example.readalquran.roomdb.QuranUserDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class QuranDataViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repo: QuranRepository,
    private val dataStorePreff: DataStorePreff,
    private val quranUserDao: QuranUserDao
) : ViewModel() {

    lateinit var _ayatPagedList: LiveData<PagedList<Ayat>>

    val quranDataChannel = Channel<QuranData>(Channel.Factory.CONFLATED)
    val quranDataFlow = quranDataChannel.receiveAsFlow()

    val quranUserEventChannel = Channel<QuranUserEvent>()
    val quranUserEvent = quranUserEventChannel.receiveAsFlow()
    val bookmarkListFlow: Flow<List<Bookmark>> = quranUserDao.selectAllBookmark()

    val userDatastoreFlow = dataStorePreff.prefferenceFlow

    fun getQuranData() = viewModelScope.launch {
        val quranDataObj = repo.provideQuranData(context)
        quranDataChannel.send(quranDataObj)
    }

    fun getAyatPagedList(indexSura: Int, indexAyat: Int = 0): LiveData<PagedList<Ayat>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(Conf.POST_PER_PAGE)
            .build()
        val _ayatDataSourceFactory = createAyatDataSourceFactory(indexSura, indexAyat)
        _ayatPagedList = LivePagedListBuilder(_ayatDataSourceFactory, config).build()
        return _ayatPagedList
    }

    fun createAyatDataSourceFactory(indexSura: Int, indexAyat: Int): DataSource.Factory<Int, Ayat> {
        val result = object : DataSource.Factory<Int, Ayat>() {
            override fun create(): DataSource<Int, Ayat> {
                val dataSource = AyatDataSource(repo, indexSura, indexAyat)
                dataSource.page = indexAyat
                return dataSource
            }
        }
        return result
    }

    fun insertBookmark(bookmark: Bookmark) = viewModelScope.launch {
        quranUserDao.insertBookmark(bookmark)
        quranUserEventChannel.send(
            QuranUserEvent.BookmarkInserted(
                bookmark,
                "process insert bookmark successfully"
            )
        )
    }

    fun deleteBookmark(bookmark: Bookmark) = viewModelScope.launch {
        quranUserDao.deleteBookmark(bookmark)
        quranUserEventChannel.send(
            QuranUserEvent.BookmarkDeleted(
                bookmark,
                "process delete bookmark successfully"
            )
        )
    }

    fun updateBookmark(bookmark: Bookmark) = viewModelScope.launch {
        quranUserDao.updateBookmark(bookmark)
        quranUserEventChannel.send(
            QuranUserEvent.BookmarkUpdated(
                bookmark,
                "process update bookmark successfully"
            )
        )
    }

    fun updateHistoryReadQuran(historyReadQuran: HistoryReadQuran) = viewModelScope.launch {
        dataStorePreff.updateHistoryReadQuran(historyReadQuran)
    }

    sealed class QuranUserEvent {
        data class BookmarkInserted(val bookmark: Bookmark, val message: String) : QuranUserEvent()
        data class BookmarkUpdated(val bookmark: Bookmark, val message: String) : QuranUserEvent()
        data class BookmarkDeleted(val bookmark: Bookmark, val message: String) : QuranUserEvent()
    }
}