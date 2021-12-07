package com.example.salestracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class NewProductActivity extends AppCompatActivity {

    //Data names for the intents.
    public static final String EXTRA_PID = "com.example.salestrackertest3.PID";
    public static final String EXTRA_PTITLE = "com.example.salestrackertest3.PTITLE";
    public static final String EXTRA_PDESC = "com.example.salestrackertest3.PDESC";
    public static final String EXTRA_IMAGEPATH = "com.example.salestrackertest3.IMAGEPATH";

    //Request code for the gallery intent.
    public static final int PRODUCT_IMAGE_REQUEST_CODE = 2;

    //Activity components.
    private TextInputLayout mProductTitle;
    private TextInputLayout mProductDesc;
    private ImageView mProductImage;
    private Button mUploadImage;
    private Uri mProductImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        //Get the components by their Id.
        mProductTitle = findViewById(R.id.product_title);
        mProductDesc = findViewById(R.id.product_desc);
        mProductImage = findViewById(R.id.product_image);
        //This is the button that will start gallery intent.
        mUploadImage = findViewById(R.id.button_upload_image);


        //Set the close icon and color of the actionbar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00D8D8")));

        //Get the intent. We should have an intent if it is an update operation.
        Intent updateIntent = getIntent();

        //If the intent has Id as an extra, set the edittext fields with the current product info.
        if(updateIntent.hasExtra(EXTRA_PID)){
            /*THIS IS THE PART WHERE I COULD NOT SET THE IMAGEVIEW WITH THE CURRENT PRODUCT IMAGE*/
            setTitle("Update Product");
            mProductImageUri = Uri.parse(updateIntent.getStringExtra(EXTRA_IMAGEPATH));
            mProductTitle.getEditText().setText(updateIntent.getStringExtra(EXTRA_PTITLE));
            mProductDesc.getEditText().setText(updateIntent.getStringExtra(EXTRA_PDESC));
            /*Glide.with(this)
                    .load(mProductImageUri)
                    .into(mProductImage);*/

        }
        else{
            setTitle("New Product");
        }

        //Start gallery intent on button click.
        mUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a Product Image"), PRODUCT_IMAGE_REQUEST_CODE);
        });

    }

    //Receive the response from the gallery intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PRODUCT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            mProductImageUri = data.getData();
            Glide.with(this)
                    .load(data.getData())
                    .into(mProductImage);
        }
    }

    //Validation methods.
    private boolean validateProductTitle(TextInputLayout productTitle) {
        if (productTitle.getEditText().getText().toString().trim().isEmpty()) {
            productTitle.setErrorEnabled(true);
            productTitle.setError("Please enter Product Title.");
            return false;
        } else {
            productTitle.setError(null);
            productTitle.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateProductDesc(TextInputLayout productDesc) {
        if (productDesc.getEditText().getText().toString().trim().isEmpty()) {
            productDesc.setErrorEnabled(true);
            productDesc.setError("Please enter Product Description.");
            return false;
        } else {
            productDesc.setError(null);
            productDesc.setErrorEnabled(false);
            return true;
        }
    }

    private void saveProduct() {
        //If fields could not validated, display the error messages and return.
        if (!validateProductTitle(mProductTitle) | !validateProductDesc(mProductDesc)) {
            return;
        }
        //Make sure user picked an image from the gallery.
        else if(mProductImage == null){
            Toast.makeText(this, "Please choose a product image", Toast.LENGTH_SHORT).show();
            return;
        }

        //Else, get the values from the edittext fields first.
        else{
            String pTitle = mProductTitle.getEditText().getText().toString();
            String pDesc = mProductDesc.getEditText().getText().toString();
            String pImageUri = mProductImageUri.toString();


            //Pass the inputs as intent data.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_PTITLE, pTitle);
            replyIntent.putExtra(EXTRA_PDESC, pDesc);
            replyIntent.putExtra(EXTRA_IMAGEPATH, pImageUri);

            //If it was an update operation, pass the ProductId as well.
            int productID = getIntent().getIntExtra(EXTRA_PID, -1);
            if (productID != -1) {
                replyIntent.putExtra(EXTRA_PID, productID);
            }
            //Finish the intent.
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_product:
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
