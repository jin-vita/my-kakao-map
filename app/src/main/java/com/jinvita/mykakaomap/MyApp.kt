package com.jinvita.mykakaomap

import android.app.Application
import android.content.Context
import com.jinvita.mykakaomap.util.PreferenceUtil

class MyApp : Application() {

    companion object {
        private lateinit var prefs: PreferenceUtil
        private lateinit var instance: MyApp
        fun getContext(): Context = instance
        fun getPrefs(): PreferenceUtil = prefs
    }

    override fun onCreate() {
        instance = this
        prefs = PreferenceUtil(getContext())
        super.onCreate()
    }
}