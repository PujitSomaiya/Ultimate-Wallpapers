package com.pmggroup.ultimatewallpapers.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

@SuppressLint("StaticFieldLeak")
class UltimateWallpapersApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        //Parse SDK stuff goes here
        context = this
    }

}