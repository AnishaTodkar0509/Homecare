package com.example.smarthomemaintainance;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MaintenanceActivity extends AppCompatActivity {

    private EditText etMaintenanceDate;
    private EditText etMaintenanceTime;
    private EditText etNotes;
    private Spinner spinnerServiceType;
    private MaterialButton btnSaveMaintenance;

    private int applianceId;

    private static final String PREF_NAME = "HomeCareData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maintenance);

        etMaintenanceDate = findViewById(R.id.etMaintenanceDate);
        etMaintenanceTime = findViewById(R.id.etMaintenanceTime);
        etNotes = findViewById(R.id.etNotes);
        spinnerServiceType = findViewById(R.id.spinnerServiceType);
        btnSaveMaintenance = findViewById(R.id.btnSaveMaintenance);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.maintenance_service_types,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerServiceType.setAdapter(adapter);

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

        etMaintenanceDate.setOnClickListener(v ->
                showDatePicker()
        );

        etMaintenanceTime.setOnClickListener(v ->
                showTimePicker()
        );

        btnSaveMaintenance.setOnClickListener(v ->
                saveMaintenance()
        );
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {

                            String date =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d/%02d/%04d",
                                            selectedDay,
                                            selectedMonth + 1,
                                            selectedYear
                                    );

                            etMaintenanceDate.setText(date);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    etMaintenanceTime.setText(time);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void saveMaintenance() {

        String date =
                etMaintenanceDate
                        .getText()
                        .toString()
                        .trim();
                        
        String time =
                etMaintenanceTime
                        .getText()
                        .toString()
                        .trim();

        String notes =
                etNotes
                        .getText()
                        .toString()
                        .trim();

        String serviceType =
                spinnerServiceType
                        .getSelectedItem()
                        .toString();

        if (date.isEmpty()) {

            etMaintenanceDate.setError(
                    "Please select a maintenance date"
            );

            return;
        }
        
        if (time.isEmpty()) {
            etMaintenanceTime.setError("Please select a maintenance time");
            return;
        }

        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String activeUserEmail = preferences.getString("active_user_email", "");
            
            ApplianceDatabase db = ApplianceDatabase.getInstance(getApplicationContext());
            Appliance appliance = db.applianceDao().getApplianceById(applianceId, activeUserEmail);
            
            if (appliance != null) {
                appliance.setMaintenanceDate(date);
                appliance.setMaintenanceType(serviceType);
                appliance.setMaintenanceNotes(notes);
                
                db.applianceDao().update(appliance);
                
                MaintenanceHistoryEntity history = new MaintenanceHistoryEntity(applianceId, date, serviceType, notes);
                db.maintenanceHistoryDao().insert(history);
                
                runOnUiThread(() -> {
                    scheduleMaintenanceNotification(date, time, serviceType, appliance.getName());
                    Toast.makeText(this, "Maintenance saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Appliance not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void scheduleMaintenanceNotification(
            String date,
            String time,
            String serviceType,
            String applianceName
    ) {

        try {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            Locale.getDefault()
                    );

            Date maintenanceDate =
                    dateFormat.parse(date + " " + time);

            if (maintenanceDate == null) {
                return;
            }

            Calendar notificationTime =
                    Calendar.getInstance();

            notificationTime.setTime(
                    maintenanceDate
            );

            // Don't schedule an alarm in the past
            if (notificationTime.getTimeInMillis()
                    <= System.currentTimeMillis()) {

                return;
            }

            Intent intent =
                    new Intent(
                            this,
                            MaintenanceReceiver.class
                    );

            intent.putExtra(
                    "appliance_id",
                    applianceId
            );

            intent.putExtra(
                    "appliance_name",
                    applianceName
            );

            intent.putExtra(
                    "maintenance_type",
                    serviceType
            );

            intent.putExtra(
                    "maintenance_date",
                    date
            );

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            this,
                            applianceId,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                                    | PendingIntent.FLAG_IMMUTABLE
                    );

            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(
                            Context.ALARM_SERVICE
                    );

            if (alarmManager != null) {

                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime.getTimeInMillis(),
                        pendingIntent
                );
            }

        } catch (ParseException e) {

            e.printStackTrace();
        }
    }
}