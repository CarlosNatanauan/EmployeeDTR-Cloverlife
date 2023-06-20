package com.mobiledev.employeedtr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "employeeManager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_EMPLOYEES = "employees";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT_NO = "contact_no";
    private static final String KEY_BASIC_SALARY = "basic_salary";
    private static final String KEY_WORKING_HOURS_IN = "working_hours_in";
    private static final String KEY_WORKING_HOURS_OUT = "working_hours_out";
    private static final String KEY_ROLE = "role";
    private static final String KEY_OT_ENTITLE = "ot_entitle";
    private static final String KEY_IMAGE_URI = "image_uri"; // Change this line
    private static final String TABLE_EMPLOYEE_RECORDS = "EmployeeRecords";
    private static final String KEY_RECORD_ID = "record_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME_IN = "time_in";
    private static final String KEY_TIME_OUT = "time_out";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EMPLOYEES_TABLE = "CREATE TABLE " + TABLE_EMPLOYEES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_CONTACT_NO + " TEXT,"
                + KEY_BASIC_SALARY + " REAL,"
                + KEY_WORKING_HOURS_IN + " TEXT,"
                + KEY_WORKING_HOURS_OUT + " TEXT,"
                + KEY_ROLE + " TEXT,"
                + KEY_OT_ENTITLE + " TEXT,"
                + KEY_IMAGE_URI + " TEXT" + ")";
        db.execSQL(CREATE_EMPLOYEES_TABLE);



        String CREATE_EMPLOYEE_RECORDS_TABLE = "CREATE TABLE " + TABLE_EMPLOYEE_RECORDS + "("
                + KEY_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ID + " INTEGER,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME_IN + " TEXT,"
                + KEY_TIME_OUT + " TEXT,"
                + "FOREIGN KEY(" + KEY_ID + ") REFERENCES " + TABLE_EMPLOYEES + "(" + KEY_ID + "))";
        db.execSQL(CREATE_EMPLOYEE_RECORDS_TABLE);



    }

    public EmployeeRecord getEmployeeRecordByDate(int employeeId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EMPLOYEE_RECORDS,
                new String[]{KEY_RECORD_ID, KEY_ID, KEY_DATE, KEY_TIME_IN, KEY_TIME_OUT},
                KEY_ID + "=? AND " + KEY_DATE + "=?",
                new String[]{String.valueOf(employeeId), date},
                null, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        EmployeeRecord record = new EmployeeRecord(
                cursor.getInt(0),
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );

        cursor.close();
        db.close();

        return record;
    }

    public int updateEmployeeRecord(EmployeeRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME_OUT, record.getTimeOut());

        int rowsAffected = db.update(TABLE_EMPLOYEE_RECORDS, values, KEY_RECORD_ID + "=?",
                new String[]{String.valueOf(record.getRecordId())});
        db.close();

        return rowsAffected;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_RECORDS);
        onCreate(db);
    }


    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EMPLOYEES, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int nameIndex = cursor.getColumnIndex(KEY_NAME);
                int addressIndex = cursor.getColumnIndex(KEY_ADDRESS);
                int emailIndex = cursor.getColumnIndex(KEY_EMAIL);
                int contactNoIndex = cursor.getColumnIndex(KEY_CONTACT_NO);
                int basicSalaryIndex = cursor.getColumnIndex(KEY_BASIC_SALARY);
                int workingHoursInIndex = cursor.getColumnIndex(KEY_WORKING_HOURS_IN);
                int workingHoursOutIndex = cursor.getColumnIndex(KEY_WORKING_HOURS_OUT);
                int roleIndex = cursor.getColumnIndex(KEY_ROLE);
                int otEntitleIndex = cursor.getColumnIndex(KEY_OT_ENTITLE);
                int imageUriIndex = cursor.getColumnIndex(KEY_IMAGE_URI); // Change this line

                if (idIndex == -1 || nameIndex == -1 || addressIndex == -1 || emailIndex == -1 || contactNoIndex == -1 ||
                        basicSalaryIndex == -1 || workingHoursInIndex == -1 || workingHoursOutIndex == -1 ||
                        roleIndex == -1 || otEntitleIndex == -1 || imageUriIndex == -1) {
                    continue;
                }

                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String address = cursor.getString(addressIndex);
                String email = cursor.getString(emailIndex);
                String contactNo = cursor.getString(contactNoIndex);
                double basicSalary = cursor.getDouble(basicSalaryIndex);
                String workingHoursIn = cursor.getString(workingHoursInIndex);
                String workingHoursOut = cursor.getString(workingHoursOutIndex);
                String role = cursor.getString(roleIndex);
                String otEntitle = cursor.getString(otEntitleIndex);
                String imageUri = cursor.getString(imageUriIndex); // Change this line

                Employee employee = new Employee(id, name, address, email, contactNo, basicSalary, workingHoursIn, workingHoursOut, role, otEntitle, imageUri); // Change this line
                employees.add(employee);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return employees;
    }

    public List<EmployeeRecord> getEmployeeRecords(int employeeId) {
        List<EmployeeRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EMPLOYEE_RECORDS,
                new String[]{KEY_RECORD_ID, KEY_ID, KEY_DATE, KEY_TIME_IN, KEY_TIME_OUT},
                KEY_ID + "=?",
                new String[]{String.valueOf(employeeId)},
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                EmployeeRecord record = new EmployeeRecord(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                records.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return records;
    }




    public Employee getEmployee(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EMPLOYEES,
                new String[]{KEY_ID, KEY_NAME, KEY_ADDRESS, KEY_EMAIL, KEY_CONTACT_NO, KEY_BASIC_SALARY, KEY_WORKING_HOURS_IN, KEY_WORKING_HOURS_OUT, KEY_ROLE, KEY_OT_ENTITLE, KEY_IMAGE_URI},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Employee employee = new Employee(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getDouble(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10));

        cursor.close();
        db.close();

        return employee;
    }


    public long addEmployee(Employee employee) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, employee.getName());
        values.put(KEY_ADDRESS, employee.getAddress());
        values.put(KEY_EMAIL, employee.getEmail());
        values.put(KEY_CONTACT_NO, employee.getContactNo());
        values.put(KEY_BASIC_SALARY, employee.getBasicSalary());
        values.put(KEY_WORKING_HOURS_IN, employee.getWorkingHoursIn());
        values.put(KEY_WORKING_HOURS_OUT, employee.getWorkingHoursOut());
        values.put(KEY_ROLE, employee.getRole());
        values.put(KEY_OT_ENTITLE, employee.getOtEntitle());
        values.put(KEY_IMAGE_URI, employee.getImageUri()); // Change this line

        // Inserting the row
        long newRowId = db.insert(TABLE_EMPLOYEES, null, values);
        db.close();

        return newRowId;
    }


    public long addEmployeeRecord(EmployeeRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, record.getEmployeeId());
        values.put(KEY_DATE, record.getDate());
        values.put(KEY_TIME_IN, record.getTimeIn());
        values.put(KEY_TIME_OUT, record.getTimeOut());

        // Inserting Row
        long id = db.insert(TABLE_EMPLOYEE_RECORDS, null, values);
        db.close(); // Closing database connection

        // id will be -1 if there was an error
        return id;
    }


}