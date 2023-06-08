package com.muzo.mysportapp.di

import android.content.Context
import androidx.room.Room
import com.muzo.mysportapp.db.RunningDatabase
import com.muzo.mysportapp.other.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideRunningDatabase(@ApplicationContext app: Context): RunningDatabase =
        Room.databaseBuilder(
            app, RunningDatabase::class.java,
            RUNNING_DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase)=db.getRunDao()
}