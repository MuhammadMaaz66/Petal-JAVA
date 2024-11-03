package com.example.flowerobjectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class EggplantInfo extends AppCompatActivity {
private Button gcomment;
private Button eggplantcomment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eggplant_info);


        eggplantcomment = findViewById(R.id.btn_eggplantcomment);
        eggplantcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EggplantInfo.this, EggplantComment.class));
            }
        });


    }
}