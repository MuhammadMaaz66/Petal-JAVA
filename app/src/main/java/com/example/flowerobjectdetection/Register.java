package com.example.flowerobjectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
public class Register extends AppCompatActivity {

    private Button registerBtn, resendEmailBtn;
    private TextInputEditText editTextEmail, editTextPassword, editTextUsername;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView, timerTextView;
    private FirebaseUser user;

    // Timer countdown value (100 seconds)
    private static final long TIMER_DURATION = 100000; // 100 seconds

    // Regex pattern for the password
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final String GMAIL_DOMAIN = "@gmail.com";

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Only redirect to the Login screen, not the dashboard
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Redirects to Login if user has verified email but hasn't logged in yet
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.txtregisterEmail);
        editTextPassword = findViewById(R.id.txtregisterPassword);
        registerBtn = findViewById(R.id.btn_register);
        resendEmailBtn = findViewById(R.id.btn_resend); // Button for resending verification email
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        timerTextView = findViewById(R.id.timerTextView); // TextView to display timer
        editTextUsername = findViewById(R.id.txtUserName);

        textView.setOnClickListener(v -> openLoginScreen());

        registerBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());
            String username = String.valueOf(editTextUsername.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Validate email domain
            if (!email.endsWith(GMAIL_DOMAIN)) {
                Toast.makeText(Register.this, "Only Gmail accounts are allowed.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Validate password
            if (!Pattern.matches(PASSWORD_PATTERN, password)) {
                Toast.makeText(Register.this, "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, and a digit.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Create the user
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Store the user object
                            user = mAuth.getCurrentUser();

                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(Register.this,
                                                        "Registration successful. Verification email sent to " + user.getEmail(),
                                                        Toast.LENGTH_SHORT).show();

                                                // Notify user to verify their email
                                                Toast.makeText(Register.this,
                                                        "Please verify your email before logging in.",
                                                        Toast.LENGTH_LONG).show();

                                                // Start the countdown timer
                                                startEmailVerificationTimer();

                                            } else {
                                                Toast.makeText(Register.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            if (task.getException() != null && task.getException().getMessage().contains("email address is already in use")) {
                                Toast.makeText(Register.this, "Email already in use. Please log in.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        resendEmailBtn.setOnClickListener(v -> {
            if (user != null) {
                user.sendEmailVerification()
                        .addOnCompleteListener(emailTask -> {
                            if (emailTask.isSuccessful()) {
                                Toast.makeText(Register.this,
                                        "Verification email resent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                                startEmailVerificationTimer(); // Reset the timer
                            } else {
                                Toast.makeText(Register.this,
                                        "Failed to resend verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void startEmailVerificationTimer() {
        timerTextView.setVisibility(View.VISIBLE);  // Ensure the timer is visible
        resendEmailBtn.setVisibility(View.GONE);    // Hide resend button until timer finishes

        new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeLeft = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                timerTextView.setText("Time left to verify email: " + timeLeft);
            }


            @Override
            public void onFinish() {
                timerTextView.setText("Time's up! Resend the verification email.");
                resendEmailBtn.setVisibility(View.VISIBLE); // Show resend button

                // Check if the email is verified
                if (user != null && !user.isEmailVerified()) {
                    // If not verified, delete the user
                    user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Email verification failed. Please try registering again.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Register.this, "Failed to remove unverified account.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private void openLoginScreen() {
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (user.isEmailVerified()) {
                        // Email verified, proceed to Login screen
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error verifying user. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not found. Please register again.", Toast.LENGTH_SHORT).show();
        }
    }
}
