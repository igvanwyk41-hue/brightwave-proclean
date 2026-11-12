package net.cubot.mobile

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.widget.FrameLayout
import android.widget.ProgressBar

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var progress: ProgressBar

    private val englishPatch = """
      (function(){
        const replacements = [
          ['首页','Home'],['产品','Products'],['支持','Support'],['关于我们','About Us'],
          ['联系我们','Contact Us'],['手机','Phones'],['平板电脑','Tablets'],['智能手表','Smart Watches'],
          ['耳机','Earphones'],['了解更多','Learn More'],['查看更多','View More'],
          ['加入购物车','Add to Cart'],['立即购买','Buy Now'],['搜索','Search'],
          ['新闻','News'],['博客','Blog'],['下载中心','Download Center'],
          ['售后服务','After-sales Service'],['语言','Language'],['提交','Submit']
        ];
        function translate(){
          document.documentElement.lang='en';
          const walker=document.createTreeWalker(document.body,NodeFilter.SHOW_TEXT);
          let node;
          while(node=walker.nextNode()){
            let value=node.nodeValue;
            for(const pair of replacements) value=value.split(pair[0]).join(pair[1]);
            if(value!==node.nodeValue) node.nodeValue=value;
          }
          document.querySelectorAll('input,button').forEach(el=>{
            const attr=el.getAttribute('placeholder');
            if(attr) for(const pair of replacements) el.setAttribute('placeholder',attr.split(pair[0]).join(pair[1]));
          });
        }
        translate();
        new MutationObserver(translate).observe(document.documentElement,{childList:true,subtree:true,characterData:true});
      })();
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        val root = FrameLayout(this)
        web = WebView(this)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        root.addView(web, FrameLayout.LayoutParams(-1, -1))
        root.addView(progress, FrameLayout.LayoutParams(-1, 7))
        setContentView(root)

        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.loadsImagesAutomatically = true
        web.settings.useWideViewPort = true
        web.settings.loadWithOverviewMode = true
        web.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progress.progress = newProgress
                progress.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }
        }
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                web.evaluateJavascript(englishPatch, null)
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }
        }
        if (savedInstanceState == null) web.loadUrl("https://cubot.net/")
        else web.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        web.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }
}
