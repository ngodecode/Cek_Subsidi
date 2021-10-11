package com.fxlibs.app.subsidy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.fxlibs.app.subsidy.data.AppDatabase
import com.fxlibs.subsidy.tariff.ui.SubsidyActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashScreenActivity : AppCompatActivity() , KoinComponent{

    private val db:AppDatabase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lifecycleScope.launch(Dispatchers.IO) {
            if (db.userAccessDao().getUserAccess() == null) {
                lifecycleScope.launch {
                    finish()
                    startActivity(Intent(this@SplashScreenActivity, UserAgreementActivity::class.java))
                }
            }
            else {
                lifecycleScope.launch {
                    finish()
                    startActivity(Intent(this@SplashScreenActivity, SubsidyActivity::class.java))
                }
            }
        }
    }

}