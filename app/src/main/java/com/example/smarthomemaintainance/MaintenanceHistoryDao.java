package com.example.smarthomemaintainance;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MaintenanceHistoryDao {
    @Insert
    void insert(MaintenanceHistoryEntity history);

    @Query("SELECT * FROM maintenance_history WHERE applianceId = :applianceId ORDER BY id DESC")
    List<MaintenanceHistoryEntity> getHistoryForAppliance(int applianceId);
}
