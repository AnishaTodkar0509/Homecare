package com.example.smarthomemaintainance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword;
    private MaterialButton btnSave, btnLogout;
    private ImageView btnBack, ivProfilePhoto, btnEditPhoto;
    
    private static final String PREF_NAME = "HomeCareData";
    private User currentUser;
    private String currentPhotoUri = null;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        
                        File dir = new File(getFilesDir(), "profile_images");
                        if (!dir.exists()) dir.mkdirs();
                        
                        File file = new File(dir, "profile_" + System.currentTimeMillis() + ".jpg");
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.flush();
                        fos.close();
                        
                        currentPhotoUri = file.getAbsolutePath();
                        ivProfilePhoto.setImageURI(Uri.fromFile(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnEditPhoto = findViewById(R.id.btnEditPhoto);

        btnBack.setOnClickListener(v -> finish());
        
        btnEditPhoto.setOnClickListener(v -> mGetContent.launch("image/*"));
        
        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            preferences.edit().remove("active_user_email").apply();
            
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUserProfile();

        btnSave.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String activeUserEmail = preferences.getString("active_user_email", "");

        if (!activeUserEmail.isEmpty()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                currentUser = ApplianceDatabase.getInstance(getApplicationContext()).userDao().getUserByEmail(activeUserEmail);
                runOnUiThread(() -> {
                    if (currentUser != null) {
                        etName.setText(currentUser.getName());
                        etEmail.setText(currentUser.getEmail());
                        etPassword.setText(currentUser.getPassword());
                        
                        if (currentUser.getProfilePhotoUri() != null && !currentUser.getProfilePhotoUri().isEmpty()) {
                            currentPhotoUri = currentUser.getProfilePhotoUri();
                            try {
                                File imgFile = new File(currentPhotoUri);
                                if(imgFile.exists()){
                                    ivProfilePhoto.setImageURI(Uri.fromFile(imgFile));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            });
        }
    }

    private void saveUserProfile() {
        if (currentUser == null) return;

        String newName = etName.getText() != null ? etName.getText().toString().trim() : "";
        String newPassword = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (newName.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Name and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setName(newName);
        currentUser.setPassword(newPassword);
        if (currentPhotoUri != null) {
            currentUser.setProfilePhotoUri(currentPhotoUri);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ApplianceDatabase.getInstance(getApplicationContext()).userDao().update(currentUser);
            runOnUiThread(() -> {
                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
