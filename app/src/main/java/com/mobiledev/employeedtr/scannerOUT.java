package com.mobiledev.employeedtr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class scannerOUT extends AppCompatActivity {
    private CodeScanner codeScanner;
    private DatabaseHelper databaseHelper;

    private AttendanceTracker attendanceTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_in);

        databaseHelper = new DatabaseHelper(this);
        attendanceTracker = new AttendanceTracker(databaseHelper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the Toolbar
        getSupportActionBar().setTitle("Time OUT");

        // Enable the home button as an up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Process the QR code once and store the result in the record
                        EmployeeRecord record = attendanceTracker.processQRCode(result.getText(), true);

                        // If the record is null, show an error and return
                        if (record == null) {
                            Toast.makeText(scannerOUT.this, "No existing record found for the employee on this date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Fetch the employee details from the database
                        Employee employee = databaseHelper.getEmployee(record.getEmployeeId());

                        // Create and show the dialog
                        new AlertDialog.Builder(scannerOUT.this)
                                .setTitle("Time OUT")
                                .setMessage("Name: " + employee.getName() + "\nDate: " + record.getDate() + "\nTime: " + record.getTimeOut())
                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Update the record in the database
                                        int rowsAffected = databaseHelper.updateEmployeeRecord(record);

                                        if (rowsAffected > 0) {
                                            Toast.makeText(scannerOUT.this, "Record updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(scannerOUT.this, "Error updating record", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Scan", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Rescan the QR code
                                        codeScanner.startPreview();
                                    }
                                })
                                .show();
                    }
                });
            }
        });

    }

    // Handles the back button press in the Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finish the activity and return to the previous activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}
