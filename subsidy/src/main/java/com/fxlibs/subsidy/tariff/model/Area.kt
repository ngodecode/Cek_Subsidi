package com.fxlibs.subsidy.tariff.model

sealed class Area(val id: String, val name:String) {
    class Province   (id:String, name:String) : Area(id, name)
    class District   (id:String, name:String) : Area(id, name)
    class SubDistrict(id:String, name:String) : Area(id, name)
    class Village    (id:String, name:String) : Area(id, name)

    override fun toString(): String {
        return name
    }
}
