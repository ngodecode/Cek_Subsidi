package com.fxlib.ceksubsidi

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fxlib.ceksubsidi.ui.main.SectionsPagerAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity() {

    companion object {
        val adsRewads = MutableLiveData<Boolean>()
    }

    private lateinit var mInterstitialAd: InterstitialAd
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        MobileAds.initialize(this)

        adsRewads?.observe(this, Observer {
            if (it) {
                showRewardAds()
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    var rewardedAd: RewardedAd? = null
    fun showRewardAds() {
        if (rewardedAd == null) {
            rewardedAd = RewardedAd(this, resources.getString(R.string.ads_unit_banner_reward))
        }
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdFailedToLoad(p0: Int) {
                super.onRewardedAdFailedToLoad(p0)
            }

            override fun onRewardedAdLoaded() {
                super.onRewardedAdLoaded()
                val adCallback = object: RewardedAdCallback() {
                    override fun onRewardedAdOpened() {
                    }
                    override fun onRewardedAdClosed() {
                    }
                    override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                        Toast.makeText(this@HomeActivity, "Terimakasih sudah menonton", Toast.LENGTH_SHORT);
                    }
                    override fun onRewardedAdFailedToShow(p0: Int) {
                    }
                }
                rewardedAd?.show(this@HomeActivity, null)
            }
        }
        rewardedAd?.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }
}