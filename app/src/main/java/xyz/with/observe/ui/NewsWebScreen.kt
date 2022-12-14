package xyz.with.observe.ui

import android.annotation.SuppressLint
import android.net.http.SslError
import android.view.View
import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.blankj.utilcode.util.LogUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import xyz.with.observe.theme.statusBarColor
import xyz.with.observe.viewmodel.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsView(mainViewModel: MainViewModel) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setStatusBarColor(statusBarColor, false)
        systemUiController.setSystemBarsColor(statusBarColor, false)
    })
    val context = LocalContext.current
    var webTitle by remember {
        mutableStateOf("")
    }

    val webView = WebView(context)

    val url by mainViewModel.newsUrl.collectAsState()
    webView.loadUrl(url)
    mainViewModel.loadJs()
    val js by mainViewModel.js.collectAsState()
    val jsLoading by mainViewModel.jsLoading.collectAsState()
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), backgroundColor = statusBarColor) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = webTitle, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }) {
        if (jsLoading) {
            AndroidView(
                factory = {
                    val webViewSetting = webView.settings
                    webViewSetting.apply {
                        javaScriptEnabled = true
                        javaScriptCanOpenWindowsAutomatically = false
                        cacheMode = WebSettings.LOAD_NO_CACHE
//                        useWideViewPort = true
                        loadWithOverviewMode = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        userAgentString =
                            "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H)"
                    }
                    val webChromeClient = object : WebChromeClient() {
                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            super.onShowCustomView(view, callback)
                        }
                        override fun onReceivedTitle(view: WebView?, title: String) {
                            super.onReceivedTitle(view, title)
                            webTitle = title
                        }

                        override fun onProgressChanged(view: WebView, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
//                            if (newProgress == 100) {
//                                view.loadUrl("javascript: $js")
//                            }
                        }
                    }
                    val webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String?) {
                            super.onPageFinished(view, url)
                            view.evaluateJavascript("javascript: $js", null)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {
                            view.loadUrl(request.url.toString())
                            return true
                        }

                        @SuppressLint("WebViewClientOnReceivedSslError")
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler,
                            error: SslError?
                        ) {
                            handler.proceed()
                        }
                    }


                    webView.webViewClient = webViewClient
                    webView.webChromeClient = webChromeClient

                    return@AndroidView webView
                }, modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = "?????????...")
            }
        }
    }
}