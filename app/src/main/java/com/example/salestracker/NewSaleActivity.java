package com.example.salestracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

public class NewSaleActivity extends AppCompatActivity {
    public static final String EXTRA_SID = "com.example.salestrackertest3.SID";
    public static final String EXTRA_SCUSTOMERID = "com.example.salestrackertest3.SCUSTOMERID";
    public static final String EXTRA_SPRODUCTID = "com.example.salestrackertest3.SPRODUCTID";
    public static final String EXTRA_SSTATUS = "com.example.salestrackertest3.SSTATUS";
    public static final String EXTRA_STITLE = "com.example.salestrackertest3.STITLE";
    public static final String EXTRA_SDESC = "com.example.salestrackertest3.SDESC";
    public static final String EXTRA_SDATE = "com.example.salestrackertest3.SDATE";
    public static final String EXTRA_SPAYDAY = "com.example.salestrackertest3.SPAYDAY";
    public static final String EXTRA_SCUSTOMER = "com.example.salestrackertest3.SCUSTOMER";
    public static final String EXTRA_SPRODUCT = "com.example.salestrackertest3.SPRODUCT";
    public static final String EXTRA_SPRICE = "com.example.salestrackertest3.SPRICE";

    private TextInputLayout mSaleTitle;
    private EditText mDate;
    private EditText mPayday;
    private TextInputLayout mSaleDescription;
    private TextInputLayout mPrice;
    private Spinner mCustomerSpinner;
    private Spinner mProductSpinner;
    private CustomerViewModel mCustomerViewModel;
    private ProductViewModel mProductViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale);
        mSaleTitle = findViewById(R.id.sale_title);
        mDate = findViewById(R.id.date);
        mPayday = findViewById(R.id.payday);
        mSaleDescription = findViewById(R.id.sale_description);
        mCustomerSpinner = findViewById(R.id.customer_spinner);
        mProductSpinner = findViewById(R.id.product_spinner);
        mPrice = findViewById(R.id.price);

        //Set click listeners for date inputing.
        mDate.setOnClickListener(v -> {
            //Setting the default date.
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(NewSaleActivity.this, (view, year1, month1, dayOfMonth) -> {
                month1++;
                mDate.setText(dayOfMonth + "/" + month1 + "/" + year1);
            }, year, month, day);

            dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", dpd);
            dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
            dpd.show();
        });

        mPayday.setOnClickListener(v -> {
            //Setting the default date.
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(NewSaleActivity.this, (view, year1, month1, dayOfMonth) -> {
                month1++;
                mPayday.setText(dayOfMonth + "/" + month1 + "/" + year1);
            }, year, month, day);

            dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", dpd);
            dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
            dpd.show();
        });

        mPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    mPrice.getEditText().removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String clean = s.toString().replaceAll(replaceable, "");

                    double price = Double.parseDouble(clean);
                    String formattedPrice = NumberFormat.getCurrencyInstance().format(price / 100);

                    current = formattedPrice;
                    mPrice.getEditText().setText(formattedPrice);
                    mPrice.getEditText().setSelection(formattedPrice.length());
                    mPrice.getEditText().addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00D8D8")));


        //Fetch customers as a list for the spinner.
        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        List<Customer> mCustomers = mCustomerViewModel.getmAllCustomersList();
        ArrayAdapter<Customer> cAdapter = new ArrayAdapter<Customer>(this, android.R.layout.simple_spinner_item, mCustomers);
        cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCustomerSpinner.setAdapter(cAdapter);

        //Fetch the products as a list for the spinner.
        mProductViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        List<Product> mProducts = mProductViewModel.getAllProductsList();
        ArrayAdapter<Product> pAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_spinner_item, mProducts);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductSpinner.setAdapter(pAdapter);

        Intent intent = getIntent();

        //Set the edittext fields with current values if the operation is an update.
        if (intent.hasExtra(EXTRA_SID)) {
            setTitle("Update Sale");
            mSaleTitle.getEditText().setText(intent.getStringExtra(EXTRA_STITLE));
            mDate.setText(intent.getStringExtra(EXTRA_SDATE));
            mPayday.setText(intent.getStringExtra(EXTRA_SPAYDAY));
            mSaleDescription.getEditText().setText(intent.getStringExtra(EXTRA_SDESC));
            mPrice.getEditText().setText(intent.getStringExtra(EXTRA_SPRICE));
            int customerID = intent.getIntExtra(EXTRA_SCUSTOMERID, -1);
            int productID = intent.getIntExtra(EXTRA_SPRODUCTID, -1);
            for (Customer customer : mCustomers) {
                if (customer.getID() == customerID) {
                    mCustomerSpinner.setSelection(cAdapter.getPosition(customer));
                    break;
                }
            }
            for (Product product: mProducts){
                if (product.getID() == productID){
                    mProductSpinner.setSelection(pAdapter.getPosition(product));
                    break;
                }
            }
        } else {
            setTitle("Create a New Sale");
        }
    }

    //Validation functions.
    private boolean validateSaleTitle(TextInputLayout saleTitle) {
        if (saleTitle.getEditText().getText().toString().trim().isEmpty()) {
            saleTitle.setErrorEnabled(true);
            saleTitle.setError("Please enter Sale Title.");
            return false;
        } else {
            saleTitle.setError(null);
            saleTitle.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateSaleDesc(TextInputLayout saleDesc) {
        if (saleDesc.getEditText().getText().toString().trim().isEmpty()) {
            saleDesc.setErrorEnabled(true);
            saleDesc.setError("Please enter Sale Details.");
            return false;
        } else {
            saleDesc.setError(null);
            saleDesc.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateSalePrice(TextInputLayout salePrice) {
        if (salePrice.getEditText().getText().toString().trim().isEmpty()) {
            salePrice.setErrorEnabled(true);
            salePrice.setError("Please enter sale price");
            return false;
        } else {
            salePrice.setError(null);
            salePrice.setErrorEnabled(false);
            return true;
        }
    }

    private void saveSale() {
        //Validations
        if (!validateSaleTitle(mSaleTitle) | !validateSaleDesc(mSaleDescription) | !validateSalePrice(mPrice)) {
            return;
        }
        else if(mDate.getText().toString().trim().isEmpty() || mPayday.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Please provide sale date and payday", Toast.LENGTH_SHORT).show();
        }
        else {
            String sTitle = mSaleTitle.getEditText().getText().toString();
            String sDate = mDate.getText().toString();
            String sPayday = mPayday.getText().toString();
            String sDesc = mSaleDescription.getEditText().getText().toString();
            Customer customer = (Customer) mCustomerSpinner.getSelectedItem();
            int sCustomer = customer.getID();
            Product product = (Product) mProductSpinner.getSelectedItem();
            int sProduct = product.getID();
            Double sPrice = Double.parseDouble(mPrice.getEditText().getText().toString().substring(1));


            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_STITLE, sTitle);
            replyIntent.putExtra(EXTRA_SDATE, sDate);
            replyIntent.putExtra(EXTRA_SPAYDAY, sPayday);
            replyIntent.putExtra(EXTRA_SDESC, sDesc);
            replyIntent.putExtra(EXTRA_SCUSTOMERID, sCustomer);
            replyIntent.putExtra(EXTRA_SCUSTOMER, mCustomerViewModel.getCustomerName(sCustomer));
            replyIntent.putExtra(EXTRA_SPRODUCTID, sProduct);
            replyIntent.putExtra(EXTRA_SPRODUCT, mProductViewModel.getProductName(sProduct));
            replyIntent.putExtra(EXTRA_SPRICE, sPrice);

            int saleID = getIntent().getIntExtra(EXTRA_SID, -1);
            if (saleID != -1) {
                replyIntent.putExtra(EXTRA_SID, saleID);
            }
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_sale_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_sale:
                saveSale();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
