package com.example.readalquran.datastores

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class HistoryReadQuran(
    val index_sura: Int,
    val name_sura: String,
    val name_sura_arab: String,
    val index_aya: Int,
    val time_add: Long
)

@Singleton
class DataStorePreff @Inject constructor(@ApplicationContext val context: Context) {

    private val TAG = "DataStorePreff"
    private val dataStore = context.createDataStore("user_prefference")

    val prefferenceFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading prefference", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefference ->
            val sura_index = prefference[PrefferenceKey.HISTORY_SURA_INDEX] ?: 1
            val sura_name = prefference[PrefferenceKey.HISTORY_SURA_NAME] ?: "-"
            val sura_name_arab = prefference[PrefferenceKey.HISTORY_SURA_NAME_ARAB] ?: "-"
            val index_ayat = prefference[PrefferenceKey.HISTORY_AYAT] ?: 1
            val time_add =
                prefference[PrefferenceKey.HISTORY_TIME_ADDED] ?: System.currentTimeMillis()
            HistoryReadQuran(sura_index, sura_name, sura_name_arab, index_ayat, time_add)
        }

    suspend fun updateHistoryReadQuran(historyReadQuran: HistoryReadQuran) {
        dataStore.edit { prefference ->
            prefference[PrefferenceKey.HISTORY_SURA_NAME] = historyReadQuran.name_sura
            prefference[PrefferenceKey.HISTORY_SURA_INDEX] = historyReadQuran.index_sura
            prefference[PrefferenceKey.HISTORY_SURA_NAME_ARAB] = historyReadQuran.name_sura_arab
            prefference[PrefferenceKey.HISTORY_AYAT] = historyReadQuran.index_aya
            prefference[PrefferenceKey.HISTORY_TIME_ADDED] = historyReadQuran.time_add
        }
    }

    private object PrefferenceKey {
        val HISTORY_SURA_NAME = preferencesKey<String>("sura_name")
        val HISTORY_SURA_INDEX = preferencesKey<Int>("sura_index")
        val HISTORY_SURA_NAME_ARAB = preferencesKey<String>("sura_name_arab")
        val HISTORY_AYAT = preferencesKey<Int>("ayat")
        val HISTORY_TIME_ADDED = preferencesKey<Long>("time_add")
    }
}