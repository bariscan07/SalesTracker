package com.example.salestracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class ProductFragment extends Fragment {
    private ProductViewModel mProductViewModel;
    public static final int NEW_PRODUCT_ACTIVITY_REQUEST_CODE = 1;
    public static final int PRODUCT_DETAILS_REQUEST_CODE = 3;
    private ProductAdapter adapter;


    public ProductFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the request was to add new product, get the data from the NewProductActvity class.
        if (requestCode == NEW_PRODUCT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //Initialize a product instance with the intent data.
            Product product = new Product();
            product.setProductName(data.getStringExtra(NewProductActivity.EXTRA_PTITLE));
            product.setDescription(data.getStringExtra(NewProductActivity.EXTRA_PDESC));
            product.setImageUri(data.getStringExtra(NewProductActivity.EXTRA_IMAGEPATH));
            //Get the Id of the recently inserted product.
            long newProductID = mProductViewModel.insert(product);
            //Get the recently inserted product.
            Product insertedProduct = mProductViewModel.getProduct((int) newProductID);
            //Include it on the search list as well.
            adapter.getMyProductsFull().add(insertedProduct);
            //Feedback.
            Toast.makeText(getActivity(), "Product saved", Toast.LENGTH_SHORT).show();

        }
        //Else if it was an details request and a modification performed, do the following.
        else if (requestCode == PRODUCT_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            //Update the original list as well to update search function as well.
            List<Product> productListFull = adapter.getMyProductsFull();
            //Either on update or delete, we need to find the modified product from the search list and remove it first.
            Product productToRemove = productListFull.stream().filter(oldProduct -> data.getIntExtra(NewProductActivity.EXTRA_PID, 0) == oldProduct.getID()).findFirst().orElse(null);
            productListFull.remove(productToRemove);
            //If modification was a delete operation, inform the user..
            if (data.hasExtra(ProductDetailsActivity.DELETE)) {
                Toast.makeText(getActivity(), "Product Deleted", Toast.LENGTH_SHORT).show();
            }
            //Else if it was an update, we need to replace the old product which we have just removed with the updated product on the search list.
            else {
                productListFull.add(mProductViewModel.getProduct(data.getIntExtra(NewProductActivity.EXTRA_PID, 0)));
                Toast.makeText(getActivity(), "Product List Updated", Toast.LENGTH_SHORT).show();
            }
        }
        //Else If detail page visited but nothing has been changed, feedback accordingly.
        else if (requestCode == PRODUCT_DETAILS_REQUEST_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Product not Modified", Toast.LENGTH_SHORT).show();
        }
        //Else, it was an intent to NewProductActivity, but cancelled, so feedback.
        else {
            Toast.makeText(getActivity(), "Product not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Options menu to place searchView.
        setHasOptionsMenu(true);
        //Set the view and recyclerview.
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewProduct);

        //Initialize adapter and product view model
        mProductViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        adapter = new ProductAdapter(getActivity(), mProductViewModel.getAllProductsList());

        //Set the adapter and layout manager for the recyclerView.
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Get the products and bind the data.
        mProductViewModel.getAllProducts().observe(requireActivity(), products -> adapter.setProducts(products));

        //Intent that will start NewProductActivity.
        FloatingActionButton buttonAddProduct = view.findViewById(R.id.button_add_product);
        buttonAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewProductActivity.class);
            startActivityForResult(intent, NEW_PRODUCT_ACTIVITY_REQUEST_CODE);
        });

        //Swipe function to delete.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mProductViewModel.delete(adapter.getProductAt(viewHolder.getAdapterPosition()));
                adapter.getMyProductsFull().remove(adapter.getProductAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), "Sale Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        //On card item click, start the ProductDetailsActivity with necessary data.
        adapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
            intent.putExtra(NewProductActivity.EXTRA_PID, product.getID());
            intent.putExtra(NewProductActivity.EXTRA_PTITLE, product.getProductName());
            intent.putExtra(NewProductActivity.EXTRA_PDESC, product.getDescription());
            intent.putExtra(NewProductActivity.EXTRA_IMAGEPATH, product.getImageUri());
            startActivityForResult(intent, PRODUCT_DETAILS_REQUEST_CODE);
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
