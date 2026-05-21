package com.example.waqt.di

import android.content.Context
import com.example.waqt.prefs.PrayerPrefs
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
    fun providePrayerPrefs(@ApplicationContext context: Context): PrayerPrefs {
        return PrayerPrefs(context)
    }
}
