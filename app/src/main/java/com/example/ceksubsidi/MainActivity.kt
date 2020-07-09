package com.example.ceksubsidi

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    val mTag = "MainActivity"
    var mCookie : String? = null
    var mClient : OkHttpClient? = null

    var mAdapterProvince : ArrayAdapter<String>? = null
    var mAdapterKabupaten : ArrayAdapter<String>? = null
    var mAdapterKecamatan : ArrayAdapter<String>? = null
    var mAdapterKelurahan : ArrayAdapter<String>? = null
    val mListProvince  : ArrayList<String> = ArrayList()
    val mListKabupaten : ArrayList<String> = ArrayList()
    val mListKecamatan : ArrayList<String> = ArrayList()
    val mListKelurahan : ArrayList<String> = ArrayList()
    val mMapProvince : HashMap<String, String> = HashMap()
    val mMapKabupaten : HashMap<String, String> = HashMap()
    val mMapKecamatan : HashMap<String, String> = HashMap()
    val mMapKelurahan : HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCek.setOnClickListener {
            submit()
        }

        btnClear.setOnClickListener {
            clear()
        }
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
                .addHeader("X-GWT-Module-Base", "https://layanan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/")
                .addHeader("X-GWT-Permutation", "A1630C2E2A3C73EFF36ABD53F8C9A151")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .addHeader("Content-Type", "text/x-gwt-rpc; charset=UTF-8")
                .addHeader("Accept", "*/*")
                .addHeader("Origin", "https://layanan.pln.co.id")
                .build()
        )
        }.build()

        spnProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Handler().postDelayed({
                    try {
                        pullKabupaten()
                    } catch (e:Exception){}
                }, 100)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spnKabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Handler().postDelayed({
                    try {
                        pullKecamatan()
                    } catch (e:Exception){}
                }, 100)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        spnKecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Handler().postDelayed({
                    try {
                        pullKelurahan()
                    } catch (e:Exception){}
                }, 100)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        mAdapterProvince  = ArrayAdapter(this, android.R.layout.simple_spinner_item, mListProvince)
        mAdapterKabupaten = ArrayAdapter(this, android.R.layout.simple_spinner_item, mListKabupaten)
        mAdapterKecamatan = ArrayAdapter(this, android.R.layout.simple_spinner_item, mListKecamatan)
        mAdapterKelurahan = ArrayAdapter(this, android.R.layout.simple_spinner_item, mListKelurahan)

        mAdapterProvince?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAdapterKabupaten?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAdapterKecamatan?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAdapterKelurahan?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnProvince.adapter = mAdapterProvince
        spnKabupaten.adapter = mAdapterKabupaten
        spnKecamatan.adapter = mAdapterKecamatan
        spnKelurahan.adapter = mAdapterKelurahan

        Handler().postDelayed({
            pullProvince()
        }, 100)
    }

    fun getAPIService() : APIService {
        val retrofit = Retrofit.Builder().baseUrl("https://layanan.pln.co.id")
            .client(mClient!!)
            .addConverterFactory(ConverterFactory())
            .build()

        return retrofit.create(APIService ::class.java)
    }

    fun toAreaMap (body : String) : HashMap<String, String> {
        val flag = body.indexOf("java.lang.String/2004016611")
        val sc = body.indexOf(",", flag!!)
        val ec = body.indexOf("]", sc!!)
        val array = JSONArray("[" + body.substring(sc!! + 1, ec!!) + "]")
        Log.w(mTag, "ARRAY : " + array.length())
        val map = HashMap<String, String>()
        map.put(array.getString(2), array.getString(0))
        for (x in 3 until array.length() step 2) {
            map.put(array.getString(x+1), array.getString(x))
        }
        return map
    }

    fun pullProvince() {
        txtResult.text = ""
        mListProvince.clear()
        mListKabupaten.clear()
        mListKecamatan.clear()
        mListKelurahan.clear()
        mAdapterProvince?.notifyDataSetChanged()
        mAdapterKabupaten?.notifyDataSetChanged()
        mAdapterKecamatan?.notifyDataSetChanged()
        mAdapterKelurahan?.notifyDataSetChanged()

        getAPIService().getArea("5|0|4|https://layanan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterProvinsi|1|2|3|4|0|")
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    Log.w(mTag, response?.body() ?: "NULL")
                    try {
                        val area = toAreaMap(response?.body() ?: "")
                        mListProvince.addAll(area.keys)
                        mListProvince.sort()
                        mAdapterProvince?.notifyDataSetChanged()
                        mMapProvince.clear()
                        mMapProvince.putAll(area)
                    } catch (e:Exception) {
                        Log.e(mTag, e.message, e)
                        showError("Info Provinsi Gagal Dimuat, Mohon ulangi")
                        btnCek.visibility = View.GONE
                        btnClear.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showError("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                    btnCek.visibility = View.GONE
                    btnClear.visibility = View.VISIBLE
                }
            })
    }

    fun pullKabupaten() {
        txtResult.text = ""
        val areaId = mMapProvince[mListProvince[spnProvince.selectedItemPosition]]
        mListKabupaten.clear()
        mListKecamatan.clear()
        mListKelurahan.clear()
        mAdapterKabupaten?.notifyDataSetChanged()
        mAdapterKecamatan?.notifyDataSetChanged()
        mAdapterKelurahan?.notifyDataSetChanged()

        getAPIService().getArea("5|0|6|https://layanan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterKabupatenByKdProv|java.lang.String/2004016611|$areaId|1|2|3|4|1|5|6|")
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    Log.w(mTag, response?.body() ?: "NULL")
                    try {
                        val area = toAreaMap(response?.body() ?: "")
                        mListKabupaten.addAll(area.keys)
                        mListKabupaten.sort()
                        mAdapterKabupaten?.notifyDataSetChanged()
                        mMapKabupaten.clear()
                        mMapKabupaten.putAll(area)
                    } catch (e:Exception) {
                        Log.e(mTag, e.message, e)
                        showError("Info Kabupaten Gagal Dimuat, Mohon ulangi")
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showError("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                    btnCek.visibility = View.GONE
                    btnClear.visibility = View.VISIBLE
                }
            })
    }

    fun pullKecamatan() {
        txtResult.text = ""
        val areaId = mMapKabupaten[mListKabupaten[spnKabupaten.selectedItemPosition]]
        mListKecamatan.clear()
        mListKelurahan.clear()
        mAdapterKecamatan?.notifyDataSetChanged()
        mAdapterKelurahan?.notifyDataSetChanged()

        getAPIService().getArea("5|0|6|https://layanan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterKecamatanByKdKab|java.lang.String/2004016611|$areaId|1|2|3|4|1|5|6|")
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    Log.w(mTag, response?.body() ?: "NULL")
                    try {
                        val area = toAreaMap(response?.body() ?: "")
                        mListKecamatan.addAll(area.keys)
                        mListKecamatan.sort()
                        mAdapterKecamatan?.notifyDataSetChanged()
                        mMapKecamatan.clear()
                        mMapKecamatan.putAll(area)
                    } catch (e:Exception) {
                        Log.e(mTag, e.message, e)
                        showError("Info Kecamatan Gagal Dimuat, Mohon ulangi")
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showError("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                }
            })
    }

    fun pullKelurahan() {
        txtResult.text = ""
        val areaId = mMapKecamatan[mListKecamatan[spnKecamatan.selectedItemPosition]]
        mListKelurahan.clear()
        mAdapterKelurahan?.notifyDataSetChanged()

        getAPIService().getArea("5|0|6|https://layanan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterDesaByKdKec|java.lang.String/2004016611|$areaId|1|2|3|4|1|5|6|")
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    Log.w(mTag, response?.body() ?: "NULL")
                    try {
                        val area = toAreaMap(response?.body() ?: "")
                        mListKelurahan.addAll(area.keys)
                        mListKelurahan.sort()
                        mAdapterKelurahan?.notifyDataSetChanged()
                        mMapKelurahan.clear()
                        mMapKelurahan.putAll(area)
                    } catch (e:Exception) {
                        Log.e(mTag, e.message, e)
                        showError("Info Kelurahan Gagal Dimuat, Mohon ulangi")
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showError("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                }
            })
    }


    fun submit() {
        getAPIService().getSession()
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    mCookie = response?.headers()?.get("Set-Cookie")
                    if (mCookie != null) {
                        getInfo()
                    }
                    else {
                        showError("Koneksi Bermasalah\nMohon Periksa Internet Anda")
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showError(t?.message + "\nSilahkan coba lagi nanti")
                }

            })
    }

    fun getInfo() {
        val areaId = mMapKelurahan[mListKelurahan[spnKelurahan.selectedItemPosition]]
        val ktp    = edtKTP.text.trim()

        Log.w(mTag, "areaId=$areaId / ktp=$ktp")
        getAPIService().getStatus(mCookie!!,
            "5|0|12|https://layanan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|AB6BB8F2A9B546B9DE2B259C9242D117|id.co.iconpln.web.client.service.TransService|getValidasiTarifDaya|D|java.lang.String/2004016611|R||52201|PASANG BARU|$ktp|$areaId|1|2|3|4|9|5|6|5|6|6|6|6|5|6|8|7|450|8|9|10|11|0|12|"
        ).enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                Log.w(mTag, response?.body() ?: "NULL")
                try {
                    val flag = response?.body()?.indexOf("out_message")
                    val sc   = response?.body()?.indexOf(",", flag!!)
                    val ec   = response?.body()?.indexOf(",", sc!! + 1)
                    Log.w(mTag, "flag:$flag sc:$sc ec:$ec")
                    txtResult.text = response?.body()?.substring(sc!!, ec!!)
                } catch (e : Exception) {
                    Log.e(mTag, e?.message, e)
                    showError("Koneksi bermasalah\nSilahkan coba lagi")
                }
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
            }
        })
    }


    fun clear() {
        pullProvince()
    }

    fun showError(message:String) {
        txtResult.text = message
    }
}



