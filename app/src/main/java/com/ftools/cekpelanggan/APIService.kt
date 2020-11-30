package com.ftools.cekpelanggan

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {
    @GET("id.co.iconpln.web.PBMohonEntryPoint/SimpleCaptcha.jpg")
    fun getSession(): Call<String>

    @POST("id.co.iconpln.web.PBMohonEntryPoint/MasterService.js")
    fun getArea(@Body body:String): Call<String>

    @POST("id.co.iconpln.web.PBMohonEntryPoint/TransService")
    fun getStatus(@Header("Cookie") sesionId : String, @Body body:String): Call<String>
}