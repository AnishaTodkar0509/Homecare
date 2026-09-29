package com.example.smarthomemaintainance;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ApplianceDao {

    @Insert
    void insert(Appliance appliance);

    @Update
    void update(Appliance appliance);

    @Delete
    void delete(Appliance appliance);

    @Query("SELECT * FROM appliances WHERE userEmail = :userEmail")
    List<Appliance> getAllAppliances(String userEmail);

    @Query("SELECT * FROM appliances WHERE id = :id AND userEmail = :userEmail")
    Appliance getApplianceById(int id, String userEmail);
    
    @Query("SELECT * FROM appliances WHERE userEmail = :userEmail AND (name LIKE '%' || :searchQuery || '%' OR category LIKE '%' || :searchQuery || '%')")
    List<Appliance> searchAppliances(String searchQuery, String userEmail);
}
