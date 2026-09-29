package com.example.smarthomemaintainance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Check empty email
        if (email.isEmpty()) {
            Toast.makeText(
                    LoginActivity.this,
                    "Please enter your email",
                    Toast.LENGTH_SHORT
            ).show();

            etEmail.requestFocus();
            return;
        }

        // Check email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                    LoginActivity.this,
                    "Enter a valid email",
                    Toast.LENGTH_SHORT
            ).show();

            etEmail.requestFocus();
            return;
        }

        // Check empty password
        if (password.isEmpty()) {
            Toast.makeText(
                    LoginActivity.this,
                    "Please enter your password",
                    Toast.LENGTH_SHORT
            ).show();

            etPassword.requestFocus();
            return;
        }

        // Check password length
        if (password.length() < 6) {
            Toast.makeText(
                    LoginActivity.this,
                    "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();

            etPassword.requestFocus();
            return;
        }

        // Validate credentials against Room DB
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User user = ApplianceDatabase.getInstance(getApplicationContext()).userDao().login(email, password);
            
            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Login successful, save active user session
                android.content.SharedPreferences preferences = getSharedPreferences("HomeCareData", MODE_PRIVATE);
                preferences.edit().putString("active_user_email", email).apply();

                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        });
    }
}