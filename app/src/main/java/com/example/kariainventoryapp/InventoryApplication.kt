package com.example.kariainventoryapp

import android.app.Application
import com.example.kariainventoryapp.data.AppContainer
import com.example.kariainventoryapp.data.AppDataContainer

class InventoryApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
