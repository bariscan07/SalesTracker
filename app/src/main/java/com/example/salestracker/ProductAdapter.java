package com.example.salestracker;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private final LayoutInflater myInflater;
    private List<Product> myProducts;
    private List<Product> myProductsFull;
    private Context context;
    private onProductClickListener listener;

    public ProductAdapter(Context context ,List<Product> myProducts) {
        this.myProducts = myProducts;
        myProductsFull = new ArrayList<>(myProducts);
        this.context = context;
        myInflater = LayoutInflater.from(context);

    }

    public List<Product> getMyProductsFull() {
        return myProductsFull;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = myInflater.inflate(R.layout.card_view_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = myProducts.get(position);
        holder.productTitle.setText(product.getProductName());
        holder.productDesc.setText(product.getDescription());
        Glide.with(context)
                .load(Uri.parse(product.getImageUri()))
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return myProducts.size();
    }

    public void setProducts(List<Product> products) {
        myProducts = products;
        notifyDataSetChanged();
    }

    public Product getProductAt(int position){return myProducts.get(position);}

    public Filter getFilter(){ return myFilter;}

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(myProductsFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Product product: myProductsFull){
                    if(product.getProductName().contains(filterPattern)){
                        filteredList.add(product);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myProducts.clear();
            myProducts.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productDesc;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productDesc = itemView.findViewById(R.id.product_desc);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onProductClick(myProducts.get(position));
                }
            });
        }
    }

    public interface onProductClickListener {
        void onProductClick(Product product);
    }

    //Set the click listeners for the card views.
    public void setOnProductClickListener(ProductAdapter.onProductClickListener listener) {
        this.listener = listener;
    }
}
