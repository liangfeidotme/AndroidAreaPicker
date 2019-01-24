package me.liangfei.areapicker.sample

import android.app.Application

/**
 * Created by LIANG.FEI on 24/1/2019.
 */
class App : Application() {
    companion object {
        lateinit var instance: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}