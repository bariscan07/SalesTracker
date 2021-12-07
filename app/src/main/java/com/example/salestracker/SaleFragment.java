package com.example.salestracker;

import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.NestedScrollingChild;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SaleFragment extends Fragment {
    private SaleViewModel mSaleViewModel;
    public static final int NEW_SALE_ACTIVITY_REQUEST_CODE = 1;
    public static final int SALE_DETAILS_REQUEST_CODE = 2;
    SaleAdapter adapter;


    public SaleFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_SALE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Sale sale = new Sale();
            sale.setTitle(data.getStringExtra(NewSaleActivity.EXTRA_STITLE));
            DateFormat inputFormatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                sale.setSaleDate(inputFormatter.parse(data.getStringExtra(NewSaleActivity.EXTRA_SDATE)));
                sale.setPayday(inputFormatter.parse(data.getStringExtra(NewSaleActivity.EXTRA_SPAYDAY)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sale.setDescription(data.getStringExtra(NewSaleActivity.EXTRA_SDESC));
            sale.setCustomerId(data.getIntExtra(NewSaleActivity.EXTRA_SCUSTOMERID, 0));
            sale.setProductId(data.getIntExtra(NewSaleActivity.EXTRA_SPRODUCTID, 0));
            sale.setPrice(data.getDoubleExtra(NewSaleActivity.EXTRA_SPRICE, 0.0));
            long newSaleID = mSaleViewModel.insert(sale);
            Sale insertedSale = mSaleViewModel.getSale((int) newSaleID);
            adapter.getMySalesFull().add(insertedSale);
            Toast.makeText(getActivity(), "Sale Saved", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == SALE_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            //Get the old full list which is being used to search and update it as well.
            List<Sale> saleListFull = adapter.getMySalesFull();

            //Either on update or delete, we need to find the modified sale from the search list and remove it first.
            Sale saleToRemove = saleListFull.stream().filter(oldSale -> data.getIntExtra(NewSaleActivity.EXTRA_SID, 0) == oldSale.getID()).findFirst().orElse(null);
            saleListFull.remove(saleToRemove);

            //If modification was a delete operation, inform the user..
            if (data.hasExtra(SaleDetailsActivity.DELETE)){
                Toast.makeText(getActivity(), "Sale Deleted", Toast.LENGTH_SHORT).show();
            }
            //Else if it was an update, we need to replace the old sale which we have just removed with the updated sale on the search list.
            else{
                //Update the original sale list because we use it while searching and we need to be able to search the updated item as well.
                saleListFull.add(mSaleViewModel.getSale(data.getIntExtra(NewSaleActivity.EXTRA_SID, 0)));
                Toast.makeText(getActivity(), "Sale List Updated", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == SALE_DETAILS_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Sale not Modified", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Sale not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //To set the options menu to make search view visible.
        setHasOptionsMenu(true);
        //Set Recyclerview and initiate the adapter variable.
        View view = inflater.inflate(R.layout.fragment_sale, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSale);

        //SaleViewModel initialization to fetch the data and pass it to adapter instance.
        mSaleViewModel = new ViewModelProvider(requireActivity()).get(SaleViewModel.class);
        adapter = new SaleAdapter(getActivity(), mSaleViewModel.getAllSalesList());


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSaleViewModel.getAllSales().observe(requireActivity(), adapter::setSales);

        //Intent for add icon on the bottom right to start new activity.
        FloatingActionButton buttonAddSale = view.findViewById(R.id.button_add_sale);
        buttonAddSale.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewSaleActivity.class);
            startActivityForResult(intent, NEW_SALE_ACTIVITY_REQUEST_CODE);
        });

        //Swipe function to delete.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mSaleViewModel.delete(adapter.getSaleAt(viewHolder.getAdapterPosition()));
                //Remove it from the search list as well to make the sale not searchable anymore.
                adapter.getMySalesFull().remove(viewHolder.getAdapterPosition());
                Toast.makeText(getActivity(), "Sale Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnSaleClickListener(sale -> {
            CustomerViewModel mCustomerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
            ProductViewModel mProductViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
            Intent intent = new Intent(getActivity(), SaleDetailsActivity.class);
            intent.putExtra(NewSaleActivity.EXTRA_SID, sale.getID());
            intent.putExtra(NewSaleActivity.EXTRA_SCUSTOMERID, sale.getCustomerId());
            intent.putExtra(NewSaleActivity.EXTRA_SPRODUCTID, sale.getProductId());
            intent.putExtra(NewSaleActivity.EXTRA_STITLE, sale.getTitle());
            intent.putExtra(NewSaleActivity.EXTRA_SCUSTOMER, mCustomerViewModel.getCustomerName(sale.getCustomerId()));
            intent.putExtra(NewSaleActivity.EXTRA_SPRODUCT, mProductViewModel.getProductName(sale.getProductId()));

            DateFormat outputFormatter = new SimpleDateFormat("dd/MM/yyyy");
            String saleDate = outputFormatter.format(sale.getSaleDate());
            String payday = outputFormatter.format(sale.getPayday());

            intent.putExtra(NewSaleActivity.EXTRA_SDATE, saleDate);
            intent.putExtra(NewSaleActivity.EXTRA_SPAYDAY, payday);
            intent.putExtra(NewSaleActivity.EXTRA_SDESC, sale.getDescription());
            intent.putExtra(NewSaleActivity.EXTRA_SPRICE, sale.getPrice());
            intent.putExtra(NewSaleActivity.EXTRA_SSTATUS, sale.isStatus());
            startActivityForResult(intent, SALE_DETAILS_REQUEST_CODE);
        });

        return view;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }
}
