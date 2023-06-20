    package com.mobiledev.employeedtr;
    
    public class EmployeeRecord {
        public int getRecordId() {
            return recordId;
        }
    
        public void setRecordId(int recordId) {
            this.recordId = recordId;
        }
    
        public int getEmployeeId() {
            return employeeId;
        }
    
        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }
    
        public String getDate() {
            return date;
        }
    
        public void setDate(String date) {
            this.date = date;
        }
    
        public String getTimeIn() {
            return timeIn;
        }
    
        public void setTimeIn(String timeIn) {
            this.timeIn = timeIn;
        }
    
        public String getTimeOut() {
            return timeOut;
        }
    
        public void setTimeOut(String timeOut) {
            this.timeOut = timeOut;
        }
    
        private int recordId;
        private int employeeId;
        private String date;
        private String timeIn;
        private String timeOut;
    
        public EmployeeRecord(int recordId, int employeeId, String date, String timeIn, String timeOut) {
            this.recordId = recordId;
            this.employeeId = employeeId;
            this.date = date;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
        }

    }