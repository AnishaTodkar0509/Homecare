package com.example.smarthomemaintainance;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "maintenance_history")
public class MaintenanceHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int applianceId;
    private String date;
    private String type;
    private String notes;

    public MaintenanceHistoryEntity(int applianceId, String date, String type, String notes) {
        this.applianceId = applianceId;
        this.date = date;
        this.type = type;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getApplianceId() { return applianceId; }
    public void setApplianceId(int applianceId) { this.applianceId = applianceId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
