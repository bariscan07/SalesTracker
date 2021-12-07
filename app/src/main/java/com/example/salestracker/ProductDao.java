package com.example.salestracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Dao interface class to construct an API for our database queries under product_table.
@Dao
public interface ProductDao {

    //Insert query.
    @Insert
    long insert(Product product);

    //Select query to get all products as a list.
    @Query("SELECT * from product_table")
    LiveData<List<Product>> getProducts();

    //Delete query to delete particular product instance.
    @Delete
    void deleteProduct(Product product);

    //Delete all records.
    @Query("DELETE FROM product_table")
    void deleteAll();

    //Update query to update particular product instance.
    @Update
    void updateProduct(Product product);

    //Get products in list format.
    @Query("SELECT * from product_table")
    List<Product> getProductsList();

    @Query("SELECT productName FROM product_table WHERE ID = :pID")
    String getProductName(int pID);

    @Query("SELECT * FROM product_table WHERE ID = :pID")
    Product getProduct(int pID);
}
