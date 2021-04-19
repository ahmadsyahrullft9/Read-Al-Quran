package com.example.readalquran

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.readalquran.lib.DataBaseHelper
import com.example.readalquran.repositories.QuranRepository
import com.example.readalquran.roomdb.QuranUserDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideDataBaseHelper(@ApplicationContext context: Context): DataBaseHelper {
        return DataBaseHelper(context)
    }

    @Provides
    fun provideQuranRepository(dataBaseHelper: DataBaseHelper) = QuranRepository(dataBaseHelper)

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, QuranUserDb::class.java, "quran_user_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideQuranUserDao(db: QuranUserDb) = db.quranUserDao()
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope