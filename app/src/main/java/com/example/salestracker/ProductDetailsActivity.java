package com.example.salestracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ProductDetailsActivity extends AppCompatActivity {

    public static final int UPDATE_PRODUCT_REQUEST_CODE = 4;
    public static final String DELETE = "com.example.salestrackertest3.DELETE";
    private ImageView productImage;
    private TextView productTitle;
    private TextView productDesc;
    private int productID;
    private ProductViewModel mProductViewModel;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_PRODUCT_REQUEST_CODE && resultCode == RESULT_OK){
            Product product = new Product();

            int productID = data.getIntExtra(NewProductActivity.EXTRA_PID, -1);
            if (productID == -1){
                Toast.makeText(this, "Update Failure", Toast.LENGTH_SHORT).show();
                return;
            }

            product.setImageUri(data.getStringExtra(NewProductActivity.EXTRA_IMAGEPATH));
            product.setProductName(data.getStringExtra(NewProductActivity.EXTRA_PTITLE));
            product.setDescription(data.getStringExtra(NewProductActivity.EXTRA_PDESC));
            product.setID(productID);

            mProductViewModel.update(product);

            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewProductActivity.EXTRA_PID, productID);
            setResult(RESULT_OK, replyIntent);

            fieldSetter(data);
            Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //Get the fields
        productImage = findViewById(R.id.product_image);
        productTitle = findViewById(R.id.product_title);
        productDesc = findViewById(R.id.product_desc);

        //Color for the action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00D8D8")));

        //Set the fields.
        fieldSetter(getIntent());

        
        //ViewModel Instance
        mProductViewModel =  new ViewModelProvider(this).get(ProductViewModel.class);

        //Get the buttons.
        Button updateButton = findViewById(R.id.button_update_product);
        Button deleteButton = findViewById(R.id.button_delete_product);

        updateButton.setOnClickListener(v -> {
            Intent updateIntent = new Intent(ProductDetailsActivity.this, NewProductActivity.class);

            updateIntent.putExtra(NewProductActivity.EXTRA_PID, productID);
            updateIntent.putExtra(NewProductActivity.EXTRA_IMAGEPATH, productImage.getTag().toString());
            updateIntent.putExtra(NewProductActivity.EXTRA_PTITLE, productTitle.getText());
            updateIntent.putExtra(NewProductActivity.EXTRA_PDESC, productDesc.getText());
            startActivityForResult(updateIntent, UPDATE_PRODUCT_REQUEST_CODE);
        });

        deleteButton.setOnClickListener(v -> {
            //Get the product that is being viewed at the moment.
            Product product = mProductViewModel.getProduct(productID);
            //Delete it.
            mProductViewModel.delete(product);
            //Inform the ProductFragment class about the change to delete the product from the search list as well.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewProductActivity.EXTRA_PID, productID);
            replyIntent.putExtra(DELETE, "delete");
            setResult(RESULT_OK, replyIntent);
            //Finish the activity.
            finish();
        });
    }

    private void fieldSetter(Intent intent) {
        productID = intent.getIntExtra(NewProductActivity.EXTRA_PID, -1);
        Glide.with(this)
                .load(Uri.parse(intent.getStringExtra(NewProductActivity.EXTRA_IMAGEPATH)))
                .into(productImage);
        productImage.setTag(intent.getStringExtra(NewProductActivity.EXTRA_IMAGEPATH));
        productTitle.setText(intent.getStringExtra(NewProductActivity.EXTRA_PTITLE));
        productDesc.setText(intent.getStringExtra(NewProductActivity.EXTRA_PDESC));
    }


}
