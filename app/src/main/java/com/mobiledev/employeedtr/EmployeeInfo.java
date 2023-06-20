package com.mobiledev.employeedtr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import android.graphics.BitmapFactory;

import java.io.File;

public class EmployeeInfo extends AppCompatActivity {

    //Bitmap QR
    private Bitmap generateQRCode(String data, int width, int height) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_info);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the Toolbar
        getSupportActionBar().setTitle("Employee Info");

        // Enable the home button as an up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Employee employee = getIntent().getParcelableExtra("selectedEmployee");

        TextView infoID = findViewById(R.id.infoID);
        TextView infoEmpName = findViewById(R.id.infoEmpName);
        TextView infoAddress = findViewById(R.id.infoAddress);
        TextView infoEmail = findViewById(R.id.infoEmail);
        TextView infoContactNo = findViewById(R.id.infoContactNo);
        TextView infoBasicSalary = findViewById(R.id.infoBasicSalary);
        TextView infoTimeIN = findViewById(R.id.infoTimeIN);
        TextView infoTimeOUT = findViewById(R.id.infoTimeOUT);
        TextView infoEmpRole = findViewById(R.id.infoEmpRole);
        TextView infoOT = findViewById(R.id.infoOT);

        infoID.setText(String.valueOf(employee.getId()));
        infoEmpName.setText(employee.getName());
        infoAddress.setText(employee.getAddress());
        infoEmail.setText(employee.getEmail());
        infoContactNo.setText(employee.getContactNo());
        infoBasicSalary.setText(String.valueOf(employee.getBasicSalary()));
        infoTimeIN.setText(employee.getWorkingHoursIn());
        infoTimeOUT.setText(employee.getWorkingHoursOut());
        infoEmpRole.setText(employee.getRole());
        infoOT.setText(employee.getOtEntitle());



        // Generate QR code based on employee ID
        String employeeId = String.valueOf(employee.getId());
        int qrWidth = 400; // Set the width of the QR code
        int qrHeight = 400; // Set the height of the QR code
        Bitmap qrCodeBitmap = generateQRCode(employeeId, qrWidth, qrHeight);

        // Display the QR code in the qrInfo ImageView
        ImageView qrInfo = findViewById(R.id.qrInfo);
        if (qrCodeBitmap != null) {
            qrInfo.setImageBitmap(qrCodeBitmap);
        } else {
            // Show an error message or a placeholder image if the QR code could not be generated
            qrInfo.setImageResource(R.drawable.error);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finish the activity and return to the previous activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}