package com.example.salestracker;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Java class that corresponds to the "product_table" in our database.
@Entity(tableName = "product_table")
public class Product {

    //Java attributes that correspond to the fields in product_table.
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public String productName;
    public String description;
    public String imageUri;

    //Getters and setters.

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return productName;
    }
}
