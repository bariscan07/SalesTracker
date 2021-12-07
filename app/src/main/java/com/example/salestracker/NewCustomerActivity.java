package com.example.salestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.textfield.TextInputLayout;

public class NewCustomerActivity extends AppCompatActivity {

    //Data names for the intents.
    public static final String EXTRA_CID = "com.example.salestrackertest3.ID";
    public static final String EXTRA_CTITLE = "com.example.salestrackertest3.TITLE";
    public static final String EXTRA_CPHONENO = "com.example.salestrackertest3.PHONENO";
    public static final String EXTRA_CADDRESS = "com.example.salestrackertest3.ADDRESS";

    //Activity components.
    private TextInputLayout mCustomerTitle;
    private TextInputLayout mCustomerPhoneNo;
    private TextInputLayout mCustomerAddressLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);
        //Get the components by their Id.
        mCustomerTitle = findViewById(R.id.customer_title);
        mCustomerPhoneNo = findViewById(R.id.customer_phoneNo);
        mCustomerAddressLine = findViewById(R.id.customer_addressLine);

        //Set the close icon and color of the actionbar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00D8D8")));

        //Format the phone number input.
        mCustomerPhoneNo.getEditText().addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        //Get the intent. We should have an intent if it is an update operation.
        Intent updateIntent = getIntent();

        //If the intent has Id as an extra, set the edittext fields with the current customer info.
        if (updateIntent.hasExtra(EXTRA_CID)) {
            setTitle("Update Customer");
            mCustomerTitle.getEditText().setText(updateIntent.getStringExtra(EXTRA_CTITLE));
            mCustomerPhoneNo.getEditText().setText(updateIntent.getStringExtra(EXTRA_CPHONENO));
            mCustomerAddressLine.getEditText().setText(updateIntent.getStringExtra(EXTRA_CADDRESS));
        }
        //Else, it is an create operation so, just set the title.
        else {
            setTitle("New Customer");
        }
    }

    //Validation methods.
    private boolean validateCustomerTitle(TextInputLayout customerTitle) {
        if (customerTitle.getEditText().getText().toString().trim().isEmpty()) {
            customerTitle.setErrorEnabled(true);
            customerTitle.setError("Please enter Customer Title.");
            return false;
        } else {
            customerTitle.setError(null);
            customerTitle.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCustomerPhoneNo(TextInputLayout phoneNo) {
        if (phoneNo.getEditText().getText().toString().trim().isEmpty()) {
            phoneNo.setErrorEnabled(true);
            phoneNo.setError("Please enter Phone Number");
            return false;
        } else if (phoneNo.getEditText().getText().length() > 14) {
            phoneNo.setErrorEnabled(true);
            phoneNo.setError("Phone Number in wrong format");
            return false;
        } else {
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCustomerAddress(TextInputLayout customerAddress) {
        if (customerAddress.getEditText().getText().toString().trim().isEmpty()) {
            customerAddress.setErrorEnabled(true);
            customerAddress.setError("Please enter Address.");
            return false;
        } else {
            customerAddress.setError(null);
            customerAddress.setErrorEnabled(false);
            return true;
        }
    }

    private void saveCustomer() {
        //If fields could not validated, display the error messages and return.
        if (!validateCustomerTitle(mCustomerTitle) | !validateCustomerPhoneNo(mCustomerPhoneNo) | !validateCustomerAddress(mCustomerAddressLine)) {
            return;
        }
        //Else, get the values from the edittext fields first.
        else {
            String cTitle = mCustomerTitle.getEditText().getText().toString();
            String cPhoneNo = mCustomerPhoneNo.getEditText().getText().toString();
            String cAddress = mCustomerAddressLine.getEditText().getText().toString();

            //Pass the inputs as intent data.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_CTITLE, cTitle);
            replyIntent.putExtra(EXTRA_CPHONENO, cPhoneNo);
            replyIntent.putExtra(EXTRA_CADDRESS, cAddress);

            //If it was an update operation, pass the CustomerId as well.
            int customerID = getIntent().getIntExtra(EXTRA_CID, -1);
            if (customerID != -1) {
                replyIntent.putExtra(EXTRA_CID, customerID);
            }
            //Finish the intent.
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    //Place the save icon to the toolbar, top-right corner.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_customer_menu, menu);
        return true;
    }

    //On save icon click, call the function.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_customer:
                saveCustomer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
