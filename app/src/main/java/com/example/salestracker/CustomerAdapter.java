package com.example.salestracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> implements Filterable {

    private final LayoutInflater myInflater;
    //List that stores customers that are currently visible on the screen.
    private List<Customer> myCustomers;
    //All of the customers in the database.
    private List<Customer> myCustomersFull;
    //Click listener for each card on the screen.
    private onCustomerClickListener listener;

    //Constructor
    public CustomerAdapter(Context context, List<Customer> myCustomers) {
        this.myCustomers = myCustomers;
        myCustomersFull = new ArrayList<>(myCustomers);
        myInflater = LayoutInflater.from(context);
    }

    //Getter for the full customer list
    public List<Customer> getMyCustomersFull() {
        return myCustomersFull;
    }

    //Attach view to ViewHolder.
    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = myInflater.inflate(R.layout.card_view_customer, parent, false);
        return new CustomerViewHolder(itemView);
    }

    //Fill the view components with necessary information
    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = myCustomers.get(position);
        holder.customerTitle.setText(customer.getName());
        holder.phoneNo.setText(customer.getPhoneNo());
        holder.address.setText(customer.getAddress());
    }

    //Return the size of the visible customers list.
    @Override
    public int getItemCount() {
        return myCustomers.size();
    }

    //Setter for the visible customers list.
    public void setCustomers(List<Customer> customers) {
        myCustomers = customers;
        notifyDataSetChanged();
    }

    //Get the customer at particular position.
    public Customer getCustomerAt(int position) {
        return myCustomers.get(position);
    }

    //Return search filter.
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    //Search feature implementation.
    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Initialize resulting list.
            List<Customer> filteredList = new ArrayList<>();
            //If there is no search query yet, fill the resulting set with all of the customers.
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(myCustomersFull);
                //Else filter the all customers and place the ones to the visible customer list that matches with the query
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Customer customer : myCustomersFull) {
                    if (customer.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(customer);
                    }
                }
            }
            //Return the result set
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        //Reflect changes on the screen
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myCustomers.clear();
            myCustomers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    //ViewHolder class for the customer
    class CustomerViewHolder extends RecyclerView.ViewHolder {

        private TextView customerTitle;
        private TextView phoneNo;
        private TextView address;


        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerTitle = itemView.findViewById(R.id.customer_title);
            phoneNo = itemView.findViewById(R.id.customer_phoneNo);
            address = itemView.findViewById(R.id.customer_address);
            //Assign click listener to each card view on the list.
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCustomerClick(myCustomers.get(position));
                }
            });
        }
    }

    public interface onCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    //Set the click listeners for the card views.
    public void setOnCustomerClickLister(onCustomerClickListener listener) {
        this.listener = listener;
    }
}
