package com.fxlibs.subsidy.tariff.core

import android.util.Log
import com.fxlibs.common.data.Error
import com.fxlibs.subsidy.tariff.api.TariffService
import com.fxlibs.subsidy.tariff.model.Area.*
import com.fxlibs.subsidy.tariff.model.SubsidyStatus
import org.json.JSONArray
import java.io.IOException

class TariffDataSource(private val api:TariffService) {

    fun getProvince():Result<List<Province>> {
        try {
            val body = "5|0|4|https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterProvinsi|1|2|3|4|0|"
            val response = api.getArea(body).execute()
            if (response.isSuccessful) {
                val resp = response.body()
                resp?.let(::toAreaMap)?.map { e -> Province(e.key, e.value) }?.let {
                    return Result.success(it.sortedBy { a -> a.name })
                }
            }
            return Result.failure(Error.ErrorData)
        } catch (e:IOException) {
            return Result.failure(Error.ErrorConnection)
        } catch (e:Exception) {
            Log.e(javaClass.simpleName, "", e)
            return Result.failure(e)
        }
    }

    fun getDistrict(provinceId:String):Result<List<District>> {
        try {
            val body = "5|0|6|https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterKabupatenByKdProv|java.lang.String/2004016611|$provinceId|1|2|3|4|1|5|6|"
            val response = api.getArea(body).execute()
            if (response.isSuccessful) {
                response.body()?.let(::toAreaMap)?.map { e -> District(e.key, e.value) }?.let {
                    return Result.success(it.sortedBy { a -> a.name })
                }
            }
            return Result.failure(Error.ErrorData)
        } catch (e:IOException) {
            return Result.failure(Error.ErrorConnection)
        } catch (e:Exception) {
            Log.e(javaClass.simpleName, "", e)
            return Result.failure(e)
        }
    }

    fun getSubDistrict(districtId:String):Result<List<SubDistrict>> {
        try {
            val body = "5|0|6|https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterKecamatanByKdKab|java.lang.String/2004016611|$districtId|1|2|3|4|1|5|6|"
            val response = api.getArea(body).execute()
            if (response.isSuccessful) {
                response.body()?.let(::toAreaMap)?.map { e -> SubDistrict(e.key, e.value) }?.let {
                    return Result.success(it.sortedBy { a -> a.name })
                }
            }
            return Result.failure(Error.ErrorData)
        } catch (e:IOException) {
            return Result.failure(Error.ErrorConnection)
        } catch (e:Exception) {
            Log.e(javaClass.simpleName, "", e)
            return Result.failure(e)
        }
    }
    fun getVillage(subDistrictId:String):Result<List<Village>> {
        try {
            val body = "5|0|6|https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|F70866662104DD987A6DE8B1B78CD0FF|id.co.iconpln.web.client.service.MasterService|getMasterDesaByKdKec|java.lang.String/2004016611|$subDistrictId|1|2|3|4|1|5|6|"
            val response = api.getArea(body).execute()
            if (response.isSuccessful) {
                response.body()?.let(::toAreaMap)?.map { e -> Village(e.key, e.value) }?.let {
                    return Result.success(it.sortedBy { a -> a.name })
                }
            }
            return Result.failure(Error.ErrorData)
        } catch (e:IOException) {
            return Result.failure(Error.ErrorConnection)
        } catch (e:Exception) {
            Log.e(javaClass.simpleName, "", e)
            return Result.failure(e)
        }
    }
    fun getStatus(customerId:String, villageId:String):Result<SubsidyStatus> {
        try {
            val session = api.getSession().execute()
            if (!session.isSuccessful) {
                return Result.failure(Error.ErrorData)
            }

            val body = "5|0|12|https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/|AB6BB8F2A9B546B9DE2B259C9242D117|id.co.iconpln.web.client.service.TransService|getValidasiTarifDaya|D|java.lang.String/2004016611|R||52201|PASANG BARU|$customerId|$villageId|1|2|3|4|9|5|6|5|6|6|6|6|5|6|8|7|450|8|9|10|11|0|12|"
            val response = api.getStatus(body).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    val flag = it.indexOf("out_message")
                    val sc   = it.indexOf(",", flag)
                    val ec   = it.indexOf(",", sc + 1)
                    return Result.success(SubsidyStatus(customerId, it.substring(sc + 2, ec -1).replace("Apakah Anda Setuju ?", "")))
                }
            }
            return Result.failure(Error.ErrorData)
        } catch (e:IOException) {
            return Result.failure(Error.ErrorConnection)
        } catch (e:Exception) {
            Log.e(javaClass.simpleName, "", e)
            return Result.failure(e)
        }
    }

    private fun toAreaMap (body : String) : HashMap<String, String> {
        val flag = body.indexOf("java.lang.String/2004016611")
        val sc   = body.indexOf(",", flag)
        val ec   = body.indexOf("]", sc)
        val array = JSONArray("[" + body.substring(sc + 1, ec) + "]")

        val map   = HashMap<String, String>()
            map[array.getString(0)] = array.getString(2)

        var id   = ""
        for (x in 3 until array.length()) {
            array.getString(x).let {
                if (it.matches(Regex("[0-9]+"))) {
                    id = it
                }
                else {
                    map[id] = it
                }
            }
        }
        return map
    }
}