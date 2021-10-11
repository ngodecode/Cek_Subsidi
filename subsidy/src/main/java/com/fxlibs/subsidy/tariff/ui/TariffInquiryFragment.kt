package com.fxlibs.subsidy.tariff.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fxlibs.subsidy.BuildConfig
import com.fxlibs.subsidy.databinding.FragmentInquiryBinding
import com.fxlibs.subsidy.tariff.core.Action
import com.fxlibs.subsidy.tariff.core.TariffViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [TariffInquiryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@ExperimentalCoroutinesApi
@FlowPreview
class TariffInquiryFragment : DialogFragment() {

    lateinit var binding:FragmentInquiryBinding
    private  var timer:CountDownTimer? = null

    private val viewModel:TariffViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAds.setOnClickListener {
            showAds()
        }

        with(viewModel.state.value) {
            if (village?.id != null && customerId != null) {
                Action.LoadStatus(village.id, customerId).let(viewModel.action)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.map { it.subsidyStatus }.collectLatest {
                binding.btnAds.isEnabled = it?.data()?.consumeOnce() != null
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.map { it.error }.collectLatest {
                it?.let {
                    dismiss()
                }
            }
        }

        timer = object : CountDownTimer(60000L, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
                binding.txtTimer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                dismiss()
                navigateToResult()
            }

        }
        timer?.start()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timer?.cancel()
        viewLifecycleOwner.lifecycleScope.cancel()
    }

    private var mRewardedAd: RewardedAd? = null

    private fun showAds() {
        timer ?.cancel()
        binding.btnAds.isVisible = false
        binding.prgAds.isVisible = true
        RewardedAd.load(
            requireActivity(),
            BuildConfig.ADS_REWARD,
            AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mRewardedAd = null
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                    binding.btnAds.isVisible = true
                    binding.prgAds.isVisible = false
                    dismiss()
                    navigateToResult()
                }
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardedAd = rewardedAd
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                    mRewardedAd?.show(requireActivity()) { _ ->
                        dismiss()
                        navigateToResult()
                    }
                }
            }
        })
    }

    private fun navigateToResult() {
        viewModel.state.value.subsidyStatus?.data()?.let {
            findNavController().navigate(TariffInquiryFragmentDirections.actionShowInfo())
        }
    }

}