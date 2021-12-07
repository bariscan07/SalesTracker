package com.example.salestracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Java class that corresponds to the "customer_table" in our database.
@Entity(tableName = "customer_table")
public class Customer {
    //Java attributes that correspond to the fields in customer_table.
    @PrimaryKey(autoGenerate = true)
    private int ID;
    private String name;
    private String phoneNo;
    private String address;

    public Customer(String name, String phoneNo, String address) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.address = address;
    }

    //Getters and setters.
    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String  getAddress() {
        return address;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
