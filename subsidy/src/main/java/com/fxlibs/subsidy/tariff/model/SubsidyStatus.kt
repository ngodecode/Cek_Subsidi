package com.fxlibs.subsidy.tariff.model

data class SubsidyStatus(val customerId:String, val status:String? = null) {

    private var consumed = false
    fun consumeOnce() : SubsidyStatus? = if (consumed) {
        null
    }
    else {
        consumed = true
        this
    }

}
