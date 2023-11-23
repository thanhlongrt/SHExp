package com.example.salehub.webview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentWebviewBinding

class WebViewFragment : BaseViewBindingFragment<FragmentWebviewBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentWebviewBinding? {
        return FragmentWebviewBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding?.webView?.run {
            loadUrl("file:///android_asset/web1.html")
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(web: WebView, url: String?) {
                    // TODO Auto-generated method stub
                    val uname = "email@mail.com"
                    val pass = "******"
                    /*
             * web.loadUrl(
             * "javascript:(function(){document.getElementById('email').value='"
             * + uname +
             * "';document.getElementById('pass').value='" +
             * pass + "';})()");
             */
                    val link1 = "https://www.gstatic.com/webp/gallery3/1.png"
                    val link2 = "https://www.gstatic.com/webp/gallery3/2.png"
//                    web.loadUrl("javascript:(function(){document.body.innerHTML = document.body.innerHTML.replace('$link1', '$link2')})()")
                    web.loadUrl("javascript:(function(){document.getElementById(\"dm5kode\").src = \"$link2\"})()")
                    web.evaluateJavascript("document.getElementById(\"dm5kode\").src = \"$link2\"") {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
            }
            settings.run {
                javaScriptEnabled = true
                domStorageEnabled = true
                setAppCacheEnabled(true)
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }
}
