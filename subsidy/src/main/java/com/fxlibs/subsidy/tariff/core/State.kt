package com.fxlibs.subsidy.tariff.core

import com.fxlibs.common.data.LoadState
import com.fxlibs.subsidy.tariff.model.Area
import com.fxlibs.subsidy.tariff.model.SubsidyStatus

data class State(
    val provinceList:LoadState<List<Area.Province>>? = null,
    val districtList:LoadState<List<Area.District>>? = null,
    val subDistrictList:LoadState<List<Area.SubDistrict>>? = null,
    val villageList:LoadState<List<Area.Village>>?         = null,
    val subsidyStatus: LoadState<SubsidyStatus>? = null,

    val error:String? = null,

    val province: Area? = null,
    val district: Area? = null,
    val subDistrict: Area? = null,
    val village    : Area? = null,
    val customerId:String?  = null
)