package com.fxlibs.subsidy.tariff.ui

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.fxlibs.common.data.LoadState
import com.fxlibs.subsidy.R
import com.fxlibs.subsidy.databinding.FragmentInquiryBinding
import com.fxlibs.subsidy.tariff.core.Action
import com.fxlibs.subsidy.tariff.core.TariffViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [TariffInquiryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class TariffInquiryFragment : DialogFragment() {

    lateinit var binding:FragmentInquiryBinding
    private  var timer:CountDownTimer? = null
    private  var job:Job? = null

    @FlowPreview
    @ExperimentalCoroutinesApi
    private val viewModel:TariffViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAds.setOnClickListener {
            dismiss()
            findNavController().navigate(TariffInquiryFragmentDirections.actionShowInfo())
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
                if (binding.btnAds.isEnabled) {
                    findNavController().navigate(TariffInquiryFragmentDirections.actionShowInfo())
                }
            }

        }
        timer?.start()

    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timer?.cancel()
        viewLifecycleOwner.lifecycleScope.cancel()
    }

}