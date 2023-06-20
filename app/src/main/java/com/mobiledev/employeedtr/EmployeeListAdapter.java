package com.mobiledev.employeedtr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledev.employeedtr.Employee;

import java.util.List;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.EmployeeViewHolder> {
    private List<Employee> employees;

    public EmployeeListAdapter(List<Employee> employees) {
        this.employees = employees;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.employee_list_item, parent, false);
        return new EmployeeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employees.get(position);
        holder.nameTextView.setText(employee.getName());
        holder.addressTextView.setText(employee.getAddress());
        holder.emailTextView.setText(employee.getEmail());
        holder.contactNoTextView.setText(employee.getContactNo());
        holder.basicSalaryTextView.setText(String.valueOf(employee.getBasicSalary()));
        holder.workingHoursInTextView.setText(employee.getWorkingHoursIn());
        holder.workingHoursOutTextView.setText(employee.getWorkingHoursOut());
        holder.roleTextView.setText(employee.getRole());
        holder.otEntitleTextView.setText(employee.getOtEntitle());

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView addressTextView;
        public TextView emailTextView;
        public TextView contactNoTextView;
        public TextView basicSalaryTextView;
        public TextView workingHoursInTextView;
        public TextView workingHoursOutTextView;
        public TextView roleTextView;
        public TextView otEntitleTextView;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            contactNoTextView = itemView.findViewById(R.id.contactNoTextView);
            basicSalaryTextView = itemView.findViewById(R.id.basicSalaryTextView);
            workingHoursInTextView = itemView.findViewById(R.id.workingHoursInTextView);
            workingHoursOutTextView = itemView.findViewById(R.id.workingHoursOutTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            otEntitleTextView = itemView.findViewById(R.id.otEntitleTextView);
        }
    }
}