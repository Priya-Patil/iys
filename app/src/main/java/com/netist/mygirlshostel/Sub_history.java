package com.netist.mygirlshostel;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class Sub_history extends BaseActivity implements View.OnClickListener{

    public String name, rent;
    Timer refreshTimer;


    private String tag_string_req = "getdata";
    static String TAG = "ExelLog";
    ArrayList<HashMap<String, String>> chargesList;

    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_history);


        View writeButton = findViewById(R.id.write);
        writeButton.setOnClickListener(this);
        View readButton = findViewById(R.id.read);
        readButton.setOnClickListener(this);
        View writeExcelButton = findViewById(R.id.writeExcel);
        writeExcelButton.setOnClickListener(this);
        View readExcelButton = findViewById(R.id.readExcel);
        readExcelButton.setOnClickListener(this);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.write:
                saveFile(this,"myFile.txt");
                break;
            case R.id.read:
                readFile(this,"myFile.txt");
                break;
            case R.id.writeExcel:

                saveExcelFile(this,"myExcelpp.xls");
                Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_LONG).show();
                break;
            case R.id.readExcel:
              //  readExcelFile(this,"myExcel.xls");
              /*  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + HotelListChargesActivity.path);
                intent.setDataAndType(uri, "text/csv");
                startActivity(Intent.createChooser(intent, "Open folder"));*/

               /* File file = new File(Environment.getExternalStorageDirectory()+ HotelListChargesActivity.path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
                startActivity(intent);
*/

                try {
                    //  File file = new File(Environment.getExternalStorageDirectory()+ "storage/emulated/0/Android/data/com.netist.mygirlshostel/file/" + "hostel_charges");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("/storage/emulated/0/Android/data/com.netist.mygirlshostel/file/hostel_charges.xls"), "application/vnd.ms-excel");
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Application not available to open it" , Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private static boolean saveFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        PrintStream p = null; // declare a print stream object
        boolean success = false;

        try {
            OutputStream os = new FileOutputStream(file);
            // Connect print stream to the output stream
            p = new PrintStream(os);
            p.println("This is a TEST");
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != p)
                    p.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    private static void readFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        FileInputStream fis = null;

        try
        {
            File file = new File(context.getExternalFilesDir(null), filename);
            fis = new FileInputStream(file);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                Log.w("FileUtils", "File data: " + strLine);
                Toast.makeText(context, "File Data: " + strLine , Toast.LENGTH_SHORT).show();
            }
            in.close();
        }
        catch (Exception ex) {
            Log.e("FileUtils", "failed to load file", ex);
        }
        finally {
            try {if (null != fis) fis.close();} catch (IOException ex) {}
        }

        return;
    }

    private boolean saveExcelFile(Context context, String fileName) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        HSSFWorkbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myOrder");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Item Number");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Quantity");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Price");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));

        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;



        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            Toast.makeText(Sub_history.this,"Writing file"+file,Toast.LENGTH_LONG).show();
            success = true;
            os.close();
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
            Toast.makeText(getApplicationContext(),"IOException"+e,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
            Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    private static void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        try{
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.w("FileUtils", "Cell Value: " +  myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){e.printStackTrace(); }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
}
