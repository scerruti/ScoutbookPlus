package com.otabi.scoutbooksecure;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    public static final String HTTPS_WWW_SCOUTBOOK_COM_MOBILE_DASHBOARD = "https://www.scoutbook.com/mobile/dashboard/";
    public static final List<String> VALID_PROTOCOLS = Arrays.asList("http", "https");
    public static final String SCOUTBOOK_COM = "scoutbook.com";
    private static final String TAG = "ScoutbookSecure";
    private WebView webView;
    private ProgressBar spinner;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar) findViewById(R.id.loadingSpinner);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new SimpleBrowser());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(HTTPS_WWW_SCOUTBOOK_COM_MOBILE_DASHBOARD);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private class SimpleBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String urlString) {
            Log.i(TAG, urlString);
            spinner.setVisibility(View.VISIBLE);
            URL url = null;

            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                return false;
            }
            String protocol = url.getProtocol();
            if (VALID_PROTOCOLS.contains(protocol)) {
                String host = url.getHost();
                if (host.endsWith(SCOUTBOOK_COM)) {
                    view.loadUrl(urlString);
                    return true;
                }
            } else {
                // Otherwise allow the OS to handle it
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                startActivity(intent);
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);

        }
    }
}
