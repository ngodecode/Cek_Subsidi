package com.fxlib.ceksubsidi.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fxlib.ceksubsidi.CekStimulusCovidFragment
import com.fxlib.ceksubsidi.CekSubsidiFragment
import com.fxlib.ceksubsidi.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    var fragment1:Fragment? = null
    var fragment2:Fragment? = null

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            if (fragment1 == null) {
                fragment1 = CekSubsidiFragment()
            }
            return fragment1!!
        }
        else {
            if (fragment2 == null) {
                fragment2 = CekStimulusCovidFragment()
            }
            return fragment2!!
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}