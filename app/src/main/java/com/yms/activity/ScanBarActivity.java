package com.yms.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.yms.R;

import java.io.IOException;

public class ScanBarActivity extends AppCompatActivity {

    //REQUEST CODE
    private static final int REQEUST_CAMERA = 1001;
    private SurfaceView cameraView;
    private BarcodeDetector barcode;
    private CameraSource cameraSource;
    private SurfaceHolder holder;
    private TextView tvScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        initInstace();
    }

    private void initInstace() {

        cameraView = findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        tvScan = findViewById(R.id.tvScan);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(ScanBarActivity.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        if (!barcode.isOperational()) {
            showToast("Sorry, Couldn't setup the detector");
            finish();
        }
        cameraSource = new CameraSource.Builder(ScanBarActivity.this, barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(ScanBarActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                    return;
                }
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeArray = detections.getDetectedItems();
                if (barcodeArray.size() != 0) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                    }else{
                        //deprecated in API 26
                        vibrator.vibrate(500);
                    }
                    tvScan.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ScanBarActivity.this, MainActivity.class);
                            intent.putExtra("barcode", barcodeArray.valueAt(0).displayValue);
                            setResult(RESULT_OK, intent);
                            finish();
                            cameraSource.stop();
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQEUST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPermission()) {
                        requestPermission();
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(ScanBarActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ScanBarActivity.this, new String[]{Manifest.permission.CAMERA}, REQEUST_CAMERA);
    }

    private void showToast(String s) {
    }
}
