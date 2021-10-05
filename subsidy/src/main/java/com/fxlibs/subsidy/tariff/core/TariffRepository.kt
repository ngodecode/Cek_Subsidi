package com.fxlibs.subsidy.tariff.core

import com.fxlibs.subsidy.tariff.model.Area.Province
import com.fxlibs.subsidy.tariff.model.Area.District
import com.fxlibs.subsidy.tariff.model.Area.SubDistrict
import com.fxlibs.subsidy.tariff.model.Area.Village
import com.fxlibs.subsidy.tariff.model.SubsidyStatus

class TariffRepository(private val source:TariffDataSource) {

    fun getProvince() : Result<List<Province>> {
        return source.getProvince()
    }
    fun getDistrict(provinceId:String) : Result<List<District>> {
        return source.getDistrict(provinceId)
    }
    fun getSubDistrict(districtId:String) : Result<List<SubDistrict>> {
        return source.getSubDistrict(districtId)
    }
    fun getVillage(subDistrictId:String) : Result<List<Village>> {
        return source.getVillage(subDistrictId)
    }
    fun getStatus(villageId:String, customerId:String) : Result<SubsidyStatus> {
        return source.getStatus(customerId, villageId)
    }

}