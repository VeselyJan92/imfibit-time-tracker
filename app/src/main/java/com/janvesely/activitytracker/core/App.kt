package com.janvesely.activitytracker.core

import android.app.Application
import android.content.Context
import com.janvesely.getitdone.database.AppDatabase

open class App : Application() {

    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)

        context = applicationContext
    }


}