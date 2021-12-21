package com.fxlibs.subsidy.tariff.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.fxlibs.subsidy.BuildConfig
import com.fxlibs.subsidy.R
import com.fxlibs.subsidy.databinding.ActivitySubsidyBinding
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds

class SubsidyActivity : AppCompatActivity(), IUnityAdsInitializationListener {

    lateinit var binding: ActivitySubsidyBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivitySubsidyBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setSupportActionBar(toolbar)
        }
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.fgContainer) as? NavHostFragment ?: return
        navController = host.navController
        setupActionBarWithNavController(navController)

        UnityAds.initialize(applicationContext, BuildConfig.UNITY_GAME_ID, BuildConfig.UNITY_TEST_MODE, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.popBackStack() || super.onSupportNavigateUp()
    }

    override fun onInitializationComplete() {
    }

    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
    }

}