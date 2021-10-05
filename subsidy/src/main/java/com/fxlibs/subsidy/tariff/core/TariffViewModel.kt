package com.fxlibs.subsidy.tariff.core

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fxlibs.common.data.LoadState
import com.fxlibs.subsidy.tariff.model.Area.Province
import com.fxlibs.subsidy.tariff.model.Area.District
import com.fxlibs.subsidy.tariff.model.Area.SubDistrict
import com.fxlibs.subsidy.tariff.model.Area.Village
import com.fxlibs.subsidy.tariff.model.SubsidyStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class TariffViewModel(private val savedStateHandle: SavedStateHandle, private val repository: TariffRepository) : ViewModel() {

    private val stateKey = "state_saved"

    private val _state  = MutableStateFlow(State())
    private val _action = BroadcastChannel<Action>(Channel.BUFFERED)

    val state: StateFlow<State> get() = _state.asStateFlow()
    val action:(Action) -> Unit =  {
        Log.w("APP_SEND", "Action: $it")
        viewModelScope.launch {
            _action.send(it)
        }
    }

    init {
        initReceiver()
        initState()
    }


    private fun initState() {
        savedStateHandle.get<State>(stateKey)?.let {
            _state.value = it
        }
    }

    private fun <T> Flow<T>.onCollect(action: suspend (value: T) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            collect(action)
        }
    }
    private fun initReceiver() {
        _action.asFlow().apply {
            filterIsInstance<Action.LoadProvince>()   .flatMapLatest(::onAction).onCollect(::onCollectProvince)
            filterIsInstance<Action.LoadDistrict>()   .flatMapLatest(::onAction).onCollect(::onCollectDistrict)
            filterIsInstance<Action.LoadSubDistrict>().flatMapLatest(::onAction).onCollect(::onCollectSubDistrict)
            filterIsInstance<Action.LoadVillage>()    .flatMapLatest(::onAction).onCollect(::onCollectVillage)
            filterIsInstance<Action.LoadStatus>()     .flatMapLatest(::onAction).onCollect(::onCollectStatus)

            filterIsInstance<Action.SelectProvince>().onCollect {
                _state.value = state.value.copy (province = it.area)
                _action.send(Action.LoadDistrict(it.area.id))
            }
            filterIsInstance<Action.SelectDistrict>().onCollect {
                _state.value = state.value.copy (district = it.area)
                _action.send(Action.LoadSubDistrict(it.area.id))
            }
            filterIsInstance<Action.SelectSubDistrict>().onCollect {
                _state.value = state.value.copy (subDistrict = it.area)
                _action.send(Action.LoadVillage(it.area.id))
            }
            filterIsInstance<Action.SelectVillage>().onCollect {
                _state.value = state.value.copy (village = it.area)
            }
            filterIsInstance<Action.InputCustomerId>().onCollect {
                _state.value = state.value.copy(customerId = it.customerId)
            }
        }

        _state.apply {
            map { it.province }.distinctUntilChanged().onCollect {
                _state.value = state.value.copy(
                    district = null,
                    districtList = null
                )
            }
            map { it.district }.distinctUntilChanged().onCollect {
                _state.value = state.value.copy(
                    subDistrict = null,
                    subDistrictList = null
                )
            }
            map { it.subDistrict }.distinctUntilChanged().onCollect {
                _state.value = state.value.copy(
                    village     = null,
                    villageList = null
                )
            }
        }

    }

    private fun onAction(param:Action.LoadProvince) : Flow<LoadState<List<Province>>> = flow {
        emit(LoadState.Loading())
        emit(LoadState.Loaded(repository.getProvince()))
    }

    private fun onCollectProvince(result:LoadState<List<Province>>) {
        _state.value = state.value.copy(
            provinceList = result,
            error = (result as? LoadState.Loaded)?.result?.exceptionOrNull()?.message
        )
    }

    private fun onAction(param:Action.LoadDistrict) : Flow<LoadState<List<District>>>  = flow {
        emit(LoadState.Loading())
        emit(LoadState.Loaded(repository.getDistrict(param.provinceId)))
    }

    private fun onCollectDistrict(result:LoadState<List<District>>) {
        _state.value = state.value.copy(
            districtList = result,
            error = (result as? LoadState.Loaded)?.result?.exceptionOrNull()?.message
        )
    }

    private fun onAction(param:Action.LoadSubDistrict) : Flow<LoadState<List<SubDistrict>>>  = flow {
        emit(LoadState.Loading())
        emit(LoadState.Loaded(repository.getSubDistrict(param.districtId)))
    }

    private fun onCollectSubDistrict(result:LoadState<List<SubDistrict>>) {
        _state.value = state.value.copy(
            subDistrictList = result,
            error = (result as? LoadState.Loaded)?.result?.exceptionOrNull()?.message
        )
    }

    private fun onAction(param:Action.LoadVillage) : Flow<LoadState<List<Village>>>  = flow {
        emit(LoadState.Loading())
        emit(LoadState.Loaded(repository.getVillage(param.subDistrictId)))
    }

    private fun onCollectVillage(result:LoadState<List<Village>>) {
        _state.value = state.value.copy(
            villageList = result,
            error = (result as? LoadState.Loaded)?.result?.exceptionOrNull()?.message
        )
    }

    private fun onAction(param:Action.LoadStatus) : Flow<LoadState<SubsidyStatus>>  = flow {
        emit(LoadState.Loading())
        emit(LoadState.Loaded(repository.getStatus(param.villageId, param.customerId)))
    }

    private fun onCollectStatus(result:LoadState<SubsidyStatus>) {
        _state.value = state.value.copy(
            subsidyStatus = result,
            error = (result as? LoadState.Loaded)?.result?.exceptionOrNull()?.message
        )
    }

}


