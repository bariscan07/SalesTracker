package com.example.salestracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    public SalesTrackerRepository mRepository;
    private LiveData<List<Product>> mAllProducts;


    public ProductViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SalesTrackerRepository(application);
        mAllProducts = mRepository.getAllProducts();
    }

    LiveData<List<Product>> getAllProducts() {
        return mAllProducts;
    }

    List<Product> getAllProductsList() {
        return mRepository.getmAllProductsList();
    }

    Product getProduct(int pID){
        return mRepository.getProduct(pID);
    }

    public long insert(Product product) {
        return mRepository.insert(product);
    }

    public void update(Product product) {
        mRepository.update(product);
    }

    public void delete(Product product) {
        mRepository.delete(product);
    }

    public void deleteAll() {
        mRepository.deleteAllProducts();
    }

    public String getProductName(int pID) {
        return mRepository.getProductName(pID);
    }
}
