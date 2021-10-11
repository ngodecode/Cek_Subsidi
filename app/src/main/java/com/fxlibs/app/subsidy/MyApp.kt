package com.fxlibs.app.subsidy

import android.app.Application
import androidx.room.Room
import com.fxlibs.app.subsidy.data.AppDatabase
import com.fxlibs.app.subsidy.module.subsidy.SubsidyModule
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {

    lateinit var appOpenManager:AppOpenManager
    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {

        }
        appOpenManager = AppOpenManager(this)

        val appModule = module {
            single { provideDB() }
        }

        startKoin {
            modules(appModule, SubsidyModule.module)
        }

    }

    private fun provideDB() : AppDatabase {
        return Room.databaseBuilder(
            this,
            AppDatabase::class.java, "app-db"
        ).build()
    }


}