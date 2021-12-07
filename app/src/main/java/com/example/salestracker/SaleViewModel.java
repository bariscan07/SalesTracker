package com.example.salestracker;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SaleViewModel extends AndroidViewModel {

    private SalesTrackerRepository myRepository;
    private LiveData<List<Sale>> myAllSales;


    public SaleViewModel(@NonNull Application application) {
        super(application);
        myRepository = new SalesTrackerRepository(application);
        myAllSales = myRepository.getAllSales();
    }

    LiveData<List<Sale>> getAllSales() {
        return myAllSales;
    }

    List<Sale> getAllSalesList(){
        return myRepository.getmAllSalesList();
    }

    Sale getSale(int saleID){ return myRepository.getSale(saleID);}

    public long insert(Sale sale) {
        return myRepository.insert(sale);
    }

    public void update(Sale sale) {
        myRepository.update(sale);
    }

    public void delete(Sale sale) {
        myRepository.delete(sale);
    }

    public void deleteAllSales() {
        myRepository.deleteAllSales();
    }
}
