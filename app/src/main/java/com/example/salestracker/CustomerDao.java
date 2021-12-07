package com.example.salestracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Dao interface class to construct an API for our database queries under customer_table.
@Dao
public interface CustomerDao {

    //Insert query.
    @Insert
    long insert(Customer customer);

    //Select query to get all customers as a list.
    @Query("SELECT * from customer_table ORDER BY name")
    LiveData<List<Customer>> getCustomers();

    //Get particular customer
    @Query("SELECT * from customer_table WHERE ID= :customerID")
    Customer getCustomer(int customerID);

    //Get customers in list format.
    @Query("SELECT * from customer_table ORDER BY name")
    List<Customer> getCustomersList();

    //Delete query to delete particular customer instance.
    @Delete
    void deleteCustomer(Customer customer);

    //Update query to update particular customer instance.
    @Update
    void updateCustomer(Customer customer);

    //Delete all records.
    @Query("DELETE FROM customer_table")
    void deleteAll();

    //Get customer name according to condition.
    @Query("SELECT name FROM customer_table WHERE ID = :cID")
    String getCustomerName(int cID);

}
