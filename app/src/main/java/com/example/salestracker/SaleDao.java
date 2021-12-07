package com.example.salestracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Dao interface class to construct an API for our database queries under sale_table.
@Dao
public interface SaleDao {

    //Insert query and return to new records' ID.
    @Insert
    long insert(Sale sale);

    //Select query to get all sales as a list.
    @Query("SELECT * from sale_table ORDER BY payday ASC")
    LiveData<List<Sale>> getSales();

    //Get sales in list format.
    @Query("SELECT * from sale_table ORDER BY payday ASC")
    List<Sale> getSalesList();

    @Query("SELECT * from sale_table WHERE ID = :saleID")
    Sale getSale(int saleID);
    //Delete query to delete particular sale instance.
    @Delete
    void deleteSale(Sale sale);

    //Update query to update particular sale instance.
    @Update
    void updateSale(Sale sale);

    //Delete all customer records.
    @Query("DELETE FROM sale_table")
    void deleteAll();

    //Get customer of a particular sale
    @Query("SELECT c.name FROM customer_table c INNER JOIN sale_table s ON s.customerId = s.ID AND s.ID = :saleID ")
    String getSalesCustomer(int saleID);
}
