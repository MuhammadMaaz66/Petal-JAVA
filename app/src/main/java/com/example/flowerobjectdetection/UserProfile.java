package com.example.flowerobjectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView emailTxt, uidTxt, usernameTxt; // Added usernameTxt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize TextViews
        emailTxt = findViewById(R.id.emailTextView);
        uidTxt = findViewById(R.id.usernameTextView);
        usernameTxt = findViewById(R.id.usernameTextView); // TextView for displaying username

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Display email and UID
            emailTxt.setText(currentUser.getEmail());
            uidTxt.setText(currentUser.getUid());

            // Retrieve username from Firestore
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get username from Firestore
                            String username = documentSnapshot.getString("username");
                            usernameTxt.setText(username); // Display username
                        } else {
                            Toast.makeText(UserProfile.this, "User profile not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserProfile.this, "Error getting user profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
