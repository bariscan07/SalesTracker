package com.example.salestracker;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

//Data repository class to utilize Dao classes and the queries. Methods in here will be invoked in ViewModel class.
public class SalesTrackerRepository {

    //Dao instances.
    private SaleDao mSaleDao;
    private CustomerDao mCustomerDao;
    private ProductDao mProductDao;
    //Listing items.
    private LiveData<List<Sale>> mAllSales;
    private LiveData<List<Customer>> mAllCustomers;
    private LiveData<List<Product>> mAllProducts;


    SalesTrackerRepository(Application application) {
        SalesTrackerDatabase db = SalesTrackerDatabase.getDatabase(application);
        //Define Daos
        mSaleDao = db.saleDao();
        mCustomerDao = db.customerDao();
        mProductDao = db.productDao();
        //Define the list of items.
        mAllSales = mSaleDao.getSales();
        mAllCustomers = mCustomerDao.getCustomers();
        mAllProducts = mProductDao.getProducts();
    }

    //Getters for the lists.
    LiveData<List<Sale>> getAllSales() {
        return mAllSales;
    }

    List<Sale> getmAllSalesList() {
        return mSaleDao.getSalesList();
    }

    Sale getSale(int saleID) {
        return mSaleDao.getSale(saleID);
    }

    String getCustomerName(int cID) {
        return mCustomerDao.getCustomerName(cID);
    }

    LiveData<List<Customer>> getAllCustomers() {
        return mAllCustomers;
    }

    List<Customer> getmAllCustomersList() {
        return mCustomerDao.getCustomersList();
    }

    Customer getCustomer(int customerID) {
        return mCustomerDao.getCustomer(customerID);
    }

    LiveData<List<Product>> getAllProducts() {
        return mAllProducts;
    }

    List<Product> getmAllProductsList() {
        return mProductDao.getProductsList();
    }

    String getProductName(int pID) {
        return mProductDao.getProductName(pID);
    }

    Product getProduct(int pID){
        return mProductDao.getProduct(pID);
    }

    //Sale insertion query execution.
    public long insert(Sale sale) {
        return mSaleDao.insert(sale);
    }

    //Customer insertion query execution.
    public long insert(Customer customer) {
        return mCustomerDao.insert(customer);
    }

    //Product insertion query execution.
    public long insert(Product product) {
        return mProductDao.insert(product);
    }

    public void update(Sale sale) {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mSaleDao.updateSale(sale));
    }

    public void update(Customer customer) {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mCustomerDao.updateCustomer(customer));
    }

    public void update(Product product) {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mProductDao.updateProduct(product));
    }

    public void delete(Sale sale) {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mSaleDao.deleteSale(sale));
    }

    public void delete(Customer customer) {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mCustomerDao.deleteCustomer(customer));
    }

    public void delete(Product product) {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mProductDao.deleteProduct(product));
    }

    public void deleteAllCustomers() {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mCustomerDao.deleteAll());
    }

    public void deleteAllSales() {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mSaleDao.deleteAll());
    }

    public void deleteAllProducts() {
        SalesTrackerDatabase.databaseWriteExecutor.execute(() -> mProductDao.deleteAll());
    }
}
