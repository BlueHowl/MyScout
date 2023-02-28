package be.helmo.myscout

import android.app.Application
import be.helmo.myscout.database.MyScoutDatabase

class MyScoutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MyScoutDatabase.initDatabase(baseContext)
    }

    override fun onTerminate() {
        super.onTerminate()
        MyScoutDatabase.disconnectDatabase()
    }
}