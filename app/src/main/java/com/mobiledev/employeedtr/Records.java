package com.mobiledev.employeedtr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;


public class Records extends Fragment implements OnPageChangeListener, OnLoadCompleteListener {

    private TextView dateText;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy", Locale.US);
    private DatabaseHelper dbHelper;
    private PDFView pdfView;
    private String pdfPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EmployeeInfo/";
    private List<Employee> employeeList;
    private String currentPdfFileName;

    public Records() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        FButton btpdfdl = view.findViewById(R.id.btPdfDL1);
        FButton btpdfview = view.findViewById(R.id.btPdfVIEW1);

        // Set the color of the button
        btpdfdl.setButtonColor(Color.parseColor("#FFEEBB"));
        btpdfview.setButtonColor(Color.parseColor("#FFEEBB"));

        TextView selectDateText = view.findViewById(R.id.select_date_text);
        dateText = view.findViewById(R.id.date_text);
        pdfView = view.findViewById(R.id.pdfView);



        dbHelper = new DatabaseHelper(getActivity());

        Spinner employeeSpinner = view.findViewById(R.id.employee_spinner);
        employeeList = dbHelper.getAllEmployees();
        List<String> employeeNames = new ArrayList<>();
        employeeNames.add("Select Employee");

        for (Employee employee : employeeList) {
            employeeNames.add(employee.getName());
        }

        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, employeeNames);
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeSpinner.setAdapter(employeeAdapter);
        employeeSpinner.setSelection(0);

        employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;

                Employee selectedEmployee = employeeList.get(position - 1);

                List<EmployeeRecord> employeeRecords = dbHelper.getEmployeeRecords(selectedEmployee.getId());

                String selectedDate = dateText.getText().toString();

                boolean shouldShowPDF = true;
                for (EmployeeRecord record : employeeRecords) {
                    String recordDate = record.getDate();
                    String recordDateMonthYear = recordDate.substring(0, 7);
                    if (!recordDateMonthYear.equals(selectedDate)) {
                        shouldShowPDF = false;
                        break;
                    }
                }

                if (shouldShowPDF) {
                    createEmployeePDF(selectedEmployee);
                    String fileName = selectedEmployee.getName() + ".pdf";
                    loadEmployeePDF(fileName);
                    currentPdfFileName = fileName;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthYearPicker();
            }
        });

        btpdfview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfInExternalViewer();
            }
        });


        btpdfdl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPdf();
            }
        });


        return view;
    }


    private void showMonthYearPicker() {
        Calendar calendar = Calendar.getInstance();

        MonthYearPickerDialog monthYearPickerDialog = new MonthYearPickerDialog(
                getContext(),
                (datePicker, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);

                    // Formatting month to be 2 digits, i.e. "06" instead of "6"
                    String formattedMonth = String.format("%02d", month + 1);
                    String date = year + "-" + formattedMonth;
                    dateText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)
        );

        monthYearPickerDialog.show();
    }

    private void createEmployeePDF(Employee employee) {
        try {
            File dir = new File(pdfPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = (employee.getName() != null ? employee.getName() : "N/A") + ".pdf";
            File file = new File(dir, fileName);
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4); // Set page size to A4

            // Add company logo at the top
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.logo3);
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ImageData imageData = ImageDataFactory.create(stream.toByteArray());
            Image logo = new Image(imageData);
            logo.scaleToFit(300, 50); //

            // Create a new paragraph, add the logo to it, then set alignment
            Paragraph p = new Paragraph();
            p.add(logo);
            p.setTextAlignment(TextAlignment.CENTER);
            document.add(p);

            // Create employee details with larger, bold text
            Text name = new Text("Name: " + (employee.getName() != null ? employee.getName() : "N/A")).setFontSize(16).setBold();
            Text role = new Text("Role: " + (employee.getRole() != null ? employee.getRole() : "N/A")).setFontSize(16).setBold();
            Text basicSalary = new Text("Basic Salary: " + (employee.getBasicSalary() != 0.0 ? employee.getBasicSalary() : "N/A")).setFontSize(16).setBold();
            Text workingHours = new Text("Working Hours: " + (employee.getWorkingHoursIn() != null ? employee.getWorkingHoursIn() : "N/A") + " - " + (employee.getWorkingHoursOut() != null ? employee.getWorkingHoursOut() : "N/A")).setFontSize(16).setBold();
            Text otEntitle = new Text("OT Entitle: " + (employee.getOtEntitle() != null ? employee.getOtEntitle() : "N/A")).setFontSize(16).setBold();

            // Add the employee details to the document
            document.add(new Paragraph(name));
            document.add(new Paragraph(role));
            document.add(new Paragraph(basicSalary));
            document.add(new Paragraph(workingHours));
            document.add(new Paragraph(otEntitle));

            // Create a table with three columns
            float[] pointColumnWidths = {100F, 100F, 100F}; // Adjust column width values
            Table table = new Table(pointColumnWidths);

            // Set table to the center
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setMarginTop(20);  // Add space between the last paragraph and the table

            // Add headers with custom properties
            table.addHeaderCell(new Cell().add(new Paragraph("DATE").setFontColor(ColorConstants.WHITE).setBold()).setPadding(5).setBackgroundColor(new DeviceRgb(24, 92, 55)).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("TIME IN").setFontColor(ColorConstants.WHITE).setBold()).setPadding(5).setBackgroundColor(new DeviceRgb(24, 92, 55)).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("TIME OUT").setFontColor(ColorConstants.WHITE).setBold()).setPadding(5).setBackgroundColor(new DeviceRgb(24, 92, 55)).setTextAlignment(TextAlignment.CENTER));

            // Fetch the records from the database
            List<EmployeeRecord> employeeRecords = dbHelper.getEmployeeRecords(employee.getId());
            for (EmployeeRecord record : employeeRecords) {
                // Add rows with text centered
                table.addCell(new Cell().add(new Paragraph(record.getDate() != null ? record.getDate() : "N/A")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(record.getTimeIn() != null ? record.getTimeIn() : "N/A")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(record.getTimeOut() != null ? record.getTimeOut() : "N/A")).setTextAlignment(TextAlignment.CENTER));
            }

            // Add table to document
            document.add(table);

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEmployeePDF(String fileName) {
        pdfView.fromFile(new File(pdfPath, fileName))
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getActivity()))
                .load();
    }


    public static class MonthYearPickerDialog extends AlertDialog implements DialogInterface.OnClickListener {
        private DatePicker mDatePicker;
        private final DatePickerDialog.OnDateSetListener mDateSetListener;

        public MonthYearPickerDialog(Context context, DatePickerDialog.OnDateSetListener listener, int year, int month) {
            super(context);

            mDateSetListener = listener;

            setButton(BUTTON_POSITIVE, "OK", this);
            setButton(BUTTON_NEGATIVE, "Cancel", this);

            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.date_picker_dialog, null);
            setView(view);

            mDatePicker = view.findViewById(R.id.date_picker);
            mDatePicker.init(year, month, 1, null);
            mDatePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        }


        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == BUTTON_POSITIVE && mDateSetListener != null) {
                mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            }
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        // Do something when the PDF file is fully loaded
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        // Do something when page changed
    }
    private void openPdfInExternalViewer() {
        if (currentPdfFileName != null && !currentPdfFileName.isEmpty()) {
            File pdfFile = new File(pdfPath, currentPdfFileName);

            if (pdfFile.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", pdfFile);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void downloadPdf() {
        if (currentPdfFileName != null && !currentPdfFileName.isEmpty()) {
            File pdfFile = new File(pdfPath, currentPdfFileName);

            if (pdfFile.exists()) {
                String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                File outputFile = new File(downloadPath, currentPdfFileName);

                try (FileInputStream fis = new FileInputStream(pdfFile);
                     FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }

                    Toast.makeText(getActivity(), "File downloaded to " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed to download file", Toast.LENGTH_LONG).show();
                }
            }
        }
    }




}
