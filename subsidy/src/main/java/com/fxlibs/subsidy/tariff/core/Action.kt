package com.fxlibs.subsidy.tariff.core

import com.fxlibs.subsidy.tariff.model.Area

sealed class Action {

    object LoadProvince : Action()
    data class LoadDistrict   (val provinceId:String) : Action()
    data class LoadSubDistrict(val districtId:String) : Action()
    data class LoadVillage    (val subDistrictId:String) : Action()
    data class LoadStatus(val villageId:String, val customerId:String) : Action()

    data class SelectProvince   (val area: Area) : Action()
    data class SelectDistrict   (val area: Area) : Action()
    data class SelectSubDistrict(val area: Area) : Action()
    data class SelectVillage    (val area: Area) : Action()
    data class InputCustomerId(val customerId:String) : Action()

}