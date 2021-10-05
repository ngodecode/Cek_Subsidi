package com.fxlibs.subsidy.tariff.ui

import android.app.Activity
import android.content.Intent
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class WebClient(val activity:Activity, val onLoaded:() -> Unit) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        onLoaded.invoke()
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.url?.let {
            activity.startActivity(Intent(Intent.ACTION_VIEW, it))
            return true
        }
        return true
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
    }

}