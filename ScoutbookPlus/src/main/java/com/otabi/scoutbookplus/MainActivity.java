package com.otabi.scoutbookplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import intentbuilder.IntentBuilder;

public class MainActivity extends Activity {
    private static final String HTTPS_WWW_SCOUTBOOK_COM_MOBILE_DASHBOARD = "https://www.scoutbook.com/mobile/dashboard/";
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String MAILTO = "mailto";
    private static final String SCOUTBOOK_COM = "scoutbook.com";
    private static final String TAG = "ScoutbookPlus";

    private WebView webView;
    private LinearLayout loadingView;
    private View decorView;
    private int animationDuration;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_main);
        // Retrieve and cache the system's default "short" animation time.
        animationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        loadingView = (LinearLayout) findViewById(R.id.loading);
        loadingView.setVisibility(View.VISIBLE);
        AndroidBug5497Workaround.assistActivity(this);

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
            if (urlString == null) {
                Log.e(TAG, "null urlString");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Internal Error")
                        .setMessage("Something funny happened.")
                        .setNeutralButton(R.string.error_dialog_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
            }
            Log.i(TAG, urlString);
            Uri uri;

            uri = Uri.parse(urlString).normalizeScheme();
            if (uri.isRelative()) {
                uri = uri.buildUpon().scheme(HTTPS).authority(SCOUTBOOK_COM).build();
            }
            Intent intent = null;
            switch (uri.getScheme()) {
                case HTTP:
                case HTTPS:
                    String host = uri.getHost();
                    if (null == host || !host.endsWith(SCOUTBOOK_COM)) {
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                    break;
                case MAILTO:
                    if (MailTo.isMailTo(urlString)) {
                        MailTo mailTo = MailTo.parse(urlString);
                        intent = new IntentBuilder()
                                .action(Intent.ACTION_SEND)
                                .extra(Intent.EXTRA_EMAIL, defaultString(mailTo.getTo()).split(","))
                                .extra(Intent.EXTRA_CC, defaultString(mailTo.getCc()).split(","))
                                .extra(Intent.EXTRA_SUBJECT, defaultString(mailTo.getSubject()))
                                .extra(Intent.EXTRA_TEXT, defaultString(mailTo.getBody()))
                                .type("message/rfc822")
                                .build();
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Improper Email Link")
                                .setMessage("This email link could not be parsed.")
                                .setNeutralButton(R.string.error_dialog_button,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create().show();
                    }
                    break;
                default:
                    // Otherwise allow the OS to handle it
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    break;
            }
            if (null != intent) {
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Security Restriction")
                            .setMessage("This device does not allow that action.")
                            .setNeutralButton(R.string.error_dialog_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                }
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (View.VISIBLE == loadingView.getVisibility()) {
                crossFade(webView, loadingView);
            }
        }

        protected String defaultString(String string) {
            if (null == string) {
                return "";
            } else {
                return string;
            }
        }

    }

    private void crossFade(final View viewIn, final View viewOut) {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewIn.setAlpha(0f);
        viewIn.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        viewIn.animate()
                .alpha(1f)
                .setDuration(animationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        viewOut.animate()
                .alpha(0f)
                .setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewOut.setVisibility(View.GONE);
                    }
                });
    }
}
