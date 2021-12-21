package com.fxlibs.subsidy.tariff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fxlibs.subsidy.BuildConfig
import com.fxlibs.subsidy.databinding.FragmentTariffBinding
import com.fxlibs.subsidy.databinding.FragmentTariffInfoBinding
import com.fxlibs.subsidy.tariff.core.TariffViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class TariffInfoFragment : Fragment() {

    lateinit var binding: FragmentTariffInfoBinding

    @FlowPreview
    @ExperimentalCoroutinesApi
    val viewModel:TariffViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTariffInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.apply {
                  province = it.province
                  district = it.district
                  subDistrict = it.subDistrict
                  village  = it.village
                  status   = it.subsidyStatus?.data()
                  executePendingBindings()
                }
            }
        }
        binding.wbInfo.apply {
            webViewClient = WebClient(requireActivity()) {
                parent.requestLayout()
            }
            loadUrl(BuildConfig.DISCLAIMER_END_URL)
        }
        binding.bindAds()
        binding.bindAdsUnity()
    }

    private fun FragmentTariffInfoBinding.bindAds() {
        val ads = AdView(requireContext())
        ads.adSize = AdSize.BANNER
        ads.adUnitId = BuildConfig.ADS_BANNER2
        ads.loadAd(AdRequest.Builder().build())
        adView.addView(ads, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun FragmentTariffInfoBinding.bindAdsUnity() {
        val listener = object: BannerView.IListener {
            override fun onBannerLoaded(p0: BannerView?) {
            }

            override fun onBannerClick(p0: BannerView?) {
            }

            override fun onBannerFailedToLoad(p0: BannerView?, p1: BannerErrorInfo?) {
            }

            override fun onBannerLeftApplication(p0: BannerView?) {
            }
        }
        val ads = BannerView(requireActivity(), BuildConfig.UNITY_ADS_BANNER2, UnityBannerSize.getDynamicSize(requireContext()))
        ads.listener = listener
        ads.load()
        adView.addView(ads, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }


}