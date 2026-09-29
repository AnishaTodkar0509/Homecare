package com.example.smarthomemaintainance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Appliance.class, MaintenanceHistoryEntity.class, User.class}, version = 4, exportSchema = false)
public abstract class ApplianceDatabase extends RoomDatabase {

    private static ApplianceDatabase instance;

    public abstract ApplianceDao applianceDao();
    public abstract MaintenanceHistoryDao maintenanceHistoryDao();
    public abstract UserDao userDao();

    public static synchronized ApplianceDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ApplianceDatabase.class, "appliance_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
