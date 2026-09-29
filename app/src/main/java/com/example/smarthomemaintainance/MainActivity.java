package com.example.smarthomemaintainance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvApplianceCount;
    private TextView tvDashboardMaintenance;
    private TextView tvGreeting;

    private TextView tvHealthScore;
    private TextView tvHealthStatus;
    private TextView tvHealthMessage;

    private com.google.android.material.floatingactionbutton.FloatingActionButton btnAddAppliance;
    private MaterialButton btnViewAppliances;

    private static final String PREF_NAME = "HomeCareData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvApplianceCount = findViewById(R.id.tvApplianceCount);
        tvDashboardMaintenance = findViewById(R.id.tvDashboardMaintenance);
        tvGreeting = findViewById(R.id.tvGreeting);

        tvHealthScore = findViewById(R.id.tvHealthScore);
        tvHealthStatus = findViewById(R.id.tvHealthStatus);
        tvHealthMessage = findViewById(R.id.tvHealthMessage);

        btnAddAppliance = findViewById(R.id.btnAddAppliance);
        btnViewAppliances = findViewById(R.id.btnViewAppliances);

        // Add Appliance button
        btnAddAppliance.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddApplianceActivity.class
            );

            startActivity(intent);
        });

        // View Appliances button
        btnViewAppliances.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ApplianceListActivity.class
            );

            startActivity(intent);
        });

        // Profile button
        android.view.View btnProfileCard = findViewById(R.id.btnProfileCard);
        btnProfileCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        
        // Notifications button
        android.view.View btnNotifications = findViewById(R.id.btnNotifications);
        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        updateApplianceCount();
        loadUpcomingMaintenance();
        calculateHomeHealthScore();
        loadUserName();
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                sendWelcomeNotification();
            }
        } else {
            sendWelcomeNotification();
        }
    }

    private void sendWelcomeNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        
        android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
        if (manager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationChannel channel = new android.app.NotificationChannel(
                        "dashboard_channel",
                        "Dashboard Updates",
                        android.app.NotificationManager.IMPORTANCE_DEFAULT
                );
                manager.createNotificationChannel(channel);
            }
            androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, "dashboard_channel")
                    .setSmallIcon(R.drawable.ic_homecare_logo)
                    .setContentTitle("Smart Home Monitor Active")
                    .setContentText("Your smart home dashboard is up and running.")
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            manager.notify(1001, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                sendWelcomeNotification();
            }
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        updateApplianceCount();
        loadUpcomingMaintenance();
        calculateHomeHealthScore();
        loadUserName();
    }

    private String getActiveUserEmail() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getString("active_user_email", "");
    }

    private void loadUserName() {
        String activeUserEmail = getActiveUserEmail();
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User user = ApplianceDatabase.getInstance(getApplicationContext()).userDao().getUserByEmail(activeUserEmail);
            if (user != null) {
                String firstName = user.getName() != null ? user.getName().split(" ")[0] : "";
                String photoUri = user.getProfilePhotoUri();
                
                runOnUiThread(() -> {
                    if (tvGreeting != null && !firstName.isEmpty()) {
                        tvGreeting.setText("Hello, " + firstName);
                    }
                    
                    if (photoUri != null && !photoUri.isEmpty()) {
                        android.widget.ImageView btnProfile = findViewById(R.id.btnProfile);
                        try {
                            java.io.File imgFile = new java.io.File(photoUri);
                            if(imgFile.exists() && btnProfile != null){
                                btnProfile.setImageURI(android.net.Uri.fromFile(imgFile));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void updateApplianceCount() {
        String activeUserEmail = getActiveUserEmail();
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int count = ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().getAllAppliances(activeUserEmail).size();
            runOnUiThread(() -> tvApplianceCount.setText(String.valueOf(count)));
        });
    }

    private void loadUpcomingMaintenance() {
        String activeUserEmail = getActiveUserEmail();
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            java.util.List<Appliance> appliances = ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().getAllAppliances(activeUserEmail);
            
            String nearestDate = "";
            String nearestName = "";
            String nearestType = "";
            Date nearestDateObject = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            for (Appliance appliance : appliances) {
                String maintenanceDate = appliance.getMaintenanceDate();
                if (maintenanceDate != null && !maintenanceDate.isEmpty()) {
                    try {
                        Date currentDate = dateFormat.parse(maintenanceDate);
                        if (currentDate != null && (nearestDateObject == null || currentDate.before(nearestDateObject))) {
                            nearestDateObject = currentDate;
                            nearestDate = maintenanceDate;
                            nearestName = appliance.getName() != null ? appliance.getName() : "Unknown Appliance";
                            nearestType = appliance.getMaintenanceType() != null ? appliance.getMaintenanceType() : "Maintenance";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            final String finalNearestDate = nearestDate;
            final String finalNearestName = nearestName;
            final String finalNearestType = nearestType;

            runOnUiThread(() -> {
                if (!finalNearestDate.isEmpty()) {
                    String status = getMaintenanceStatus(finalNearestDate);
                    String maintenanceText = "🔧 " + finalNearestName
                            + "\n📅 " + finalNearestDate
                            + "\n🔧 " + finalNearestType
                            + "\n\n" + status;
                    tvDashboardMaintenance.setText(maintenanceText);
                } else {
                    tvDashboardMaintenance.setText("No upcoming maintenance");
                }
            });
        });
    }

    private String getMaintenanceStatus(String maintenanceDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date maintenance = dateFormat.parse(maintenanceDate);
            Date today = dateFormat.parse(dateFormat.format(new Date()));

            if (maintenance == null || today == null) {
                return "Maintenance date unavailable";
            }

            if (maintenance.before(today)) {
                long difference = today.getTime() - maintenance.getTime();
                long days = difference / (1000 * 60 * 60 * 24);
                return "🔴 Overdue by " + days + " day(s)";
            } else if (maintenance.equals(today)) {
                return "🟡 Due Today";
            } else {
                long difference = maintenance.getTime() - today.getTime();
                long days = difference / (1000 * 60 * 60 * 24);
                return "🟢 Upcoming • " + days + " day(s) remaining";
            }
        } catch (ParseException e) {
            return "Maintenance date unavailable";
        }
    }

    private void calculateHomeHealthScore() {
        String activeUserEmail = getActiveUserEmail();
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            java.util.List<Appliance> appliances = ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().getAllAppliances(activeUserEmail);
            int applianceCount = appliances.size();
            
            runOnUiThread(() -> {
                int score = 100;

                if (applianceCount == 0) {
                    tvHealthScore.setText("100 / 100");
                    tvHealthStatus.setText("🟢 Ready to get started");
                    tvHealthMessage.setText("Add your appliances to start managing your home.");
                    return;
                }

                int appliancesWithoutMaintenance = 0;
                int appliancesOverdue = 0;
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date today = null;
                try {
                    today = dateFormat.parse(dateFormat.format(new Date()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for (Appliance appliance : appliances) {
                    if (appliance.getMaintenanceDate() == null || appliance.getMaintenanceDate().isEmpty()) {
                        appliancesWithoutMaintenance++;
                    } else if (today != null) {
                        try {
                            Date maintenance = dateFormat.parse(appliance.getMaintenanceDate());
                            if (maintenance != null && maintenance.before(today)) {
                                appliancesOverdue++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                score -= appliancesWithoutMaintenance * 10;
                score -= appliancesOverdue * 20; // Deduct more for overdue
                if (score < 0) score = 0;

                tvHealthScore.setText(score + " / 100");

                if (score >= 80) {
                    tvHealthStatus.setText("🟢 Excellent");
                    tvHealthMessage.setText("Your home is well maintained.");
                } else if (score >= 60) {
                    tvHealthStatus.setText("🟡 Good");
                    tvHealthMessage.setText("A few appliances need attention.");
                } else if (score >= 40) {
                    tvHealthStatus.setText("🟠 Needs Attention");
                    tvHealthMessage.setText("Some appliances need maintenance or are overdue.");
                } else {
                    tvHealthStatus.setText("🔴 Needs Maintenance");
                    tvHealthMessage.setText("Several appliances are overdue or need attention.");
                }
            });
        });
    }
}