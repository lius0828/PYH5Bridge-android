package com.pycredit.h5bridge;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.pycredit.h5sdk.H5SDKHelper;
import com.pycredit.h5sdk.capture.CustomCaptureImpl;
import com.pycredit.h5sdk.impl.BannerCallback;

public class DemoActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView tvClose;
    private H5SDKHelper h5SDKHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mWebView = (WebView) findViewById(R.id.wv_content);
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (h5SDKHelper.shouldOverrideUrlLoading(view, url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            //Android 5.0 以下 必须重写此方法
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                h5SDKHelper.openFileChooser(uploadFile, acceptType, capture);
            }

            //Android 5.0 以上 必须重写此方法
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (h5SDKHelper.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
                    return true;
                }
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                h5SDKHelper.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });
        h5SDKHelper = new H5SDKHelper(this, mWebView);
        //如果需要在H5页面插入广告，使用如下方式，传入广告图片地址及回调
        //请使用 https:// 协议的网址，否则可能导致协议错误无法加载
        h5SDKHelper.setBanner("https://apk-txxy.oss-cn-shenzhen.aliyuncs.com/test_ad.png", new BannerCallback() {
            @Override
            public void onBannerClick() {
                Toast.makeText(getApplicationContext(), "正常广告被点击了", Toast.LENGTH_SHORT).show();
            }
        });
//        h5SDKHelper.setCapture(new SystemCaptureImpl());
        h5SDKHelper.setCapture(new CustomCaptureImpl());
        mWebView.loadUrl("**使用鹏元提供的渠道**");
    }

    @Override
    protected void onResume() {
        super.onResume();
        h5SDKHelper.onResume();
    }

    @Override
    protected void onPause() {
        h5SDKHelper.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        h5SDKHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (h5SDKHelper.onBackPressed()) {
            tvClose.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();
    }
}