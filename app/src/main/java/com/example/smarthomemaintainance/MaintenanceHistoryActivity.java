package com.example.smarthomemaintainance;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MaintenanceHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerHistory;
    private int applianceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_history);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        applianceId = getIntent().getIntExtra("appliance_id", -1);
        
        if (applianceId == -1) {
            Toast.makeText(this, "Appliance not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        loadHistory();
    }
    
    private void loadHistory() {
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<MaintenanceHistoryEntity> historyList = ApplianceDatabase.getInstance(getApplicationContext()).maintenanceHistoryDao().getHistoryForAppliance(applianceId);
            
            runOnUiThread(() -> {
                if (historyList.isEmpty()) {
                    Toast.makeText(this, "No maintenance history available", Toast.LENGTH_SHORT).show();
                }
                MaintenanceHistoryAdapter adapter = new MaintenanceHistoryAdapter(historyList);
                recyclerHistory.setAdapter(adapter);
            });
        });
    }
}
