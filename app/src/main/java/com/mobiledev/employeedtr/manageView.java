package com.mobiledev.employeedtr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class manageView extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the Toolbar
        getSupportActionBar().setTitle("View Employee");

        // Enable the home button as an up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = new DatabaseHelper(this);

        // Fetch the employee data from your database
        List<Employee> employees = dbHelper.getAllEmployees();

        // Assuming your ListView has the id "employeeListView"
        ListView employeeListView = findViewById(R.id.employeeListView);
        EmployeeNameAdapter adapter = new EmployeeNameAdapter(this, employees);
        employeeListView.setAdapter(adapter);

        employeeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Employee selectedEmployee = employees.get(position);
                Intent intent = new Intent(manageView.this, EmployeeInfo.class);
                intent.putExtra("selectedEmployee", selectedEmployee);
                startActivity(intent);
            }
        });
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