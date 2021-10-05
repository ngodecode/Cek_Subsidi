package com.fxlibs.app.subsidy.module.subsidy

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import com.fxlibs.app.subsidy.BuildConfig
import com.fxlibs.subsidy.tariff.api.TariffService
import com.fxlibs.subsidy.tariff.core.TariffDataSource
import com.fxlibs.subsidy.tariff.core.TariffRepository
import com.fxlibs.subsidy.tariff.core.TariffViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

object SubsidyModule {

    private fun provideSessionInterceptor() = object : Interceptor {
        var mCookie:String? = null
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("X-GWT-Module-Base", "https://pelanggan.pln.co.id/id.co.iconpln.web.PBMohonEntryPoint/")
                .addHeader("X-GWT-Permutation", "A1630C2E2A3C73EFF36ABD53F8C9A151")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36")
                .addHeader("Content-Type", "text/x-gwt-rpc; charset=UTF-8")
                .addHeader("Accept", "*/*")
                .addHeader("Origin", "https://pelanggan.pln.co.id")

            mCookie?.let {
                request.addHeader("Cookie", it)
            }

            return chain.proceed(request.build()).apply {
                mCookie  = headers["Set-Cookie"]
            }
        }

    }
    private fun provideClient() : OkHttpClient {
        val builder = OkHttpClient.Builder()
        val trustAllCerts = @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
        val sslContext: SSLContext = SSLContext.getInstance("SSL").apply {
            init(null, arrayOf(trustAllCerts), SecureRandom())
        }
        builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts)
        builder.addInterceptor(provideSessionInterceptor())
        builder.addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        return builder.build()
    }

    private fun provideConverterFactory() = object : Converter.Factory() {

        override fun responseBodyConverter (
            type: Type?,
            annotations: Array<out Annotation>?,
            retrofit: Retrofit?
        ): Converter<ResponseBody, *> {
            return Converter<ResponseBody, String> { response ->response.string() }
        }

        override fun requestBodyConverter(
            type: Type?,
            parameterAnnotations: Array<out Annotation>?,
            methodAnnotations: Array<out Annotation>?,
            retrofit: Retrofit?
        ): Converter<*, RequestBody> {
            return Converter<String, RequestBody> { value -> value.toRequestBody("text/x-gwt-rpc; charset=utf-8".toMediaType())}
        }
    }

    private fun <T> provideApiService(clazz:Class<T>) : T {
        return Retrofit.Builder()
            .client(provideClient())
            .baseUrl(BuildConfig.SUBSIDY_URL)
            .addConverterFactory(provideConverterFactory())
            .build()
            .create(clazz)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    val module = module {
        single { provideApiService(TariffService::class.java)}
        factory { TariffDataSource(get()) }
        factory { TariffRepository(get()) }
        factory { SavedStateHandle() }
        viewModel { TariffViewModel(get(), get()) }
    }

}