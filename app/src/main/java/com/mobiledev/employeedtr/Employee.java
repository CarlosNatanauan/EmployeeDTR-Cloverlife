package com.mobiledev.employeedtr;

import android.os.Parcel;
import android.os.Parcelable;

public class Employee implements Parcelable {
    private int id;
    private String name;
    private String address;
    private String email;
    private String contactNo;
    private double basicSalary;
    private String workingHoursIn;
    private String workingHoursOut;
    private String role;
    private String otEntitle;
    private String imageUri;


    public Employee() {
    }

    public Employee(int id, String name, String address, String email, String contactNo, double basicSalary,
                    String workingHoursIn, String workingHoursOut, String role, String otEntitle, String imageUri) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.contactNo = contactNo;
        this.basicSalary = basicSalary;
        this.workingHoursIn = workingHoursIn;
        this.workingHoursOut = workingHoursOut;
        this.role = role;
        this.otEntitle = otEntitle;
        this.imageUri = imageUri;
    }


    protected Employee(Parcel in) {
        id = in.readInt();
        name = in.readString();
        address = in.readString();
        email = in.readString();
        contactNo = in.readString();
        basicSalary = in.readDouble();
        workingHoursIn = in.readString();
        workingHoursOut = in.readString();
        role = in.readString();
        otEntitle = in.readString();
        imageUri = in.readString();
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeString(contactNo);
        dest.writeDouble(basicSalary);
        dest.writeString(workingHoursIn);
        dest.writeString(workingHoursOut);
        dest.writeString(role);
        dest.writeString(otEntitle);
        dest.writeString(imageUri);
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getContactNo() {
        return contactNo;
    }
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public double getBasicSalary() {
        return basicSalary;
    }
    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }
    public String getWorkingHoursIn() {
        return workingHoursIn;
    }
    public void setWorkingHoursIn(String workingHoursIn) {
        this.workingHoursIn = workingHoursIn;
    }
    public String getWorkingHoursOut() {
        return workingHoursOut;
    }
    public void setWorkingHoursOut(String workingHoursOut) {
        this.workingHoursOut = workingHoursOut;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOtEntitle() {
        return otEntitle;
    }

    public void setOtEntitle(String otEntitle) {
        this.otEntitle = otEntitle;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}