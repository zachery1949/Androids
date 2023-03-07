package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;

public class WebviewMainActivity extends AppCompatActivity {

    private WebView mWebview;
    private Context mContext;
    final static String TAG = WebviewMainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_main);
        mWebview = findViewById(R.id.wv_wang);
        String path = "file:///android_asset/editor.html";
//        String path = "https://www.baidu.com/";
        initWebView(path);

//        File file = new File(path);
//        if(file.exists()){
//            Log.d(TAG, "onCreate: file exit");
//        }else {
//            Log.d(TAG, "onCreate: file not exit");
//        }
    }

    private void initWebView(String url) {

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);//启用js
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebview.setWebChromeClient(new WebChromeClient());
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("TAG", "shouldOverrideUrlLoading: "+url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
//        mWebview.addJavascriptInterface(new JsCallBack(new JsCallBack.OnYhdCallback() {
//
//            @Override
//            public void onFinish() {
//                ToastUtils.showLong(mContext,"js点击按钮");
//            }
//        }), "llvision");
        mWebview.addJavascriptInterface(new JsInterface(),"test");
        mWebview.loadUrl(url);
    }
    public class JsInterface {
        @SuppressLint("JavascriptInterface")
        @JavascriptInterface
        public void hello(String msg) {
            Log.d(TAG, "hello: "+msg);
            //Toast.makeText(WebviewMainActivity.this, msg, Toast.LENGTH_LONG).show();
        }
    }
}