package com.fxlibs.subsidy.tariff.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.fxlibs.subsidy.R
import com.fxlibs.subsidy.databinding.ActivitySubsidyBinding

class SubsidyActivity : AppCompatActivity() {

    lateinit var binding: ActivitySubsidyBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubsidyBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setSupportActionBar(toolbar)
        }
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.fgContainer) as? NavHostFragment ?: return
        navController = host.navController
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.popBackStack() || super.onSupportNavigateUp()
    }

}