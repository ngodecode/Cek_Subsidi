package com.ftools.ceksubsidi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.cek_subsidi_fragment.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class CekSubsidiFragment : Fragment() {


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cek_subsidi_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adView.loadAd(AdRequest.Builder().build())

        btnCek.setOnClickListener {
            submit()
            try {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
            } catch (e:Exception) {}
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

        mAdapterProvince  = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, mListProvince)
        mAdapterKabupaten = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, mListKabupaten)
        mAdapterKecamatan = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, mListKecamatan)
        mAdapterKelurahan = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, mListKelurahan)

        mAdapterProvince?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAdapterKabupaten?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAdapterKecamatan?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mAdapterKelurahan?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnProvince.adapter = mAdapterProvince
        spnKabupaten.adapter = mAdapterKabupaten
        spnKecamatan.adapter = mAdapterKecamatan
        spnKelurahan.adapter = mAdapterKelurahan

        if (openDraft()) {

        }
        else {
            Handler().postDelayed({
                pullProvince()
            }, 100)
        }

        spnProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    if (spnProvince.tag == position) {
                        spnProvince.tag = null
                        return
                    }
                    spnProvince.tag = null
                    pullKabupaten()
                } catch (e:Exception){}
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
                try {
                    if (spnKabupaten.tag == position) {
                        spnKabupaten.tag = null
                        return
                    }
                    spnKabupaten.tag = null
                    pullKecamatan()
                } catch (e:Exception){}
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
                try {
                    if (spnKecamatan.tag == position) {
                        spnKecamatan.tag = null
                        return
                    }
                    spnKecamatan.tag = null
                    pullKelurahan()
                } catch (e:Exception){}
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun openDraft() : Boolean {
        try {
            val pref = activity!!.getSharedPreferences("SYS", Context.MODE_PRIVATE)
            if (pref.getString("KEL", null) != null) {

                val type: Type = object :
                    TypeToken<HashMap<String, String>?>() {}.type

                mMapProvince.putAll(Gson().fromJson(pref.getString("PROV_SRC", "")!!, type))
                mMapKabupaten.putAll(Gson().fromJson(pref.getString("KAB_SRC", "")!!, type))
                mMapKecamatan.putAll(Gson().fromJson(pref.getString("KEC_SRC", "")!!, type))
                mMapKelurahan.putAll(Gson().fromJson(pref.getString("KEL_SRC", "")!!, type))

                mListProvince .addAll(mMapProvince.keys)
                mListKabupaten.addAll(mMapKabupaten.keys)
                mListKecamatan.addAll(mMapKecamatan.keys)
                mListKelurahan.addAll(mMapKelurahan.keys)

                mListProvince .sort()
                mListKabupaten.sort()
                mListKecamatan.sort()
                mListKelurahan.sort()

                spnProvince .setSelection(mListProvince.indexOf(pref.getString("PROV", "")!!))
                spnKabupaten.setSelection(mListKabupaten.indexOf(pref.getString("KAB", "")!!))
                spnKecamatan.setSelection(mListKecamatan.indexOf(pref.getString("KEC", "")!!))
                spnKelurahan.setSelection(mListKelurahan.indexOf(pref.getString("KEL", "")!!))

                spnProvince .tag = spnProvince.selectedItemPosition
                spnKabupaten.tag = spnKabupaten.selectedItemPosition
                spnKecamatan.tag = spnKecamatan.selectedItemPosition

                mAdapterProvince?.notifyDataSetChanged()
                mAdapterKabupaten?.notifyDataSetChanged()
                mAdapterKecamatan?.notifyDataSetChanged()
                mAdapterKelurahan?.notifyDataSetChanged()

                return true
            }
        } catch (e:Exception) {Log.e(mTag, e.message, e)}
        return false
    }

    private fun saveDraft() {
        val pref = activity!!.getSharedPreferences("SYS", Context.MODE_PRIVATE)
        pref.edit()
            .putString("PROV", mListProvince[spnProvince.selectedItemPosition])
            .putString("KAB",  mListKabupaten[spnKabupaten.selectedItemPosition])
            .putString("KEC",  mListKecamatan[spnKecamatan.selectedItemPosition])
            .putString("KEL",  mListKelurahan[spnKelurahan.selectedItemPosition])
            .putString("PROV_SRC",  Gson().toJson(mMapProvince))
            .putString("KAB_SRC",   Gson().toJson(mMapKabupaten))
            .putString("KEC_SRC",   Gson().toJson(mMapKecamatan))
            .putString("KEL_SRC",   Gson().toJson(mMapKelurahan))
            .commit()
    }

    var mDialogLoading : Dialog? = null
    fun isDialogShowing() : Boolean {
        return mDialogLoading?.isShowing ?: false
    }

    private fun showDialog() {
        if (isDialogShowing()) {
            return
        }
        mDialogLoading = setProgressDialog(activity!!, "Memuat Informasi")
        mDialogLoading?.setCanceledOnTouchOutside(false)
        mDialogLoading?.setCancelable(true)
        mDialogLoading?.show()
    }

    fun setProgressDialog(context:Context, message:String):AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER_VERTICAL
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)
        builder.setNegativeButton("BATAL", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dismissDialog()
            }
        })

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

    fun dismissDialog() {
        mDialogLoading?.dismiss()
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
        map.put("..Pilih..", "-99");
        return map
    }

    fun pullProvince() {
        if (!isDialogShowing()) {
            showDialog()
        }
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
                        if (isDialogShowing()) {
                            Handler().postDelayed({pullProvince()}, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showInfo("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                    dismissDialog()
                }
            })
    }

    fun pullKabupaten() {
        txtResult.text = ""
        val areaId = mMapProvince[mListProvince[spnProvince.selectedItemPosition]]
        if (areaId == "-99" || areaId == null) {
            dismissDialog()
            return
        }
        else {
            showDialog()
        }
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
                        if (isDialogShowing()) {
                            Handler().postDelayed({pullKabupaten()}, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showInfo("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                    dismissDialog()
                }
            })
    }

    fun pullKecamatan() {
        txtResult.text = ""
        val areaId = mMapKabupaten[mListKabupaten[spnKabupaten.selectedItemPosition]]
        if (areaId == "-99" || areaId == null) {
            dismissDialog()
            return
        }
        else {
            showDialog()
        }
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
                        if (isDialogShowing()) {
                            Handler().postDelayed({pullKecamatan()}, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showInfo("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                    dismissDialog()
                }
            })
    }

    fun pullKelurahan() {
        txtResult.text = ""
        val areaId = mMapKecamatan[mListKecamatan[spnKecamatan.selectedItemPosition]]
        if (areaId == "-99" || areaId == null) {
            dismissDialog()
            return
        }
        else {
            showDialog()
        }
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
                        dismissDialog()
                    } catch (e:Exception) {
                        Log.e(mTag, e.message, e)
                        if (isDialogShowing()) {
                            Handler().postDelayed({pullKelurahan()}, 1000)
                        }
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showInfo("Perangkat bermasalah, Mohon Periksa internet Anda lalu ulangi")
                }
            })
    }


    fun submit() {
        var areaId:String? = null
        try {
            areaId = mMapKelurahan[mListKelurahan[spnKelurahan.selectedItemPosition]]
        } catch (e:Exception){}
        if (areaId == "-99" || areaId == null) {
            Toast.makeText(activity!!, "Lengkapi Lokasi pemasangan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (edtKTP.text.isEmpty()) {
            Toast.makeText(activity!!, "Mohon isi KTP terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        saveDraft()
        showInfo("")

        showDialog()
        getAPIService().getSession()
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    mCookie = response?.headers()?.get("Set-Cookie")
                    if (mCookie != null) {
                        getInfo()
                    }
                    else {
                        if (isDialogShowing()) {
                            Handler().postDelayed({submit()}, 1000)
                        }
                        else {
                            showInfo("Koneksi Bermasalah\nMohon Periksa Internet Anda")
                        }
                    }
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e(mTag, t?.message, t)
                    showInfo(t?.message + "\nSilahkan coba lagi nanti")
                    dismissDialog()
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
                    showInfo(response?.body()?.substring(sc!! + 2, ec!! -1) ?: "")

                    activity
                        ?.getSharedPreferences("SYS", Context.MODE_PRIVATE)
                        ?.edit()?.putBoolean("ADS_PERIODIC_ENABLED", true)?.commit()

                    HomeActivity.adsRewads.postValue(true)
                } catch (e : Exception) {
                    Log.e(mTag, e?.message, e)
                    showInfo("Koneksi bermasalah\nSilahkan coba lagi")
                }
                dismissDialog()
            }

            override fun onFailure(call: Call<String>?, t: Throwable?) {
                Log.e(mTag, t?.message, t)
                showInfo(t?.message + "\nSilahkan coba lagi nanti")
                dismissDialog()
            }
        })
    }


    fun clear() {
        pullProvince()
        scrollView.fullScroll(View.FOCUS_UP)
    }

    fun showInfo(message:String) {
        txtResult.text = message
        scrollView.fullScroll(View.FOCUS_DOWN)
    }
}
