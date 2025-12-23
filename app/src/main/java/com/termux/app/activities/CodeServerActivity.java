package com.termux.app.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.termux.R;
import com.termux.app.utils.CodeServerManager;
import com.termux.shared.logger.Logger;

/**
 * Activity to display code-server in a WebView
 */
public class CodeServerActivity extends AppCompatActivity {

    private static final String LOG_TAG = "CodeServerActivity";
    
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private CodeServerManager mCodeServerManager;
    private String mCodeServerUrl = "http://127.0.0.1:8080";
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make fullscreen - hide system UI
        enableFullscreen();
        
        setContentView(R.layout.activity_code_server);
        
        // Hide action bar for fullscreen experience
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        
        mWebView = findViewById(R.id.code_server_webview);
        mProgressBar = findViewById(R.id.code_server_progress);
        
        // Initialize code-server manager
        mCodeServerManager = new CodeServerManager(this);
        
        // Setup WebView
        setupWebView();
        
    }
    
    /**
     * Enable fullscreen mode - hide system bars
     */
    private void enableFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    
    // Retry logic variables
    private static final int MAX_RETRY_COUNT = 10;
    private static final int RETRY_DELAY_MS = 5000;
    private int mRetryCount = 0;
    private boolean mIsLoading = false;

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // Set cache mode for better performance
        // Note: setAppCacheEnabled() was deprecated in API 26 and removed in API 33
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                mIsLoading = false;
                mRetryCount = 0; // Reset retry count on success
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Logger.logError(LOG_TAG, "WebView error: " + description);
                Toast.makeText(CodeServerActivity.this,
                        "Error loading code-server: " + description,
                        Toast.LENGTH_LONG).show();
                attemptRetry();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Initial load
        loadCodeServerUrl();
    }

    private void loadCodeServerUrl() {
        if (!mIsLoading) {
            mIsLoading = true;
            mWebView.loadUrl(mCodeServerUrl);
        }
    }

    private void attemptRetry() {
        if (mRetryCount < MAX_RETRY_COUNT) {
            mRetryCount++;
            mProgressBar.setVisibility(View.VISIBLE);
            mWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Logger.logInfo(LOG_TAG, "Retrying to load code-server (attempt " + mRetryCount + "/" + MAX_RETRY_COUNT + ")");
                    loadCodeServerUrl();
                }
            }, RETRY_DELAY_MS);
        } else {
            Toast.makeText(CodeServerActivity.this,
                    "Failed to connect to code-server after " + MAX_RETRY_COUNT + " attempts.",
                    Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
        }
    }
    

    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Disable menu item actions to prevent exiting
        return true;
    }
    
    @Override
    public void onBackPressed() {
        // Handle back button within WebView only, never exit
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
        // Don't call super.onBackPressed() - prevents exiting
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Handle back button within WebView only
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }
            return true; // Consume the event, don't exit
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Re-enable fullscreen when window regains focus
            enableFullscreen();
        }
    }
    
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
