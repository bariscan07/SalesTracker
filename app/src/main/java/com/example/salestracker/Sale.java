package com.example.salestracker;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

//Java class that corresponds to the "sale_table" in our database.
@Entity(tableName = "sale_table", foreignKeys = {@ForeignKey(entity = Customer.class,
        parentColumns = "ID",
        childColumns = "customerId",
        onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Product.class,
                parentColumns = "ID",
                childColumns = "productId",
                onDelete = ForeignKey.CASCADE)
})
public class Sale {

    //Java attributes that correspond to the fields in sale table.
    @PrimaryKey(autoGenerate = true)
    private int ID;
    private int customerId;
    private int productId;
    private String title;
    private String description;
    private double price;
    private Date saleDate;
    private Date payday;

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    private boolean status;

    //Getters and setters.
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getPayday() {
        return payday;
    }

    public void setPayday(Date payday) {
        this.payday = payday;
    }
}
