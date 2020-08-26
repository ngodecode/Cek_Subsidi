package com.ftools.ceksubsidi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CekStimulusCovidViewModel : ViewModel() {
    val mTag = "CekStimulusCVM"
    val dialog = MutableLiveData<Boolean>()
    val success = MutableLiveData<Boolean>()
    val result  = MutableLiveData<String>()
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
            ?.newCall(Request.Builder().url("https://stimulus.pln.co.id/kaptcha.jpg")
                .get()
                .addHeader("Host", "stimulus.pln.co.id")
                .addHeader("Connection", "keep-alive")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .addHeader("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Sec-Fetch-Mode", "no-cors")
                .addHeader("Sec-Fetch-Dest", "image")
                .addHeader("Referer", "https://stimulus.pln.co.id/")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Cookie", "_ga=GA1.3.1439579538.1597333509; _gid=GA1.3.1006617863.1597333509; SRVNAME=g.242.11")
                .build())
            ?.enqueue(object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                dialog.postValue(false)
                Log.e(mTag, e?.message, e)
            }

            override fun onResponse(call: Call?, response: Response?) {
                dialog.postValue(false)
                try {
                    mCookie   = response?.headers()?.get("Set-Cookie")
                    val image = response?.body()?.byteStream()?.readBytes()

                    if (mCookie == null) {
                        for (head in response?.headers()?.names()!!) {
                            Log.w(mTag, "Header>> " + head + ": " + response?.header(head))
                        }
                    }

                    Log.w(mTag, "Cookie: " + (mCookie ?: "NULL") + " image: " + image?.size)
                    if (mCookie != null && image != null) {
                        mCookie = mCookie?.substring(0, mCookie?.indexOf(";")!!)
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
        Log.w(mTag, "getInfo " + mCookie!! + " >> " + "in_jeniscari=IDPEL&in_id=$idPel&in_kaptcha=$capcha")
        mClient
            ?.newCall(Request.Builder().url("https://stimulus.pln.co.id/api/pelanggan/getInfoTmp")
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "in_jeniscari=IDPEL&in_id=$idPel&in_kaptcha=$capcha"))
                .addHeader("Host", "stimulus.pln.co.id")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .addHeader("Origin", "https://stimulus.pln.co.id")
                .addHeader("Connection", "keep-alive")
                .addHeader("Sec-Fetch-Site", "same-origin")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Referer", "https://stimulus.pln.co.id/")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Cookie", "_ga=GA1.3.1439579538.1597333509; _gid=GA1.3.1006617863.1597333509; SRVNAME=g.242.11; " + mCookie!!)
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
                        var data = JSONObject(response)
                        data = data.getJSONObject("mapReturn")
                        if (data.has("OUT_MESSAGE") && !data.isNull("OUT_MESSAGE")) {
                            result.postValue(data.getString("OUT_MESSAGE"))
                            return
                        }
                        data = data.getJSONArray("OUT_DATA").getJSONObject(0)

                        val table = data.getString("KETERANGAN")

                        var sc = table.indexOf("<tr")
                        sc = table.indexOf("<tr", sc + 1)
                        var rows = ArrayList<ArrayList<String>>()
                        while (sc != -1) {
                            var ec = table.indexOf("</tr", sc)
                            var tds = table.subSequence(sc, ec)
                            var td_sc=0
                            var td_ec=0
                            var items = ArrayList<String>()
                            for (i in 0 until 8 ) {
                                td_sc = tds.indexOf("<b>", td_sc) + 3
                                td_ec = tds.indexOf("</b>", td_sc)

                                if (i == 0) {//Bulan
                                    items.add(tds.substring(td_sc, td_ec))
                                }
                                else if (i == 2) {// Rp
                                    items.add(tds.substring(td_sc, td_ec))
                                }
                                else if (i == 6) {// KWH
                                    items.add(tds.substring(td_sc, td_ec))
                                }
                                else if (i == 7) {// Token
                                    items.add(tds.substring(td_sc, td_ec))
                                }
                                td_sc++
                            }
                            rows.add(items)
                            sc = table.indexOf("<tr", sc + 1)
                        }

                        var desc = "NAMA : " + data["NAMA"]
                        for (row in rows) {
                            desc += "\n\n"
                            desc += "+++++++++++++"
                            desc += "\nBulan: " + row[0]
                            desc += "\nDiskon: Rp " + row[1] + " / " + row[2] + " Kwh"
                            desc += "\nTOKEN: " + row[3]
                        }
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
