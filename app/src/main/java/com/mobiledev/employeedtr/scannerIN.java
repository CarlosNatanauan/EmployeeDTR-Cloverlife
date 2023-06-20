package com.mobiledev.employeedtr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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


public class scannerIN extends AppCompatActivity {
    private CodeScanner codeScanner;
    private DatabaseHelper databaseHelper;
    private AttendanceTracker attendanceTracker;

    private AlertDialog alertDialog;
    @Override



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_in);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final AttendanceTracker attendanceTracker = new AttendanceTracker(databaseHelper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the Toolbar
        getSupportActionBar().setTitle("Time IN");

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
                        // Process the scanned QR code and get the EmployeeRecord
                        final EmployeeRecord record = attendanceTracker.processQRCode(result.getText(), false); // Don't save it here

                        if (record != null) {
                            // Fetch the employee details from the database
                            final Employee employee = databaseHelper.getEmployee(record.getEmployeeId());



                            // Create and show the dialog
                            new AlertDialog.Builder(scannerIN.this)
                                    .setTitle("TIME IN")
                                    .setMessage("Name: " + employee.getName() + "\nDate: " + record.getDate() + "\nTime: " + record.getTimeIn())
                                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Add the record to the database HERE
                                            long newRowId = databaseHelper.addEmployeeRecord(record);

                                            if (newRowId == -1) {
                                                Toast.makeText(scannerIN.this, "Error saving record", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(scannerIN.this, "Record saved successfully", Toast.LENGTH_SHORT).show();
                                                finish();
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
                        } else {
                            Toast.makeText(scannerIN.this, "Invalid QR code", Toast.LENGTH_SHORT).show();
                        }
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
