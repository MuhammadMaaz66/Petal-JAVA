package com.example.flowerobjectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.opencv.android.OpenCVLoader;

public class Dashboard extends AppCompatActivity {

private Button settings;

    private static final String TAG = "CameraActivity"; // Changed log tag

    private Button btn_detect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);




        // Logging OpenCV initialization
        if (OpenCVLoader.initDebug()) {
            Log.i(TAG, "OpenCV init successfully");
        } else {
            Log.e(TAG, "OpenCV init failed");
        }

        initializeUi();
        initializeDetectAction();
    }

    private void initializeUi() {
        btn_detect = findViewById(R.id.btn_detect);
    }

    private void initializeDetectAction() {
        btn_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting CameraActivity
                startActivity(new Intent(Dashboard.this, CameraActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        settings = findViewById(R.id.btn_Settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, Settings.class));
            }
        });



    }
}
