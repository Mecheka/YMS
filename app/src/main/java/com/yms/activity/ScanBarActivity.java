package com.yms.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;
import com.yms.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanBarActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    //REQUEST CODE
    private static final int REQEUST_CAMERA = 1001;

    //SharedPreferences file
    private static final String SPF_URL = "vidsurl";
    private static final String URL = "url";

    private ZXingScannerView scannerView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        SharedPreferences urlPreferences = getSharedPreferences(SPF_URL, Context.MODE_PRIVATE);
        url = urlPreferences.getString(URL, "");
        if (url.isEmpty()) {
            Intent settingIntent = new Intent(ScanBarActivity.this, SettingActicity.class);
            startActivity(settingIntent);
        }

        if (checkPermission()) {

        } else {
            requestPermission();
        }

    }

    @Override
    public void handleResult(Result result) {
        String scanResult = result.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("barcode", scanResult);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQEUST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission()){
            if (scannerView == null){
                scannerView = new ZXingScannerView(ScanBarActivity.this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(ScanBarActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ScanBarActivity.this, new String[]{Manifest.permission.CAMERA}, REQEUST_CAMERA);
    }
}
