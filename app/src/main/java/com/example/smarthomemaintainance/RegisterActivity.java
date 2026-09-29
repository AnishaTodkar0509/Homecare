package com.example.smarthomemaintainance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etRegisterEmail;
    private EditText etRegisterPassword;
    private EditText etConfirmPassword;

    private MaterialButton btnRegister;

    private SharedPreferences preferences;

    private static final String PREF_NAME = "HomeCareData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        btnRegister.setOnClickListener(v -> registerUser());

        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            etName.setError("Please enter your name");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etRegisterEmail.setError("Please enter your email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.setError("Please enter a valid email");
            etRegisterEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etRegisterPassword.setError("Please enter a password");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError(
                    "Password must contain at least 6 characters"
            );
            etRegisterPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User existingUser = ApplianceDatabase.getInstance(getApplicationContext()).userDao().getUserByEmail(email);
            
            runOnUiThread(() -> {
                if (existingUser != null) {
                    etRegisterEmail.setError("An account with this email already exists");
                    etRegisterEmail.requestFocus();
                    return;
                }

                // If not exists, insert new user
                executor.execute(() -> {
                    User newUser = new User(email, name, password);
                    ApplianceDatabase.getInstance(getApplicationContext()).userDao().insert(newUser);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                });
            });
        });
    }
}