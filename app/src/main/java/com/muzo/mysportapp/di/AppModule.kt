package com.muzo.mysportapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.muzo.mysportapp.db.RunningDatabase
import com.muzo.mysportapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.muzo.mysportapp.other.Constants.KEY_NAME
import com.muzo.mysportapp.other.Constants.KEY_WEIGHT
import com.muzo.mysportapp.other.Constants.RUNNING_DATABASE_NAME
import com.muzo.mysportapp.other.Constants.SHARED_PREFERENCES_NAME
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
    fun provideRunningDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app, RunningDatabase::class.java,
            RUNNING_DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase)=db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPrefenrences(@ApplicationContext app :Context)=
        app.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref:SharedPreferences)=sharedPref.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPref:SharedPreferences)=sharedPref.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref:SharedPreferences)
    =sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE,true)


}