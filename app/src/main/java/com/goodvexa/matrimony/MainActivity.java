package com.goodvexa.matrimony;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.ValueCallback;
import android.net.Uri;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainActivity extends Activity {

    private WebView webView;
    private View splashView;

    // WebView HTML <input type="file"> support
    private static final int FILE_CHOOSER_REQUEST_CODE = 1001;
    private ValueCallback<Uri[]> filePathCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isInternetAvailable()) {
            setupAppLayout();
        } else {
            showOfflineScreen();
        }
    }

    private View createSplashScreen() {
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
        return layout;
    }

    private void setupAppLayout() {
        // Main Container (FrameLayout layer-ஆக செயல்படும்)
        android.widget.FrameLayout rootLayout = new android.widget.FrameLayout(this);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 1. WebView உருவாக்கம்
        webView = new WebView(this);
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

        // பக்கவாட்டு நகர்வைத் தடுக்கும் அமைப்புகள்
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        webView.setHorizontalScrollBarEnabled(false);

        // Allow HTML file inputs (profile photo, horoscope, full photos) to open Android's picker.
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView webView,
                    ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {

                // Close any previous pending chooser request safely.
                if (MainActivity.this.filePathCallback != null) {
                    MainActivity.this.filePathCallback.onReceiveValue(null);
                }
                MainActivity.this.filePathCallback = filePathCallback;

                Intent chooserIntent;
                try {
                    chooserIntent = fileChooserParams.createIntent();
                } catch (Exception e) {
                    chooserIntent = null;
                }

                if (chooserIntent == null) {
                    chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooserIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    chooserIntent.setType("*/*");
                }

                try {
                    startActivityForResult(chooserIntent, FILE_CHOOSER_REQUEST_CODE);
                    return true;
                } catch (ActivityNotFoundException | SecurityException e) {
                    MainActivity.this.filePathCallback = null;
                    filePathCallback.onReceiveValue(null);
                    android.widget.Toast.makeText(
                            MainActivity.this,
                            "File picker திறக்க முடியவில்லை. உங்கள் போனில் Files app உள்ளதா எனச் சரிபார்க்கவும்.",
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                    return false;
                }
            }
        });

        // 2. Splash Screen லேயர் உருவாக்கம்
        splashView = createSplashScreen();

        rootLayout.addView(webView);
        rootLayout.addView(splashView);

        setContentView(rootLayout);

        // WebView Client - வெப்சைட் லோட் ஆனதும் Splash Screen-ஐ மறைக்க
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // பக்கத்தின் லோடிங் முடிந்ததும் ஸ்பிளாஷ் ஸ்கிரீனை மறைத்தல்
                if (splashView != null) {
                    splashView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    showOfflineScreen();
                }
            }
        });

        // வெப்சைட் முகவரி லோடிங் தொடக்கம்
        webView.loadUrl("https://tn.goodvexa.com/");
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
                setupAppLayout();
            } else {
                android.widget.Toast.makeText(MainActivity.this, "Internet connection இல்லை", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        layout.addView(retryButton);
        setContentView(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (filePathCallback == null) {
                return;
            }

            Uri[] results = null;
            if (resultCode == RESULT_OK && data != null) {
                Uri result = data.getData();
                if (result != null) {
                    results = new Uri[] { result };
                } else if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    results = new Uri[count];
                    for (int i = 0; i < count; i++) {
                        results[i] = data.getClipData().getItemAt(i).getUri();
                    }
                }
            }

            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
