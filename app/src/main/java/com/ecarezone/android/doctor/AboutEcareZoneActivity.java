package com.ecarezone.android.doctor;

import android.os.Bundle;
import android.webkit.WebView;

import com.ecarezone.android.doctor.config.Constants;

/**
 * Created by L&T Technology Services on 2/29/2016.
 */
public class AboutEcareZoneActivity extends EcareZoneBaseActivity {
    private String ABOUT_ECAREZONE_URL = "http://ecarezone.com/#our_mission";

    @Override
    protected String getCallerName() {
        return null;
    }

    @Override
    public void onNavigationChanged(int fragmentLayoutResId, Bundle args) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_about_ecare);
        WebView webView=(WebView)findViewById(R.id.webview_about_ecare);
        webView.loadUrl(ABOUT_ECAREZONE_URL);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DoctorApplication.nameValuePair.put(Constants.STATUS_CHANGE, true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DoctorApplication.nameValuePair.put(Constants.STATUS_CHANGE, false);
    }
}
