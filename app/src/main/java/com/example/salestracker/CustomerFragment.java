package com.example.salestracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CustomerFragment extends Fragment {
    private CustomerViewModel mCustomerViewModel;
    public static final int NEW_CUSTOMER_ACTIVITY_REQUEST_CODE = 1;
    public static final int CUSTOMER_DETAILS_REQUEST_CODE = 2;
    private CustomerAdapter adapter;

    public CustomerFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the request was to add new customer get the data from the NewCustomerActivity class.
        if (requestCode == NEW_CUSTOMER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String cTitle = data.getStringExtra(NewCustomerActivity.EXTRA_CTITLE);
            String cPhoneNo = data.getStringExtra(NewCustomerActivity.EXTRA_CPHONENO);
            String cAddress = data.getStringExtra(NewCustomerActivity.EXTRA_CADDRESS);

            //New customer created with the intent data.
            Customer customer = new Customer(cTitle, cPhoneNo, cAddress);
            //Insert the customer and get the recently inserted record.
            long newCustomerID = mCustomerViewModel.insert(customer);
            Customer insertedCustomer = mCustomerViewModel.getCustomer((int) newCustomerID);
            //Update the original full list to make the new sale searchable as well.
            adapter.getMyCustomersFull().add(insertedCustomer);
            //Feedback
            Toast.makeText(getActivity(), "Customer saved", Toast.LENGTH_SHORT).show();
        }
        //Else if the request came from the CustomerDetailsActivity, that means an update was intended.
        else if(requestCode == CUSTOMER_DETAILS_REQUEST_CODE && resultCode == RESULT_OK){
            //Update the original list as well to update search function as well.
            List<Customer> customerListFull = adapter.getMyCustomersFull();
            //Either on update or delete, we need to find the modified customer from the search list and remove it first.
            Customer customerToRemove = customerListFull.stream().filter(oldCustomer -> data.getIntExtra(NewCustomerActivity.EXTRA_CID,0) == oldCustomer.getID()).findFirst().orElse(null);
            customerListFull.remove(customerToRemove);

            //If modification was a delete operation, inform the user..
            if (data.hasExtra(CustomerDetailsActivity.DELETE)){
                Toast.makeText(getActivity(), "Customer Deleted", Toast.LENGTH_SHORT).show();
            }
            //Else if it was an update, we need to replace the old customer which we have just removed with the updated customer on the search list.
            else{
                customerListFull.add(mCustomerViewModel.getCustomer(data.getIntExtra(NewCustomerActivity.EXTRA_CID,0)));
                Toast.makeText(getActivity(), "Customer List Updated", Toast.LENGTH_SHORT).show();
            }
        }
        //Else If detail page visited but nothing has been changed, feedback accordingly.
        else if(requestCode == CUSTOMER_DETAILS_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Customer not Modified", Toast.LENGTH_SHORT).show();
        }
        //Else, it was an intent to NewCustomerActivity, but cancelled, so feedback.
        else {
            Toast.makeText(getActivity(), "Customer not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Options menu to place searchView.
        setHasOptionsMenu(true);
        //Set the view and recyclerview.
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCustomer);

        //Initialize adapter and customer view model
        mCustomerViewModel = new ViewModelProvider(requireActivity()).get(CustomerViewModel.class);
        adapter = new CustomerAdapter(getActivity(), mCustomerViewModel.getmAllCustomersList());

        //Set the adapter and layout manager for the recyclerView.
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Get the customers and bind the data.
        mCustomerViewModel.getAllCustomers().observe(requireActivity(), customers -> adapter.setCustomers(customers));

        //Intent that will start NewCustomerActivity.
        FloatingActionButton buttonAddCustomer = view.findViewById(R.id.button_add_customer);
        buttonAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewCustomerActivity.class);
            startActivityForResult(intent, NEW_CUSTOMER_ACTIVITY_REQUEST_CODE);
        });

        //Delete functionality on swipe.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mCustomerViewModel.delete(adapter.getCustomerAt(viewHolder.getAdapterPosition()));
                adapter.getMyCustomersFull().remove(adapter.getCustomerAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), "Customer Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        //On card item click, start the CustomerDetailsActivity with necessary data.
        adapter.setOnCustomerClickLister(customer -> {
            Intent intent = new Intent(getActivity(), CustomerDetailsActivity.class);
            intent.putExtra(NewCustomerActivity.EXTRA_CID, customer.getID());
            intent.putExtra(NewCustomerActivity.EXTRA_CTITLE, customer.getName());
            intent.putExtra(NewCustomerActivity.EXTRA_CPHONENO, customer.getPhoneNo());
            intent.putExtra(NewCustomerActivity.EXTRA_CADDRESS, customer.getAddress());
            startActivityForResult(intent, CUSTOMER_DETAILS_REQUEST_CODE);
        });

        return view;
    }

    //Search functionality.
    @Override
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
