package com.example.planix

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class PlanixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
