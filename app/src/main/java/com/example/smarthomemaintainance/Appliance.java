package com.example.smarthomemaintainance;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "appliances")
public class Appliance {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String category;
    private String brand;
    private String model;
    private String purchaseDate;
    private String warranty;
    private String lastService;
    private String serviceInterval;
    private String repairPerson;
    private String repairPhone;
    private String maintenanceDate;
    private String maintenanceType;
    private String maintenanceNotes;
    private String userEmail;

    public Appliance() {
    }

    @Ignore
    public Appliance(
            int id,
            String name,
            String category,
            String brand,
            String model,
            String purchaseDate,
            String warranty,
            String lastService,
            String serviceInterval,
            String repairPerson,
            String repairPhone,
            String maintenanceDate,
            String maintenanceType,
            String maintenanceNotes,
            String userEmail
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.purchaseDate = purchaseDate;
        this.warranty = warranty;
        this.lastService = lastService;
        this.serviceInterval = serviceInterval;
        this.repairPerson = repairPerson;
        this.repairPhone = repairPhone;
        this.maintenanceDate = maintenanceDate;
        this.maintenanceType = maintenanceType;
        this.maintenanceNotes = maintenanceNotes;
        this.userEmail = userEmail;
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public String getWarranty() { return warranty; }
    public void setWarranty(String warranty) { this.warranty = warranty; }
    
    public String getLastService() { return lastService; }
    public void setLastService(String lastService) { this.lastService = lastService; }
    
    public String getServiceInterval() { return serviceInterval; }
    public void setServiceInterval(String serviceInterval) { this.serviceInterval = serviceInterval; }
    
    public String getRepairPerson() { return repairPerson; }
    public void setRepairPerson(String repairPerson) { this.repairPerson = repairPerson; }
    
    public String getRepairPhone() { return repairPhone; }
    public void setRepairPhone(String repairPhone) { this.repairPhone = repairPhone; }
    
    public String getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(String maintenanceDate) { this.maintenanceDate = maintenanceDate; }
    
    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }
    
    public String getMaintenanceNotes() { return maintenanceNotes; }
    public void setMaintenanceNotes(String maintenanceNotes) { this.maintenanceNotes = maintenanceNotes; }
}