package com.example.smarthomemaintainance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ApplianceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmptyMessage;

    private ApplianceAdapter applianceAdapter;
    private ArrayList<Appliance> applianceList;

    private static final String PREF_NAME = "HomeCareData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_list);

        recyclerView = findViewById(R.id.recyclerAppliances);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAppliances(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAppliances(newText);
                return true;
            }
        });

        loadAppliances();
    }

    private String getActiveUserEmail() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getString("active_user_email", "");
    }

    private void filterAppliances(String query) {
        if (query == null || query.isEmpty()) {
            loadAppliances();
            return;
        }
        
        String activeUserEmail = getActiveUserEmail();
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            applianceList = (ArrayList<Appliance>) ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().searchAppliances(query, activeUserEmail);
            
            runOnUiThread(() -> {
                if (applianceList.isEmpty()) {
                    recyclerView.setVisibility(RecyclerView.GONE);
                    tvEmptyMessage.setVisibility(TextView.VISIBLE);
                    tvEmptyMessage.setText("No appliances found");
                } else {
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                    tvEmptyMessage.setVisibility(TextView.GONE);
                    applianceAdapter = new ApplianceAdapter(this, applianceList);
                    recyclerView.setAdapter(applianceAdapter);
                }
            });
        });
    }

    private void loadAppliances() {
        String activeUserEmail = getActiveUserEmail();
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            applianceList = (ArrayList<Appliance>) ApplianceDatabase.getInstance(getApplicationContext()).applianceDao().getAllAppliances(activeUserEmail);
            
            runOnUiThread(() -> {
                if (applianceList.isEmpty()) {
                    recyclerView.setVisibility(RecyclerView.GONE);
                    tvEmptyMessage.setVisibility(TextView.VISIBLE);
                } else {
                    recyclerView.setVisibility(RecyclerView.VISIBLE);
                    tvEmptyMessage.setVisibility(TextView.GONE);
                    applianceAdapter = new ApplianceAdapter(this, applianceList);
                    recyclerView.setAdapter(applianceAdapter);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerView != null) {
            loadAppliances();
        }
    }
}