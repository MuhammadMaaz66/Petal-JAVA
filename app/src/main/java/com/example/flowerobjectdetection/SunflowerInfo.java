package com.example.flowerobjectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SunflowerInfo extends AppCompatActivity {
private Button sunflowercomment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunflower_info);

        sunflowercomment = findViewById(R.id.btn_sunflowercomment);
        sunflowercomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SunflowerInfo.this, SunflowerComment.class));
            }
        });
    }
}