package za.co.brightwave.proclean

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var webView: WebView

    @Suppress("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(9, 44, 54)
        window.navigationBarColor = Color.rgb(6, 31, 39)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        webView = WebView(this)
        webView.setBackgroundColor(Color.rgb(244, 247, 245))
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = false
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                if (request.isForMainFrame) showLoadError()
            }
        }
        setContentView(webView)

        try {
            val html = assets.open("index.html").bufferedReader(Charsets.UTF_8).use { it.readText() }
            // A secure synthetic origin allows WebView storage APIs to work reliably on Android.
            webView.loadDataWithBaseURL(
                "https://appassets.androidplatform.net/",
                html,
                "text/html",
                "UTF-8",
                null
            )
        } catch (_: Exception) {
            showLoadError()
        }
    }

    private fun showLoadError() {
        val message = TextView(this).apply {
            text = "Brightwave Proclean could not load. Please close the app and try again."
            textSize = 17f
            setTextColor(Color.rgb(9, 44, 54))
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
        runOnUiThread { setContentView(message) }
    }

    @Deprecated("Deprecated by Android")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
