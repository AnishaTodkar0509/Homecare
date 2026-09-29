package com.example.smarthomemaintainance;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class AddApplianceActivity extends AppCompatActivity {

    private EditText etApplianceName;
    private EditText etBrand;
    private EditText etModelNumber;
    private EditText etPurchaseDate;
    private EditText etLastServiceDate;
    private EditText etRepairPerson;
    private EditText etRepairPhone;

    private Spinner spCategory;
    private Spinner spWarranty;
    private Spinner spServiceInterval;

    private MaterialButton btnSaveAppliance;

    private static final String PREF_NAME = "HomeCareData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appliance);

        // -----------------------------
        // Connect XML views
        // -----------------------------

        etApplianceName = findViewById(R.id.etApplianceName);
        etBrand = findViewById(R.id.etBrand);
        etModelNumber = findViewById(R.id.etModelNumber);
        etPurchaseDate = findViewById(R.id.etPurchaseDate);
        etLastServiceDate = findViewById(R.id.etLastServiceDate);
        etRepairPerson = findViewById(R.id.etRepairPerson);
        etRepairPhone = findViewById(R.id.etRepairPhone);

        spCategory = findViewById(R.id.spCategory);
        spWarranty = findViewById(R.id.spWarranty);
        spServiceInterval = findViewById(R.id.spServiceInterval);

        btnSaveAppliance = findViewById(R.id.btnSaveAppliance);

        // -----------------------------
        // Setup dropdowns
        // -----------------------------

        setupSpinners();

        // -----------------------------
        // Date pickers
        // -----------------------------

        etPurchaseDate.setOnClickListener(v ->
                showDatePicker(etPurchaseDate)
        );

        etLastServiceDate.setOnClickListener(v ->
                showDatePicker(etLastServiceDate)
        );

        // -----------------------------
        // Save button
        // -----------------------------

        btnSaveAppliance.setOnClickListener(v ->
                saveAppliance()
        );

        // -----------------------------
        // Load existing appliance if editing
        // -----------------------------

        applianceId = getIntent().getIntExtra("appliance_id", -1);
        if (applianceId != -1) {
            loadExistingAppliance(applianceId);
        }
    }

    private int applianceId = -1;
    private Appliance editingAppliance = null;

    private void loadExistingAppliance(int id) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String activeUserEmail = preferences.getString("active_user_email", "");
        
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            editingAppliance = ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().getApplianceById(id, activeUserEmail);
            runOnUiThread(() -> {
                if (editingAppliance != null) {
                    etApplianceName.setText(editingAppliance.getName());
                    etBrand.setText(editingAppliance.getBrand());
                    etModelNumber.setText(editingAppliance.getModel());
                    etPurchaseDate.setText(editingAppliance.getPurchaseDate());
                    etLastServiceDate.setText(editingAppliance.getLastService());
                    etRepairPerson.setText(editingAppliance.getRepairPerson());
                    etRepairPhone.setText(editingAppliance.getRepairPhone());
                    
                    setSpinnerToValue(spCategory, editingAppliance.getCategory());
                    setSpinnerToValue(spWarranty, editingAppliance.getWarranty());
                    setSpinnerToValue(spServiceInterval, editingAppliance.getServiceInterval());
                }
            });
        });
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    // =========================================================
    // Setup Spinners
    // =========================================================

    private void setupSpinners() {

        String[] categories = {
                "Air Conditioner",
                "Refrigerator",
                "Washing Machine",
                "Television",
                "Microwave",
                "Water Heater",
                "Water Purifier",
                "Other"
        };

        String[] warrantyOptions = {
                "No Warranty",
                "1 Year",
                "2 Years",
                "3 Years",
                "5 Years"
        };

        String[] serviceIntervals = {
                "3 Months",
                "6 Months",
                "12 Months",
                "18 Months",
                "24 Months"
        };

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> warrantyAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        warrantyOptions
                );

        warrantyAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spWarranty.setAdapter(warrantyAdapter);

        ArrayAdapter<String> intervalAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        serviceIntervals
                );

        intervalAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spServiceInterval.setAdapter(intervalAdapter);
    }

    // =========================================================
    // Date Picker
    // =========================================================

    private void showDatePicker(EditText targetField) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String date = String.format(
                                    "%02d/%02d/%04d",
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear
                            );

                            targetField.setText(date);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }

    // =========================================================
    // Save Appliance
    // =========================================================

    private void saveAppliance() {

        String applianceName =
                etApplianceName.getText().toString().trim();

        String brand =
                etBrand.getText().toString().trim();

        String modelNumber =
                etModelNumber.getText().toString().trim();

        String purchaseDate =
                etPurchaseDate.getText().toString().trim();

        String lastServiceDate =
                etLastServiceDate.getText().toString().trim();

        String repairPerson =
                etRepairPerson.getText().toString().trim();

        String repairPhone =
                etRepairPhone.getText().toString().trim();

        String category =
                spCategory.getSelectedItem().toString();

        String warranty =
                spWarranty.getSelectedItem().toString();

        String serviceInterval =
                spServiceInterval.getSelectedItem().toString();

        // -----------------------------
        // Validation
        // -----------------------------

        if (applianceName.isEmpty()) {
            etApplianceName.setError("Enter appliance name");
            etApplianceName.requestFocus();
            return;
        }

        if (brand.isEmpty()) {
            etBrand.setError("Enter brand");
            etBrand.requestFocus();
            return;
        }

        if (modelNumber.isEmpty()) {
            etModelNumber.setError("Enter model number");
            etModelNumber.requestFocus();
            return;
        }

        if (purchaseDate.isEmpty()) {
            etPurchaseDate.setError("Select purchase date");
            return;
        }

        if (lastServiceDate.isEmpty()) {
            etLastServiceDate.setError("Select last service date");
            return;
        }

        if (repairPerson.isEmpty()) {
            etRepairPerson.setError("Enter repair person's name");
            etRepairPerson.requestFocus();
            return;
        }

        if (repairPhone.isEmpty()) {
            etRepairPhone.setError("Enter phone number");
            etRepairPhone.requestFocus();
            return;
        }

        // -----------------------------
        // Room Database Insertion/Update
        // -----------------------------
        
        Appliance appliance = editingAppliance != null ? editingAppliance : new Appliance();
        appliance.setName(applianceName);
        appliance.setBrand(brand);
        appliance.setModel(modelNumber);
        appliance.setPurchaseDate(purchaseDate);
        appliance.setLastService(lastServiceDate);
        appliance.setRepairPerson(repairPerson);
        appliance.setRepairPhone(repairPhone);
        appliance.setCategory(category);
        appliance.setWarranty(warranty);
        appliance.setServiceInterval(serviceInterval);
        
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String activeUserEmail = preferences.getString("active_user_email", "");
        appliance.setUserEmail(activeUserEmail);

        if (editingAppliance == null) {
            appliance.setMaintenanceDate("");
            appliance.setMaintenanceType("");
            appliance.setMaintenanceNotes("");
        }

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (editingAppliance == null) {
                ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().insert(appliance);
            } else {
                ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().update(appliance);
            }
            
            scheduleWarrantyNotification(appliance.getName(), appliance.getPurchaseDate(), appliance.getWarranty());
            scheduleServiceNotification(appliance.getName(), appliance.getLastService(), appliance.getServiceInterval());
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Appliance saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void scheduleServiceNotification(String applianceName, String lastServiceDate, String serviceInterval) {
        if (lastServiceDate == null || serviceInterval == null || serviceInterval.isEmpty()) return;
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Date lastService = format.parse(lastServiceDate);
            if (lastService == null) return;

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(lastService);

            int months = 0;
            if (serviceInterval.equals("3 Months")) months = 3;
            else if (serviceInterval.equals("6 Months")) months = 6;
            else if (serviceInterval.equals("12 Months")) months = 12;
            else if (serviceInterval.equals("18 Months")) months = 18;
            else if (serviceInterval.equals("24 Months")) months = 24;

            if (months == 0) return;

            cal.add(java.util.Calendar.MONTH, months);
            cal.add(java.util.Calendar.DAY_OF_YEAR, -7); // Notify 7 days before

            if (cal.getTimeInMillis() <= System.currentTimeMillis()) return;

            android.content.Intent intent = new android.content.Intent(this, MaintenanceReceiver.class);
            intent.putExtra("appliance_name", applianceName);
            intent.putExtra("maintenance_type", "Scheduled Maintenance Due Soon");
            intent.putExtra("maintenance_date", format.format(cal.getTime()));

            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                this,
                (applianceName + "_service").hashCode(),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );

            android.app.AlarmManager am = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
            if (am != null) {
                am.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleWarrantyNotification(String applianceName, String purchaseDate, String warranty) {
        if (purchaseDate == null || warranty == null || warranty.equals("No Warranty") || warranty.equals("-")) return;
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Date purchase = format.parse(purchaseDate);
            if (purchase == null) return;
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(purchase);
            
            int years = 0;
            if (warranty.equals("1 Year")) years = 1;
            else if (warranty.equals("2 Years")) years = 2;
            else if (warranty.equals("3 Years")) years = 3;
            else if (warranty.equals("5 Years")) years = 5;
            
            if (years == 0) return;
            
            cal.add(java.util.Calendar.YEAR, years);
            cal.add(java.util.Calendar.DAY_OF_YEAR, -7); // Notify 7 days before expiry
            
            if (cal.getTimeInMillis() <= System.currentTimeMillis()) return;
            
            android.content.Intent intent = new android.content.Intent(this, MaintenanceReceiver.class);
            intent.putExtra("appliance_name", applianceName);
            intent.putExtra("maintenance_type", "Warranty Expiring Soon");
            intent.putExtra("maintenance_date", format.format(cal.getTime()));
            
            android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                this, 
                applianceName.hashCode(), 
                intent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );
            
            android.app.AlarmManager am = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
            if (am != null) {
                am.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}