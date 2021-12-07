package com.example.salestracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class CustomerDetailsActivity extends AppCompatActivity {

    private CustomerViewModel mCustomerViewModel;
    public static final int UPDATE_CUSTOMER_REQUEST_CODE = 3;
    public static final String DELETE = "com.example.salestrackertest3.DELETE";
    private TextView customerTitle;
    private TextView phoneNo;
    private TextView address;
    private int customerID;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the request as we expected,
        if (requestCode == UPDATE_CUSTOMER_REQUEST_CODE && resultCode == RESULT_OK) {
            //but ID could not received it means something went wrong. Return.
            if (customerID == -1) {
                Toast.makeText(this, "Update Failure", Toast.LENGTH_SHORT).show();
                return;
            }
            //Set the parameters for the customer constructor by receiving values from the intent response.
            String cTitle = data.getStringExtra(NewCustomerActivity.EXTRA_CTITLE);
            String cPhoneNo = data.getStringExtra(NewCustomerActivity.EXTRA_CPHONENO);
            String cAddress = data.getStringExtra(NewCustomerActivity.EXTRA_CADDRESS);
            //Set the customer.
            Customer customer = new Customer(cTitle, cPhoneNo, cAddress);
            customer.setID(customerID);
            //Update the customer.
            mCustomerViewModel.update(customer);
            //Pass customerID to CustomerFragment to use it in there to update search list as well.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewCustomerActivity.EXTRA_CID, customerID);
            setResult(RESULT_OK, replyIntent);
            //Set the textviews again to reflect the update;
            fieldSetter(data);
            //Feedback to user.
            Toast.makeText(this, "Customer Updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        //Get the text fields
        customerTitle = findViewById(R.id.customer_title);
        phoneNo = findViewById(R.id.customer_phoneNo);
        address = findViewById(R.id.customer_address);

        //Color for the action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00D8D8")));

        //Set the textfields with the data received from the customer fragment's intent.
        fieldSetter(getIntent());

        //CustomerViewModel instance.
        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        Button updateButton = findViewById(R.id.button_update_customer);
        Button deleteButton = findViewById(R.id.button_delete_customer);

        //On click to update button, pass the current data to the NewCustomerActivity class.
        updateButton.setOnClickListener(v -> {
            Intent updateIntent = new Intent(CustomerDetailsActivity.this, NewCustomerActivity.class);
            updateIntent.putExtra(NewCustomerActivity.EXTRA_CID, customerID);
            updateIntent.putExtra(NewCustomerActivity.EXTRA_CTITLE, customerTitle.getText());
            updateIntent.putExtra(NewCustomerActivity.EXTRA_CPHONENO, phoneNo.getText());
            updateIntent.putExtra(NewCustomerActivity.EXTRA_CADDRESS, address.getText());
            //Start the NewCustomerActivity with the update code.
            startActivityForResult(updateIntent, UPDATE_CUSTOMER_REQUEST_CODE);
        });

        deleteButton.setOnClickListener(v -> {
            //Get the customer that is being viewed at the moment.
            Customer customer = mCustomerViewModel.getCustomer(customerID);
            //Delete it.
            mCustomerViewModel.delete(customer);
            //Inform the CustomerFragment class about the change to delete the customer from the search list as well.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewCustomerActivity.EXTRA_CID, customerID);
            replyIntent.putExtra(DELETE, "delete");
            setResult(RESULT_OK, replyIntent);
            //Finish the activity.
            finish();

        });
    }

    //This method is used to set the fields on activity start and on update operation.
    private void fieldSetter(Intent intent) {
        customerID = intent.getIntExtra(NewCustomerActivity.EXTRA_CID, -1);
        customerTitle.setText(intent.getStringExtra(NewCustomerActivity.EXTRA_CTITLE));
        phoneNo.setText(intent.getStringExtra(NewCustomerActivity.EXTRA_CPHONENO));
        address.setText(intent.getStringExtra(NewCustomerActivity.EXTRA_CADDRESS));
    }
}
