package com.fxlibs.subsidy.tariff.ui

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fxlibs.common.data.LoadState
import com.fxlibs.subsidy.R
import com.fxlibs.subsidy.databinding.FragmentTariffBinding
import com.fxlibs.subsidy.tariff.core.Action
import com.fxlibs.subsidy.tariff.core.State
import com.fxlibs.subsidy.tariff.core.TariffViewModel
import com.fxlibs.subsidy.tariff.model.Area
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
@ExperimentalCoroutinesApi
class TariffFragment : Fragment() {

    private val viewModel:TariffViewModel by sharedViewModel()
    lateinit var binding:FragmentTariffBinding
    
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTariffBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.wbInfo.apply {
            webViewClient = WebClient(requireActivity()) {
                parent.requestLayout()
            }
            loadUrl("file:///android_asset/subsidy_disclaimer_start.html")
        }
        val state    = viewModel.state
        val onAction = viewModel.action

        binding.apply {
            bindError     (state, onAction)
            bindProvince  (state, onAction)
            bindDistrict  (state, onAction)
            bindSubDistrict (state, onAction)
            bindVillage   (state, onAction)
            bindInputText (state, onAction)
            bindNextAction(state, onAction)
        }

        if (state.value.province == null) {
            Action.LoadProvince.let(onAction)
        }
    }

    private fun AutoCompleteTextView.setOnSelectedItem(state: StateFlow<State>, onAction:(Action) -> Unit) {
        this.setOnItemClickListener { _, _, position, _ ->
            val area = adapter.getItem(position) as? Area ?: return@setOnItemClickListener
            state.value.let {
                with(binding) {
                    when(this@setOnSelectedItem) {
                        spnProvince     -> if (it.province == area)    null else Action.SelectProvince(area)
                        spnDistrict     -> if (it.district == area)    null else Action.SelectDistrict(area)
                        spnSubDistrict  -> if (it.subDistrict == area) null else Action.SelectSubDistrict(area)
                        spnVillage      -> if (it.village     == area) null else Action.SelectVillage(area)
                        else -> null
                    }?.let(onAction)
                }
            }
        }
    }

    fun AutoCompleteTextView.setItems(items:List<Area>, selectedItem:Area?) {
        threshold = Integer.MAX_VALUE
        setAdapter(ArrayAdapter(requireContext(), R.layout.common_spinner_item, items))
        setText(selectedItem?.let {items.find {it2 -> it.id == it2.id}}?.name ?: "", false)
    }

    private fun FragmentTariffBinding.bindProvince(state:StateFlow<State>, onAction:(Action) -> Unit) {
        spnProvince.setOnSelectedItem(state, onAction)
        viewLifecycleOwner.lifecycleScope.launch {
            state
            .map {Pair(it.provinceList, it.province)}
            .collectLatest { (load, selectedItem) ->
                prgProvince.isVisible = load?.isLoading() == true
                spnProvince.setItems(load?.data() ?: emptyList(), selectedItem)
            }
        }
    }

    private fun FragmentTariffBinding.bindDistrict(state:StateFlow<State>, onAction:(Action) -> Unit) {
        spnDistrict.setOnSelectedItem(state, onAction)
        viewLifecycleOwner.lifecycleScope.launch {
            state
            .map {Pair(it.districtList, it.district)}
            .collectLatest { (load, selectedItem) ->
                prgDistrict.isVisible = load?.isLoading() == true
                spnDistrict.setItems(load?.data() ?: emptyList(), selectedItem)
            }
        }

    }

    private fun FragmentTariffBinding.bindSubDistrict(state:StateFlow<State>, onAction:(Action) -> Unit) {
        spnSubDistrict.setOnSelectedItem(state, onAction)
        viewLifecycleOwner.lifecycleScope.launch {
            state
            .map {Pair(it.subDistrictList, it.subDistrict)}
            .collectLatest { (load, selectedItem) ->
                prgSubDistrict.isVisible = load?.isLoading() == true
                spnSubDistrict.setItems(load?.data() ?: emptyList(), selectedItem)
            }
        }
    }

    private fun FragmentTariffBinding.bindVillage(state:StateFlow<State>, onAction:(Action) -> Unit) {
        spnVillage.setOnSelectedItem(state, onAction)
        viewLifecycleOwner.lifecycleScope.launch {
            state
            .map {Pair(it.villageList, it.village)}
            .collectLatest { (load, selectedItem) ->
                prgVillage.isVisible = load?.isLoading() == true
                spnVillage.setItems(load?.data() ?: emptyList(), selectedItem)
            }
        }
    }

    private fun FragmentTariffBinding.bindError(state:StateFlow<State>, onAction:(Action) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            state.map { it.error }.collectLatest {
                it?.let {error ->
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry) {
                        when {
                            spnProvince.adapter.count == 0 ->   Action.LoadProvince
                            spnDistrict.adapter.count == 0 ->   (spnProvince.selectedItem() as? Area)   ?.id?.let(Action::LoadDistrict)
                            spnSubDistrict.adapter.count == 0 ->(spnDistrict.selectedItem() as? Area)   ?.id?.let(Action::LoadSubDistrict)
                            spnVillage.adapter.count     == 0 ->(spnSubDistrict.selectedItem() as? Area)?.id?.let(Action::LoadVillage)
                            else -> null
                        }?.let(onAction)
                    }.show()
                }
            }
        }
    }

    private fun AutoCompleteTextView.selectedItem() : Any? {
        return if (this.listSelection == -1) null else adapter.getItem(this.listSelection)
    }

    private fun FragmentTariffBinding.bindNextAction(state:StateFlow<State>, onAction:(Action) -> Unit) {
        btnNext.setOnClickListener {
            findNavController().navigate(TariffFragmentDirections.actionNext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            state.collectLatest {
                btnNext.isEnabled = it.province != null
                                &&  it.district != null
                                &&  it.subDistrict != null
                                &&  it.village != null
                                &&  it.customerId?.length == 16
            }
        }
    }

    private fun FragmentTariffBinding.bindInputText(state:StateFlow<State>, onAction:(Action) -> Unit) {
        edtCustomerId.addTextChangedListener {
            Action.InputCustomerId(it.toString()).let(onAction)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            state.map { it.customerId }.distinctUntilChanged().collectLatest {
                if (it != edtCustomerId.text.toString()) {
                    edtCustomerId.setText(it)
                }
            }
        }
    }


}