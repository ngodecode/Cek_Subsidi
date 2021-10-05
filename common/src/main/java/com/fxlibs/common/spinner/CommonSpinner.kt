package com.fxlibs.common.spinner

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Spinner
import android.widget.ProgressBar
import android.widget.LinearLayout
import android.widget.SpinnerAdapter
import android.widget.AdapterView.OnItemSelectedListener
import com.fxlibs.common.R

class CommonSpinner(context:Context, attributes: AttributeSet) : androidx.appcompat.widget.AppCompatSpinner(context, attributes) {

//    private val spinner:Spinner
    private val loading:ProgressBar
//    init {
//        inflate(context, R.layout.common_spinner, this)
//        spinner = findViewById(R.id.spinner)
//        loading = findViewById(R.id.loading)
//    }
//

    init {
        loading = ProgressBar(context)
        addView(loading)
    }
    var isLoading:Boolean
    get() = loading.visibility == View.VISIBLE
    set(value) {
        loading.visibility = if (value) View.VISIBLE else View.GONE
    }
//
//    var adapter:SpinnerAdapter
//    get() = spinner.adapter
//    set(value) {
//        spinner.adapter = value
//    }
//
//    var onItemSelectedListener:OnItemSelectedListener?
//    get() = spinner.onItemSelectedListener
//    set(value) {
//        spinner.onItemSelectedListener = value
//    }
//
//    fun setSelection(position:Int) {
//        spinner.setSelection(position)
//    }
//
//    var selectedItem:Any? = spinner.selectedItem


}