package com.example.salehub

import android.app.Application

class SHApp : Application() {
    companion object {
        private var APP_INSTANCE: SHApp? = null
        fun getAppContext() = APP_INSTANCE!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        APP_INSTANCE = this
    }
}