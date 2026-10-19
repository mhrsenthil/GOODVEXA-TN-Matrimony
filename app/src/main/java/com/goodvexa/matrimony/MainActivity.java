package com.goodvexa.matrimony;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // தொடக்கத்தில் ஸ்பிளாஷ் ஸ்கிரீன் காட்டுதல்
        showSplashScreen();

        // 2.5 விநாடிகள் கழித்து இன்டர்நெட் உள்ளதா எனச் சோதித்து வெப்சைட் அல்லது ஆஃப்லைன் ஸ்கிரீன் காட்டுவது
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isInternetAvailable()) {
                showWebView();
            } else {
                showOfflineScreen();
            }
        }, 2500);
    }

    private void showSplashScreen() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.WHITE);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.app_icon);
        logo.setAdjustViewBounds(true);

        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(220, 220);
        layout.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText("Goodvexa Matrimony");
        title.setTextSize(22);
        title.setTextColor(Color.rgb(190, 18, 60)); // Rose/Brand color
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 20, 10, 10);

        layout.addView(title);
        setContentView(layout);
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showOfflineScreen() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 30, 40, 30);
        layout.setBackgroundColor(Color.WHITE);

        TextView icon = new TextView(this);
        icon.setText("📡");
        icon.setTextSize(60);
        icon.setGravity(Gravity.CENTER);
        layout.addView(icon);

        TextView title = new TextView(this);
        title.setText("No Internet Connection");
        title.setTextSize(24);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.rgb(30, 30, 30));
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 15, 10, 10);
        layout.addView(title);

        TextView message = new TextView(this);
        message.setText("இந்த app-ஐ பயன்படுத்த Internet connection தேவை.\n\nWi-Fi அல்லது Mobile Data-ஐ ON செய்து மீண்டும் முயற்சி செய்யுங்கள்.");
        message.setTextSize(17);
        message.setTextColor(Color.DKGRAY);
        message.setGravity(Gravity.CENTER);
        message.setPadding(10, 10, 10, 25);
        layout.addView(message);

        android.widget.Button retryButton = new android.widget.Button(this);
        retryButton.setText("🔄 Retry");
        retryButton.setTextSize(16);

        retryButton.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                showWebView();
            } else {
                android.widget.Toast.makeText(MainActivity.this, "Internet connection இல்லை", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        layout.addView(retryButton);
        setContentView(layout);
    }

    private void showWebView() {
        webView = new WebView(this);
        
        // WebView முழுத்திரையில் சரியாகத் தெரிய LayoutParams அமைத்தல்
        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // **பக்கவாட்டு ஸ்கரோலைத் தவிர்க்கும் வியூபோர்ட் செட்டிங்ஸ்**
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    showOfflineScreen();
                }
            }
        });

        // உங்கள் மேட்ரிமோனி வெப்சைட் முகவரி
        webView.loadUrl("https://tn.goodvexa.com/");

        setContentView(webView);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
