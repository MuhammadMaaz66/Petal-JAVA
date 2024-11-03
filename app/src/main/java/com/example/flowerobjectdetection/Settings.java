package com.example.flowerobjectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import  android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {
 private Button profileButton;
 private Button logoutButton;
    FirebaseAuth auth;
    Button button;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileButton = findViewById(R.id.btn_Profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, UserProfile.class);
                startActivity(intent);

            }
        });
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.btn_logout);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(Settings.this, Login.class);
            startActivity(intent);
            finish();
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Settings.this, Login.class);
                startActivity(intent);
                finish();


            }
        });



    }
}