package com.example.taiga.cleapp;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    CoordinatorLayout coordinatorLayout;

    boolean kill=false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences data = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        String userId = data.getString("userId", null);
        String userPass = data.getString("userPass", null);

        if (userId != null && userPass != null) {

            coordinatorLayout = (CoordinatorLayout)findViewById(R.id.first_coordinator_layout);

            webView = (WebView) findViewById(R.id.web_v);
            webView.setWebViewClient(new MyWVClient(userId, userPass));
            webView.loadUrl("https://cleweb.tsc.u-tokai.ac.jp/campusweb/top.do");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    String[] values = contentDisposition.split("filename=");
                    String filename = values[1].replaceAll("\"", "");

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    String host = URI.create(url).getHost();

                    //String cookies = CookieManager.getInstance().getCookie(host);
                    request.allowScanningByMediaScanner();
                    request.addRequestHeader("Cookie", "CLE");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    request.setTitle(filename);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    if (dm != null) {
                        dm.enqueue(request);
                    } else {
                        //失敗？
                        Log.d("SSS", "aa");
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(!kill) {
                kill = true;
                Snackbar.make(coordinatorLayout,"もう一度押すと終了します", Snackbar.LENGTH_LONG).show();
                return false;
            }

            return super.onKeyDown(keyCode, event);
        } else {
            kill=false;
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setting(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("EditMode", true);
        startActivity(intent);
    }
}

class MyWVClient extends WebViewClient {
    private static final String LOGIN_URL = "https://cleweb.tsc.u-tokai.ac.jp/campusweb/top.do";
    private String id = null;
    private String pass = null;

    public MyWVClient(String id, String pass) {
        this.id = id;
        this.pass = pass;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (url.startsWith(LOGIN_URL)) {
            view.loadUrl(String.format(
                    "javascript:document.getElementById('userId').value='" + this.id + "';" +
                            "document.getElementById('password').value='" + this.pass + "';" +
                            "javascript:document.getElementById('loginButton').click();"));
            //
            // view.loadUrl(String.format(""));
        }
    }
}
