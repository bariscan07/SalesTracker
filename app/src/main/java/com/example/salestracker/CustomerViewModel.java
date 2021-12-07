package com.example.salestracker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

//View model class for the customer entity that utilizes the relevant repository methods.
public class CustomerViewModel extends AndroidViewModel {

    private SalesTrackerRepository mRepository;
    private LiveData<List<Customer>> mAllCustomers;

    public CustomerViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SalesTrackerRepository(application);
        mAllCustomers = mRepository.getAllCustomers();
    }

    LiveData<List<Customer>> getAllCustomers() {
        return mAllCustomers;
    }

    List<Customer> getmAllCustomersList() {
        return mRepository.getmAllCustomersList();
    }

    public long insert(Customer customer) {
        return  mRepository.insert(customer);
    }

    public void update(Customer customer) {
        mRepository.update(customer);
    }

    public void delete(Customer customer) {
        mRepository.delete(customer);
    }

    public void deleteAllCustomers() {
        mRepository.deleteAllCustomers();
    }

    public Customer getCustomer(int customerID){ return mRepository.getCustomer(customerID);}

    public String getCustomerName(int cID){ return mRepository.getCustomerName(cID);}
}
