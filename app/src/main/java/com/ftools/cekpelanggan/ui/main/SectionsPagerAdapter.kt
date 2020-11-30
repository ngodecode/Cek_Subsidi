package com.ftools.cekpelanggan.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ftools.cekpelanggan.CekInfoPelangganFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    var fragment2:Fragment? = null

    override fun getItem(position: Int): Fragment {
        if (fragment2 == null) {
            fragment2 = CekInfoPelangganFragment()
        }
        return fragment2!!
    }

    override fun getCount(): Int {
        return 1
    }
}