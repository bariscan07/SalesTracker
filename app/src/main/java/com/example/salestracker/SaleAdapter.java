package com.example.salestracker;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {

    private final LayoutInflater myInflater;
    private CustomerViewModel customerViewModel;
    private Context context;
    private List<Sale> mySales;
    private List<Sale> mySalesFull;
    private onSaleClickListener listener;

    public SaleAdapter(Context context, List<Sale> mySales) {
        this.mySales = mySales;
        mySalesFull = new ArrayList<>(mySales);
        this.context = context;
        myInflater = LayoutInflater.from(context);
    }

    public List<Sale> getMySalesFull() {
        return mySalesFull;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = myInflater.inflate(R.layout.card_view_sale, parent, false);
        customerViewModel = new ViewModelProvider((FragmentActivity) context).get(CustomerViewModel.class);
        return new SaleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleAdapter.SaleViewHolder holder, int position) {
        Sale sale = mySales.get(position);
        holder.saleTitle.setText(sale.getTitle());
        holder.customerTitle.setText(customerViewModel.getCustomerName(sale.getCustomerId()));
        DateFormat outputFormatter = new SimpleDateFormat("dd/MM/yyyy");

        holder.payday.setText(outputFormatter.format(sale.getPayday()));
        holder.date.setText(outputFormatter.format(sale.getSaleDate()));
        holder.price.setText(String.valueOf(sale.getPrice()));
        holder.status.setText((sale.isStatus()) ? "Payed" : "Not Payed");
        holder.status.setTextColor((sale.isStatus()) ? Color.GREEN : Color.RED);
        if (sale.isStatus())
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        else
            holder.status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_red, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return mySales.size();
    }

    public void setSales(List<Sale> sales) {
        mySales = sales;
        notifyDataSetChanged();
    }

    public Sale getSaleAt(int position) {
        return mySales.get(position);
    }

    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Sale> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mySalesFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Sale sale: mySalesFull){
                    if (sale.getTitle().toLowerCase().contains(filterPattern)){
                        filteredList.add(sale);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mySales.clear();
            mySales.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class SaleViewHolder extends RecyclerView.ViewHolder {

        private TextView saleTitle;
        private TextView customerTitle;
        private TextView date;
        private TextView payday;
        private TextView price;
        private TextView status;


        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            saleTitle = itemView.findViewById(R.id.sale_title);
            customerTitle = itemView.findViewById(R.id.customer_title);
            payday = itemView.findViewById(R.id.payday);
            date = itemView.findViewById(R.id.date);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onSaleClick(mySales.get(position));
                }
            });
        }
    }

    public interface onSaleClickListener {
        void onSaleClick(Sale sale);
    }

    public void setOnSaleClickListener(onSaleClickListener listener) {
        this.listener = listener;
    }
}
