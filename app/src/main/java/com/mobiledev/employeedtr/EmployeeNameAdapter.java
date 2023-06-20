package com.mobiledev.employeedtr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobiledev.employeedtr.R;

import java.util.List;

public class EmployeeNameAdapter extends ArrayAdapter<Employee> {
    private Context context;
    private List<Employee> employees;

    public EmployeeNameAdapter(Context context, List<Employee> employees) {
        super(context, R.layout.list_item_employee_name, employees);
        this.context = context;
        this.employees = employees;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_employee_name, null);

        TextView employeeName = view.findViewById(R.id.employeeName);
        Employee employee = employees.get(position);
        employeeName.setText(employee.getName());

        return view;
    }
}