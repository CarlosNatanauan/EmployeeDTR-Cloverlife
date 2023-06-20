package com.mobiledev.employeedtr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceTracker {
    private DatabaseHelper dbHelper;

    public AttendanceTracker(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public EmployeeRecord processQRCode(String qrCodeData, boolean isTimeOut) {
        int employeeId = parseEmployeeId(qrCodeData);
        Employee employee = dbHelper.getEmployee(employeeId);
        if (employee == null) {
            // Employee not found in the database
            return null;
        }

        String currentDate = getCurrentDate();
        String currentTime = getCurrentTime();

        EmployeeRecord record;
        if(isTimeOut) {
            // If it's time-out, fetch the existing record and update the time-out value
            record = dbHelper.getEmployeeRecordByDate(employeeId, currentDate);
            if (record != null) {
                record.setTimeOut(currentTime);
                dbHelper.updateEmployeeRecord(record);
            }
        } else {
            // If it's time-in, create a new record
            record = new EmployeeRecord(0, employeeId, currentDate, currentTime, null);

        }
        return record;
    }


    private int parseEmployeeId(String qrCodeData) {
        // Implement your QR code parsing logic to extract the employee ID
        // For example, assume the QR code data is just the employee ID
        return Integer.parseInt(qrCodeData);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
