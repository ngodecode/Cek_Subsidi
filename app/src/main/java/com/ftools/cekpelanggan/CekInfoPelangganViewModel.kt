package com.ftools.cekpelanggan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CekInfoPelangganViewModel : ViewModel() {
    val mTag = "CekInfoPelangganCVM"
    val dialog   = MutableLiveData<Boolean>()
    val success  = MutableLiveData<Boolean>()
    val result   = MutableLiveData<String>()
    val captcha  = MutableLiveData<Bitmap>()

    var mCookie : String? = null
    var mClient : OkHttpClient? = null


    fun initClient() {
        val builder = OkHttpClient.Builder()
        val trustAllCerts: Array<TrustManager> = arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        builder.sslSocketFactory(sslContext.socketFactory)
        mClient = builder.addInterceptor {
                chain -> chain.proceed(
                chain.request().newBuilder()
                .build()
        )
        }.build()

    }

    fun getCapcha() {
        dialog.postValue(true)
        mClient
            ?.newCall(Request.Builder().url("https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/SimpleCaptcha.jpg?validasi=5")
                .get()
                .addHeader("Host", "layanan.pln.co.id")
                .addHeader("Connection", "keep-alive")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .addHeader("Accept", "image/webp,*/*")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Sec-Fetch-Mode", "no-cors")
                .addHeader("Sec-Fetch-Dest", "image")
                .addHeader("Referer", "https://pelanggan.pln.co.id/PBMohon.html")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Cookie", "_ga=GA1.3.1439579538.1597333509; _gid=GA1.3.1006617863.1597333509")
                .build())
            ?.enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                dialog.postValue(false)
                Log.e(mTag, e?.message, e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                dialog.postValue(false)
                try {
                    var cookies = response?.headers()?.toMultimap()?.get("Set-Cookie")
                    if (cookies != null && cookies?.isNotEmpty()) {
                        mCookie = ""
                        cookies.forEach {
                            var cook = it?.substring(0, it?.indexOf(";")!!)
                            mCookie += ";$cook "
                        }
                    }

                    val image = response?.body()?.byteStream()?.readBytes()

                    if (mCookie == null) {
                        for (head in response?.headers()?.names()!!) {
                            Log.w(mTag, "Header>> " + head + ": " + response?.header(head))
                        }
                    }

                    Log.w(mTag, "Cookie: " + (mCookie ?: "NULL") + " image: " + image?.size)
                    if (mCookie != null && image != null) {
                        captcha.postValue(BitmapFactory.decodeByteArray(image, 0, image.size))
                    }
                    else {
                        result.postValue("Koneksi Bermasalah\nMohon Periksa Internet Anda")
                    }
                } catch (e:Exception) {
                    Log.e(mTag, e?.message, e)
                    result.postValue("Koneksi Bermasalah\nMohon Periksa Internet Anda")
                }

            }

        })
    }

    fun getInfo(capcha:String, idPel:String) {
        dialog.postValue(true)
        Log.w(mTag, "getInfo " + mCookie!! + " >> idPel: $idPel >> captcha:$capcha")
        mClient
            ?.newCall(Request.Builder().url("https://pelanggan.pln.co.id/id.co.iconpln.web.PDMohonEntryPoint/TransService")
                .post(RequestBody.create(MediaType.parse("text/x-gwt-rpc; charset=UTF-8"), "5|0|8|https://pelanggan.pln.co.id/id.co.iconpln.web.PDMohonEntryPoint/|AB6BB8F2A9B546B9DE2B259C9242D117|id.co.iconpln.web.client.service.TransService|getDataPelangganBykriteria|java.lang.String/2004016611|nometer|$idPel|$capcha|1|2|3|4|3|5|5|5|6|7|8|"))
                .addHeader("Host", "pelanggan.pln.co.id")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .addHeader("Content-Type", "text/x-gwt-rpc; charset=UTF-8")
                .addHeader("Accept", "*/*")
                .addHeader("Origin", "https://pelanggan.pln.co.id")
                .addHeader("Connection", "keep-alive")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Referer", "https://pelanggan.pln.co.id/id.co.iconpln.web.PDMohonEntryPoint/78FCC45720AE0CCB246A48FC48C44B87.cache.html")
                .addHeader("Accept-Language", "id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Cookie", "_ga=GA1.3.1439579538.1597333509; _gid=GA1.3.1006617863.1597333509" + mCookie!!)
                .build())?.enqueue(object : Callback{
                override fun onFailure(call: Call?, e: IOException?) {
                    dialog.postValue(false)
                    Log.e(mTag, e?.message, e)
                }

                override fun onResponse(call: Call?, response: Response?) {
                    dialog.postValue(false)
                    try {
                        var response = response?.body()?.string()
                        Log.w(mTag, response ?: " Response NULL")
                        response = response?.substring(4)
                        var data = JSONArray(response).getJSONArray(204)

                        //nama, nometer_kwh, idpel, daya, tarif bersubsidi

                        var nama  = data.getString(64)
                        var idpel = data.getString(57)
                        var daya  = data.getString(67)

                        var desc =  "NAMA : $nama" +
                                    "\nID PELANGGAN : $idpel" +
                                    "\nDAYA : $daya"
                        HomeActivity.adsRewads.postValue(true)
                        success.postValue(true)
                        result.postValue(desc)
                    } catch (e:Exception) {
                        Log.e(mTag, e?.message, e)
                    }
                }
            })
    }
}
