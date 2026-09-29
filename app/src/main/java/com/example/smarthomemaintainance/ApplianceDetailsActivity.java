package com.example.smarthomemaintainance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ApplianceDetailsActivity extends AppCompatActivity {

    private TextView tvApplianceName;
    private TextView tvCategory;
    private TextView tvBrand;
    private TextView tvModel;
    private TextView tvPurchaseDate;
    private TextView tvWarranty;
    private TextView tvWarrantyStatus;
    private TextView tvWarrantyExpiry;
    private TextView tvLastService;
    private TextView tvServiceInterval;
    private TextView tvRepairPerson;
    private TextView tvRepairPhone;
    private TextView tvMaintenance;

    private MaterialButton btnCallRepair;
    private MaterialButton btnScheduleMaintenance;

    private MaterialButton btnEditAppliance;
    private MaterialButton btnDeleteAppliance;

    private Appliance currentAppliance;

    private static final String PREF_NAME = "HomeCareData";

    private int applianceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appliance_details);

        // Connect views

        tvApplianceName = findViewById(R.id.tvApplianceName);
        tvCategory = findViewById(R.id.tvCategory);
        tvBrand = findViewById(R.id.tvBrand);
        tvModel = findViewById(R.id.tvModel);
        tvPurchaseDate = findViewById(R.id.tvPurchaseDate);
        tvWarranty = findViewById(R.id.tvWarranty);
        tvWarrantyStatus = findViewById(R.id.tvWarrantyStatus);
        tvWarrantyExpiry = findViewById(R.id.tvWarrantyExpiry);
        tvLastService = findViewById(R.id.tvLastService);
        tvServiceInterval = findViewById(R.id.tvServiceInterval);
        tvRepairPerson = findViewById(R.id.tvRepairPerson);
        tvRepairPhone = findViewById(R.id.tvRepairPhone);
        tvMaintenance = findViewById(R.id.tvMaintenance);

        btnCallRepair = findViewById(R.id.btnCallRepair);
        btnScheduleMaintenance = findViewById(R.id.btnScheduleMaintenance);
        btnEditAppliance = findViewById(R.id.btnEditAppliance);
        btnDeleteAppliance = findViewById(R.id.btnDeleteAppliance);

        // Get appliance ID

        applianceId = getIntent().getIntExtra(
                "appliance_id",
                -1
        );

        if (applianceId == -1) {

            Toast.makeText(
                    this,
                    "Appliance not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadApplianceDetails();

        // Call Repair Person

        btnCallRepair.setOnClickListener(v -> {
            if (currentAppliance != null && currentAppliance.getRepairPhone() != null && !currentAppliance.getRepairPhone().isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + currentAppliance.getRepairPhone()));
                startActivity(dialIntent);
            } else {
                Toast.makeText(this, "Repair phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Schedule Maintenance

        btnScheduleMaintenance.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ApplianceDetailsActivity.this,
                    MaintenanceActivity.class
            );

            intent.putExtra(
                    "appliance_id",
                    applianceId
            );

            startActivity(intent);
        });

        MaterialButton btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ApplianceDetailsActivity.this, MaintenanceHistoryActivity.class);
            intent.putExtra("appliance_id", applianceId);
            startActivity(intent);
        });

        // Edit Appliance

        btnEditAppliance.setOnClickListener(v -> {
            Intent intent = new Intent(ApplianceDetailsActivity.this, AddApplianceActivity.class);
            intent.putExtra("appliance_id", applianceId);
            startActivity(intent);
        });

        // Delete Appliance

        btnDeleteAppliance.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Appliance")
                .setMessage("Are you sure you want to delete this appliance?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (currentAppliance != null) {
                        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().delete(currentAppliance);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Appliance deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    private void loadApplianceDetails() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String activeUserEmail = preferences.getString("active_user_email", "");
        
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Appliance appliance = ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().getApplianceById(applianceId, activeUserEmail);
            
            runOnUiThread(() -> {
                if (appliance == null) {
                    Toast.makeText(this, "Appliance not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                currentAppliance = appliance;

                String name = appliance.getName() != null ? appliance.getName() : "Unknown Appliance";
                String category = appliance.getCategory() != null ? appliance.getCategory() : "-";
                String brand = appliance.getBrand() != null ? appliance.getBrand() : "-";
                String model = appliance.getModel() != null ? appliance.getModel() : "-";
                String purchaseDate = appliance.getPurchaseDate() != null ? appliance.getPurchaseDate() : "-";
                String warranty = appliance.getWarranty() != null ? appliance.getWarranty() : "-";
                String lastService = appliance.getLastService() != null ? appliance.getLastService() : "-";
                String serviceInterval = appliance.getServiceInterval() != null ? appliance.getServiceInterval() : "-";
                String repairPerson = appliance.getRepairPerson() != null ? appliance.getRepairPerson() : "-";
                String repairPhone = appliance.getRepairPhone() != null ? appliance.getRepairPhone() : "-";
                String maintenanceDate = appliance.getMaintenanceDate() != null ? appliance.getMaintenanceDate() : "";
                String maintenanceType = appliance.getMaintenanceType() != null ? appliance.getMaintenanceType() : "";

                tvApplianceName.setText(name);
                tvCategory.setText(category);
                tvBrand.setText(brand);
                tvModel.setText(model);
                tvPurchaseDate.setText(purchaseDate);

                tvWarranty.setText(calculateWarrantyStatus(purchaseDate, warranty));
                updateWarrantyCard(purchaseDate, warranty);

                tvLastService.setText(lastService);
                tvServiceInterval.setText(serviceInterval);
                tvRepairPerson.setText(repairPerson);
                tvRepairPhone.setText(repairPhone);

                if (!maintenanceDate.isEmpty()) {
                    String status = getMaintenanceStatus(maintenanceDate);
                    String maintenanceText = "📅 " + maintenanceDate + "\n🔧 " + maintenanceType + "\n\n" + status;
                    tvMaintenance.setText(maintenanceText);
                } else {
                    tvMaintenance.setText("No maintenance scheduled");
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

    private String calculateWarrantyStatus(
            String purchaseDate,
            String warranty
    ) {

        if (purchaseDate.equals("-")
                || warranty.equals("-")
                || warranty.equals("No Warranty")) {

            return "No Warranty";
        }

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            Date purchase =
                    dateFormat.parse(
                            purchaseDate
                    );

            if (purchase == null) {

                return warranty;
            }

            int warrantyYears =
                    getWarrantyYears(warranty);

            if (warrantyYears == 0) {

                return "No Warranty";
            }

            Calendar expiryCalendar =
                    Calendar.getInstance();

            expiryCalendar.setTime(purchase);

            expiryCalendar.add(
                    Calendar.YEAR,
                    warrantyYears
            );

            Date expiryDate =
                    expiryCalendar.getTime();

            Date today =
                    dateFormat.parse(
                            dateFormat.format(
                                    new Date()
                            )
                    );

            if (today == null) {

                return warranty;
            }

            long difference =
                    expiryDate.getTime()
                            - today.getTime();

            long daysRemaining =
                    difference /
                            (1000L * 60 * 60 * 24);

            String expiryText =
                    dateFormat.format(
                            expiryDate
                    );

            if (daysRemaining < 0) {

                return "🔴 Expired\n"
                        + "Expired on: "
                        + expiryText;

            } else if (daysRemaining <= 30) {

                return "🟡 Expiring Soon\n"
                        + "Expires: "
                        + expiryText
                        + "\n"
                        + daysRemaining
                        + " day(s) remaining";

            } else {

                return "🟢 Warranty Active\n"
                        + "Expires: "
                        + expiryText
                        + "\n"
                        + daysRemaining
                        + " day(s) remaining";
            }

        } catch (ParseException e) {

            return warranty;
        }
    }

    private void updateWarrantyCard(
            String purchaseDate,
            String warranty
    ) {

        if (purchaseDate.equals("-")
                || warranty.equals("-")
                || warranty.equals("No Warranty")) {

            tvWarrantyStatus.setText(
                    "No Warranty"
            );

            tvWarrantyExpiry.setText(
                    "This appliance does not have an active warranty."
            );

            return;
        }

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            Date purchase =
                    dateFormat.parse(
                            purchaseDate
                    );

            int warrantyYears =
                    getWarrantyYears(warranty);

            if (purchase == null
                    || warrantyYears == 0) {

                tvWarrantyStatus.setText(
                        "No Warranty"
                );

                tvWarrantyExpiry.setText(
                        "Warranty information unavailable."
                );

                return;
            }

            Calendar expiryCalendar =
                    Calendar.getInstance();

            expiryCalendar.setTime(purchase);

            expiryCalendar.add(
                    Calendar.YEAR,
                    warrantyYears
            );

            Date expiryDate =
                    expiryCalendar.getTime();

            Date today =
                    dateFormat.parse(
                            dateFormat.format(
                                    new Date()
                            )
                    );

            if (today == null) {
                return;
            }

            long difference =
                    expiryDate.getTime()
                            - today.getTime();

            long daysRemaining =
                    difference /
                            (1000L * 60 * 60 * 24);

            String expiryText =
                    dateFormat.format(
                            expiryDate
                    );

            if (daysRemaining < 0) {

                tvWarrantyStatus.setText(
                        "🔴 Warranty Expired"
                );

                tvWarrantyExpiry.setText(
                        "Expired on: "
                                + expiryText
                );

            } else if (daysRemaining <= 30) {

                tvWarrantyStatus.setText(
                        "🟡 Warranty Expiring Soon"
                );

                tvWarrantyExpiry.setText(
                        "Expires: "
                                + expiryText
                                + "\n"
                                + daysRemaining
                                + " day(s) remaining"
                );

            } else {

                tvWarrantyStatus.setText(
                        "🟢 Warranty Active"
                );

                tvWarrantyExpiry.setText(
                        "Expires: "
                                + expiryText
                                + "\n"
                                + daysRemaining
                                + " day(s) remaining"
                );
            }

        } catch (ParseException e) {

            tvWarrantyStatus.setText(
                    "Warranty information unavailable"
            );

            tvWarrantyExpiry.setText("");
        }
    }

    private int getWarrantyYears(String warranty) {

        if (warranty.equals("1 Year")) {
            return 1;
        }

        if (warranty.equals("2 Years")) {
            return 2;
        }

        if (warranty.equals("3 Years")) {
            return 3;
        }

        if (warranty.equals("5 Years")) {
            return 5;
        }

        return 0;
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (applianceId != -1) {

            loadApplianceDetails();
        }
    }
}
