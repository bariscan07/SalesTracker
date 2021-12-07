package com.example.salestracker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Room database implementation.
@Database(entities = {Sale.class, Customer.class, Product.class}, version = 3, exportSchema = false)
//Converters for date to string casting under sale_table.
@TypeConverters({Converters.class})
public abstract class SalesTrackerDatabase extends RoomDatabase {

    //Dao getter methods.
    public abstract SaleDao saleDao();

    public abstract CustomerDao customerDao();

    public abstract ProductDao productDao();

    //Single instance of a database is opened at the same time. Singleton that is.
    private static volatile SalesTrackerDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //Creation of SalesTrackerDatabase.
    static SalesTrackerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            //Synchronized will make sure database is opened at once and at most one instance is operating.
            synchronized (SalesTrackerDatabase.class) {
                //If instance is null,
                if (INSTANCE == null) {
                    //then build it.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SalesTrackerDatabase.class, "sales_tracker_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        //Return to created database instance. Or just return it if it s already created, i.e. if not null.
        return INSTANCE;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {

                CustomerDao dao = INSTANCE.customerDao();
                //dao.deleteAll();
                ProductDao pDao = INSTANCE.productDao();
                //pDao.deleteAll();
                SaleDao sDao = INSTANCE.saleDao();
                //sDao.deleteAll();
            });
        }
    };
}
