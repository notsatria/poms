package com.notsatria.poms.di

import android.content.Context
import com.notsatria.poms.data.preferences.SettingPreference
import com.notsatria.poms.data.preferences.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {

    @Provides
    @Singleton
    fun provideSettingPreference(@ApplicationContext context: Context): SettingPreference = SettingPreference(context.dataStore)
}