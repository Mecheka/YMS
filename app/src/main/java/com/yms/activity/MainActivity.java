package com.yms.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.yms.R;
import com.yms.manager.URLManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //TEST URL
    private static final String TEST_DEFUAL = "https://www.google.co.th";

    //REQUEST CODE
    private static final int REQUEST_SCANBARCODE = 701;
    private static final int REQUEST_SETTING = 702;

    //SharedPreferences file
    private static final String SPF_URL = "vidsurl";
    private static final String URL = "url";

    private WebView webView;
    private Button btnBarcode;
    private Button btnSetting;
    private boolean firtTime = true;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstance();

        SharedPreferences urlSPF = getSharedPreferences(SettingActicity.SPF_URL, Context.MODE_PRIVATE);
        url = urlSPF.getString(SettingActicity.URL, "");
        if (url.isEmpty()) {

            Intent settingIntent = new Intent(MainActivity.this, SettingActicity.class);
            startActivityForResult(settingIntent, REQUEST_SETTING);
        }else {
            Intent scanIntent = new Intent(MainActivity.this, ScanBarActivity.class);
            startActivityForResult(scanIntent, REQUEST_SCANBARCODE);
        }
    }

    private void initInstance() {

        webView = findViewById(R.id.webView);
        btnBarcode = findViewById(R.id.btnScanBarCode);
        btnSetting = findViewById(R.id.btnSetting);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(TEST_DEFUAL);

        btnBarcode.setOnClickListener(this);
        btnSetting.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SCANBARCODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Log.d("Scan bar code kyo", data.getStringExtra("barcode"));
                        Toast.makeText(MainActivity.this, data.getStringExtra("barcode"), Toast.LENGTH_SHORT).show();
                        SharedPreferences urlPreferences = getSharedPreferences(SPF_URL, Context.MODE_PRIVATE);
                        String urlWithData = urlPreferences.getString(URL, "") + data.getStringExtra("barcode");
                        webView.loadUrl(urlWithData);
                    }
                }
                break;
            case REQUEST_SETTING:
                if (resultCode == RESULT_OK) {
                    Intent scanIntent = new Intent(this, ScanBarActivity.class);
                    startActivityForResult(scanIntent, REQUEST_SCANBARCODE);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScanBarCode:
                Intent scanIntent = new Intent(MainActivity.this, ScanBarActivity.class);
                startActivityForResult(scanIntent, REQUEST_SCANBARCODE);
                break;
            case R.id.btnSetting:
                Intent settingIntent = new Intent(MainActivity.this, SettingActicity.class);
                startActivity(settingIntent);
                break;
        }
    }
}
