package com.elpet.kaizen.di

import android.content.Context
import androidx.room.Room
import com.elpet.kaizen.core.*
import com.elpet.kaizen.data.database.FavoriteEventsDatabase
import com.elpet.kaizen.ui.fragment.home.HomeInteractor
import com.elpet.kaizen.ui.fragment.home.HomeInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideLogger(): AppLogger = AppLoggerImpl()

    @Provides
    @Singleton
    fun providePrefsController(
        impl: PrefsControllerImpl
    ): PrefsController = impl

    @Provides
    @Singleton
    fun provideHomeInteractor(
        impl: HomeInteractorImpl
    ): HomeInteractor = impl

    @Provides
    @Singleton
    fun provideEventDatabase(
        @ApplicationContext context: Context,
    ): FavoriteEventsDatabase = Room.databaseBuilder(
        context,
        FavoriteEventsDatabase::class.java, "events"
    ).allowMainThreadQueries().build()

}