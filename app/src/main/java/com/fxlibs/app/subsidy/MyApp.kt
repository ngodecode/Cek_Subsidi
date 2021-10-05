package com.fxlibs.app.subsidy

import android.app.Application
import com.fxlibs.app.subsidy.module.subsidy.SubsidyModule
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(SubsidyModule.module)
        }

    }
}