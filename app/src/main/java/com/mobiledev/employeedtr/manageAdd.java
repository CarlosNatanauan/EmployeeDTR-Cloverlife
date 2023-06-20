package com.mobiledev.employeedtr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.TimePickerDialog;

import java.util.Calendar;

import in.shadowfax.proswipebutton.ProSwipeButton;
import ir.waspar.persianedittext.PersianEditText;


import android.widget.ListPopupWindow; // Add this import at the be
import android.widget.Toast;

public class manageAdd extends AppCompatActivity {




    private PersianEditText employeeID;
    private PersianEditText employeeName;
    private PersianEditText address;
    private PersianEditText email;
    private PersianEditText contactNo;
    private PersianEditText basicSalary;
    private PersianEditText workingHoursIN;
    private PersianEditText workingHoursOUT;
    private PersianEditText empRole;
    private PersianEditText otEntitle;

    private DatabaseHelper dbHelper;
    private Employee employee;

    private ImageView employeePic;
    private ProSwipeButton btnAddEmployee;


    private static final int SELECT_IMAGE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_add);





        btnAddEmployee = findViewById(R.id.btnAddEmployee);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ProSwipeButton btnAddEmployee = findViewById(R.id.btnAddEmployee);

        // Set the title of the Toolbar
        getSupportActionBar().setTitle("Add Employee");

        // Enable the home button as an up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        employeeID = findViewById(R.id.employeeID);
        employeeName = findViewById(R.id.employeeName);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        contactNo = findViewById(R.id.contactNo);
        basicSalary = findViewById(R.id.basicSalary);
        workingHoursIN = findViewById(R.id.workingHoursIN);
        workingHoursOUT = findViewById(R.id.workingHoursOUT);
        empRole = findViewById(R.id.empRole);
        otEntitle = findViewById(R.id.otEntitle);


        btnAddEmployee.setOnSwipeListener(new ProSwipeButton.OnSwipeListener() {
            @Override
            public void onSwipeConfirm() {
                // Update the employee object with the current user inputs
                updateEmployeeFromInputs();

                // Insert the employee object into the database
                long newRowId = dbHelper.addEmployee(employee);

                if (newRowId != -1) {
                    Toast.makeText(manageAdd.this, "Employee added with ID: " + newRowId, Toast.LENGTH_SHORT).show();
                    // Finish the activity and return to the previous activity
                    finish();
                } else {
                    Toast.makeText(manageAdd.this, "Error adding employee", Toast.LENGTH_SHORT).show();
                    btnAddEmployee.showResultIcon(false); // Show an error icon
                    // Reset the ProSwipeButton after a delay
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnAddEmployee.setEnabled(false);
                        }
                    }, 1000);
                }
            }
        });


        dbHelper = new DatabaseHelper(this);
        employee = new Employee();
        updateEmployeeFromInputs();

        // time tick time in
        workingHoursIN = findViewById(R.id.workingHoursIN);

        workingHoursIN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Disable the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(manageAdd.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                            workingHoursIN.setText(formattedTime);
                        }
                    }, hour, minute, true);

                    timePickerDialog.show();

                    return true;
                }
                return false;
            }
        });
        // time tick time in

        // time tick time out
        workingHoursOUT = findViewById(R.id.workingHoursOUT);

        workingHoursOUT.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Disable the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(manageAdd.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                            workingHoursOUT.setText(formattedTime);
                        }
                    }, hour, minute, true);

                    timePickerDialog.show();

                    return true;
                }
                return false;
            }
        });
        // time tick time out

        // Employee role dropdown
        final PersianEditText empRole = findViewById(R.id.empRole);
        final String[] items = {"Operator", "Line Leader", "Staff"};

        // Create an ArrayAdapter to show the dropdown menu
        final ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.dropdown_item, parent, false);
                }
                textView = (TextView) convertView.findViewById(R.id.tv_dropdown_item);
                textView.setText(items[position]);
                return convertView;
            }
        };

        final ListPopupWindow popupWindow = new ListPopupWindow(this);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setAdapter(adapter);
        popupWindow.setModal(true);

        // Set the width of the ListPopupWindow after the layout has been drawn
        empRole.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                popupWindow.setWidth(empRole.getWidth());

                // Remove the listener to avoid multiple calls
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    empRole.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    empRole.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        // Show the dropdown menu when the PersianEditText is clicked
        empRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.setAnchorView(empRole);
                popupWindow.show();
            }
        });

        // Set the selected item text to the PersianEditText when an item is clicked
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                empRole.setText(items[position]);
                popupWindow.dismiss();
            }
        });
        // drop down for employee

        // drop down for OT
        // OT entitlement dropdown
        final PersianEditText otEntitle = findViewById(R.id.otEntitle);
        final String[] otItems = {"Yes", "No"};

        // Create an ArrayAdapter to show the dropdown menu
        final ListAdapter otAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, otItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.dropdown_item, parent, false);
                }
                textView = (TextView) convertView.findViewById(R.id.tv_dropdown_item);
                textView.setText(otItems[position]);
                return convertView;
            }
        };

        final ListPopupWindow otPopupWindow = new ListPopupWindow(this);
        otPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        otPopupWindow.setAdapter(otAdapter);
        otPopupWindow.setModal(true);

        // Set the width of the ListPopupWindow after the layout has been drawn
        otEntitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                otPopupWindow.setWidth(otEntitle.getWidth());

                // Remove the listener to avoid multiple calls
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    otEntitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    otEntitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        // Show the dropdown menu when the PersianEditText is clicked
        otEntitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otPopupWindow.setAnchorView(otEntitle);
                otPopupWindow.show();
            }
        });

        // Set the selected item text to the PersianEditText when an item is clicked
        otPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                otEntitle.setText(otItems[position]);
                otPopupWindow.dismiss();
            }
        });
        // drop down for OT


    }


    private void updateEmployeeFromInputs() {
        employee.setName(employeeName.getText().toString());
        employee.setAddress(address.getText().toString());
        employee.setEmail(email.getText().toString());
        employee.setContactNo(contactNo.getText().toString());
        employee.setBasicSalary(parseDoubleSafely(basicSalary.getText().toString()));
        employee.setWorkingHoursIn(workingHoursIN.getText().toString());
        employee.setWorkingHoursOut(workingHoursOUT.getText().toString());
        employee.setRole(empRole.getText().toString());
        employee.setOtEntitle(otEntitle.getText().toString());
    }



    //test database
    // for the clickable text and image

    private double parseDoubleSafely(String stringValue) {
        if (stringValue == null || stringValue.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(stringValue);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    // for the clickable text and image
    //test database

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