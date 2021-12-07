package com.example.salestracker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.TypeConverters;

import android.Manifest;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SaleDetailsActivity extends AppCompatActivity {

    private SaleViewModel mSaleViewModel;
    private CustomerViewModel mCustomerViewModel;
    public static final int UPDATE_SALE_REQUEST_CODE = 3;
    public static final String DELETE = "com.example.salestrackertest3.DELETE";
    private TextView saleTitle;
    private TextView customerTitle;
    private TextView productTitle;
    private TextView date;
    private TextView payday;
    private TextView price;
    private TextView status;
    private TextView description;
    private int saleID;
    private int customerID;
    private int productID;
    Bitmap img, scaledimg;

    //As a result of activity, take the values of edittext fields from the NewSaleActivitys' intent to use them in update operation.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_SALE_REQUEST_CODE && resultCode == RESULT_OK) {
            Sale sale = new Sale();
            int saleID = data.getIntExtra(NewSaleActivity.EXTRA_SID, -1);

            //If Id of the existing field could not received, something went wrong.
            if (saleID == -1) {
                Toast.makeText(this, "Update Failure", Toast.LENGTH_SHORT).show();
                return;
            }
            sale.setTitle(data.getStringExtra(NewSaleActivity.EXTRA_STITLE));
            DateFormat inputFormatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                sale.setSaleDate(inputFormatter.parse(data.getStringExtra(NewSaleActivity.EXTRA_SDATE)));
                sale.setPayday(inputFormatter.parse(data.getStringExtra(NewSaleActivity.EXTRA_SPAYDAY)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sale.setDescription(data.getStringExtra(NewSaleActivity.EXTRA_SDESC));
            sale.setCustomerId(data.getIntExtra(NewSaleActivity.EXTRA_SCUSTOMERID, 0));
            sale.setProductId(data.getIntExtra(NewSaleActivity.EXTRA_SPRODUCTID, 0));
            sale.setPrice(data.getDoubleExtra(NewSaleActivity.EXTRA_SPRICE, 0.0));
            sale.setID(saleID);
            mSaleViewModel.update(sale);
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewSaleActivity.EXTRA_SID, saleID);
            setResult(RESULT_OK, replyIntent);
            //Set the textviews again to reflect the update;
            fieldSetter(data);

            Toast.makeText(this, "Sale Updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void fieldSetter(Intent intent) {
        saleTitle.setText(intent.getStringExtra(NewSaleActivity.EXTRA_STITLE));
        customerTitle.setText(intent.getStringExtra(NewSaleActivity.EXTRA_SCUSTOMER));
        productTitle.setText(intent.getStringExtra(NewSaleActivity.EXTRA_SPRODUCT));
        date.setText(intent.getStringExtra(NewSaleActivity.EXTRA_SDATE));
        payday.setText(intent.getStringExtra(NewSaleActivity.EXTRA_SPAYDAY));
        String formatPrice;
        if (intent.getDoubleExtra(NewSaleActivity.EXTRA_SPRICE, 0) % 1 == 0) {
            formatPrice = intent.getDoubleExtra(NewSaleActivity.EXTRA_SPRICE, 0) + "0";
            price.setText(formatPrice);
        } else {
            price.setText(String.valueOf(intent.getDoubleExtra(NewSaleActivity.EXTRA_SPRICE, 0)));
        }
        description.setText(intent.getStringExtra(NewSaleActivity.EXTRA_SDESC));
    }

    private void setStatus(TextView status, boolean payStatus) {
        status.setText(payStatus ? "Payed" : "Not Payed");
        status.setTextColor(payStatus ? Color.GREEN : Color.RED);
        if (payStatus)
            status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        else
            status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_red, 0, 0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details);
        saleTitle = findViewById(R.id.sale_title);
        customerTitle = findViewById(R.id.customer_title);
        productTitle = findViewById(R.id.sale_details_product_title);
        date = findViewById(R.id.date);
        payday = findViewById(R.id.payday);
        price = findViewById(R.id.price);
        status = findViewById(R.id.status);
        description = findViewById(R.id.sale_details_description);
        img = BitmapFactory.decodeResource(getResources(), R.drawable.header);
        scaledimg = Bitmap.createScaledBitmap(img, 1200, 800, false);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00D8D8")));

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS
        }, 1);

        mSaleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);
        mCustomerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);

        Button updateButton = findViewById(R.id.button_update_sale);
        Button checkPayedButton = findViewById(R.id.button_check_payed);
        Button  deleteButton = findViewById(R.id.button_delete_sale);
        Button PDFbutton = findViewById(R.id.button_generate_pdf);
        Button SMSbutton = findViewById(R.id.send_SMS);

        //Set the textviews with the current values
        fieldSetter(getIntent());
        boolean payStatus = getIntent().getBooleanExtra(NewSaleActivity.EXTRA_SSTATUS, false);
        setStatus(status, payStatus);

        //Get the IDs of a sale item.
        saleID = getIntent().getIntExtra(NewSaleActivity.EXTRA_SID, -1);
        customerID = getIntent().getIntExtra(NewSaleActivity.EXTRA_SCUSTOMERID, -1);
        productID = getIntent().getIntExtra(NewSaleActivity.EXTRA_SPRODUCTID, -1);

        //Adjust PDF structure and styling
        PDFbutton.setOnClickListener(v -> {
            Customer customer = mCustomerViewModel.getCustomer(customerID);

            PdfDocument billPdf = new PdfDocument();
            Paint paint = new Paint();
            Paint headTitle = new Paint();

            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(1200, 2000, 1).create();
            PdfDocument.Page myPage = billPdf.startPage(myPageInfo);
            Canvas canvas = myPage.getCanvas();
            canvas.drawBitmap(scaledimg, 0, 0, paint);
            headTitle.setTextAlign(Paint.Align.CENTER);
            headTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            headTitle.setTextSize(75);
            canvas.drawText("Clean Water INC.", 600, 200, headTitle);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(35f);
            paint.setColor(Color.BLACK);
            canvas.drawText("Customer Name: " + customer.getName(), 20, 830, paint);
            canvas.drawText("Phone Number: " + customer.getPhoneNo(), 20,880, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Sale Date: " + date.getText().toString(), 1180, 830, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRect(20, 950, 1180, 1025, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("Sale ID", 40,1000, paint);
            canvas.drawText("Product", 200,1000, paint);
            canvas.drawText("Price", 700,1000, paint);
            canvas.drawText("Payday", 900,1000, paint );

            canvas.drawText(String.valueOf(saleID), 40, 1100, paint);
            canvas.drawText(productTitle.getText().toString(), 200,1100, paint);
            canvas.drawText("$" + price.getText().toString(), 700,1100,paint);
            canvas.drawText(payday.getText().toString(), 900,1100, paint);

            billPdf.finishPage(myPage);

            File file = new File(getExternalFilesDir(null).getAbsolutePath(), "/Bill.pdf");

            try {
                billPdf.writeTo(new FileOutputStream(file));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            billPdf.close();
        });

        SMSbutton.setOnClickListener(v -> {
            Customer customer = mCustomerViewModel.getCustomer(customerID);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("smsto:" + customer.getPhoneNo()));
            String message = "Dear " + customer.getName() + ", we would like to remind you that" +
                    " payday for the sale with an ID " + saleID + " is " + payday.getText().toString() +
                    " Sale performed on the date "  + date.getText().toString() + ". \n CLEAN WATER INC";

            intent.putExtra("sms_body", message);
            startActivity(intent);
        });

        checkPayedButton.setOnClickListener(v -> {
            //Get the sale that is being viewew at the moment.
            Sale sale = mSaleViewModel.getSale(saleID);
            //Invert its status.
            sale.setStatus(!sale.isStatus());
            //Update it.
            mSaleViewModel.update(sale);
            //Reflect the changes in the UI.
            setStatus(status, sale.isStatus());
            //Inform the SaleFragment class about the change to update the search list as well.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewSaleActivity.EXTRA_SID, sale.getID());
            setResult(RESULT_OK, replyIntent);

        });

        deleteButton.setOnClickListener(v -> {
            //Get the sale that is being viewed at the moment.
            Sale sale = mSaleViewModel.getSale(saleID);
            //Delete it.
            mSaleViewModel.delete(sale);
            //Inform the SaleFragment class about the change to delete the sale from the search list as well.
            Intent replyIntent = new Intent();
            replyIntent.putExtra(NewSaleActivity.EXTRA_SID, saleID);
            replyIntent.putExtra(DELETE, "delete");
            setResult(RESULT_OK, replyIntent);
            //Finish the activity.
            finish();
        });

        //Send the current values of the sale to the NewSaleActivity to fill the edittext fields in there.
        updateButton.setOnClickListener(v -> {
            Intent updateIntent = new Intent(SaleDetailsActivity.this, NewSaleActivity.class);
            updateIntent.putExtra(NewSaleActivity.EXTRA_SID, saleID);
            updateIntent.putExtra(NewSaleActivity.EXTRA_SCUSTOMERID, customerID);
            updateIntent.putExtra(NewSaleActivity.EXTRA_SPRODUCTID, productID);
            updateIntent.putExtra(NewSaleActivity.EXTRA_STITLE, saleTitle.getText());
            updateIntent.putExtra(NewSaleActivity.EXTRA_SCUSTOMER, customerTitle.getText());
            updateIntent.putExtra(NewSaleActivity.EXTRA_SPAYDAY, payday.getText());
            updateIntent.putExtra(NewSaleActivity.EXTRA_SDATE, date.getText());
            updateIntent.putExtra(NewSaleActivity.EXTRA_SDESC, description.getText());
            updateIntent.putExtra(NewSaleActivity.EXTRA_SPRICE, price.getText());
            updateIntent.putExtra(NewSaleActivity.EXTRA_SSTATUS, status.getText());
            startActivityForResult(updateIntent, UPDATE_SALE_REQUEST_CODE);
        });
    }
}
