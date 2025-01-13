package com.notsatria.poms

import android.app.Application
import com.notsatria.poms.BuildConfig.DEBUG
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}