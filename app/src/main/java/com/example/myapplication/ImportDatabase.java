package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ImportDatabase {

    private static HashMap<String, String[]> infoDict;
    public static HashMap<String, Painting> info;
    public static HashMap<String, List<String>> infoList;

    public static boolean updatePaintingCSV = false;

    private static boolean initialized = false;

    public static void createCSV(Context context, String CSV_FILE_PATH) {
        //String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        //requestPermissions(permissions,PERMISSION_REQUEST_CODE);

        // FOR EXTERNAL STORAGE: String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        // FOR INTERNAL STORAGE: String baseDir = context.getFilesDir().getAbsolutePath();
        String baseDir = context.getFilesDir().getAbsolutePath();;
        String filePath = baseDir + File.separator + CSV_FILE_PATH;
        File f = new File(filePath);
        CSVWriter writer;

        // File exist
        try {
            if (f.exists() && !f.isDirectory()) {
                //f.delete();
                Log.d("test1", "EXISTS");
                return;
            }
            writer = new CSVWriter(new FileWriter(filePath));
            CSVReader csvReader = new CSVReader(new InputStreamReader(context.getAssets().open(CSV_FILE_PATH)));
            String[] values;
            Log.d("test1", "CREATED");
            while ((values = csvReader.readNext()) != null) {
                //Log.d("Output",Arrays.toString(values));
                writer.writeNext(values);
            }
            writer.close();


        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            Log.d("test1", "Fail");
            Log.d("test1", e.toString());
        }

    }


    public static void create(Context context, String CSV_FILE_PATH) {
        if (!initialized) {
            initialized = true;
            infoDict = new HashMap<String, String[]>();
            info = new HashMap<String, Painting>();
            infoList = new HashMap<String, List<String>>();
            ArrayList<List<String>> records = new ArrayList<List<String>>();
            createCSV(context, CSV_FILE_PATH);
            try (CSVReader csvReader = new CSVReader(new FileReader(context.getFilesDir().getAbsolutePath() + File.separator + CSV_FILE_PATH))) {
                String[] values;
                while ((values = csvReader.readNext()) != null) {
                    records.add(Arrays.asList(values));
                }
                for (int i = 0; i < records.size(); i++) {
                    int[] a = {1, 2, 3, 4, 5, 6, 7, 8};
                    String[] vals = new String[a.length];
                    for (int j = 0; j < vals.length; j++)
                        vals[j] = (String) records.get(i).toArray()[a[j]];
                    //Log.d("help", new Painting((String)records.get(i).toArray()[0],vals).toString());
                    infoDict.put((String) records.get(i).toArray()[0], vals);
                    infoList.put((String) records.get(i).toArray()[0], records.get(i));
                    info.put((String) records.get(i).toArray()[0], new Painting((String) records.get(i).toArray()[0], vals));


                }
                //for (int i = 0 ; i < records.toArray().length)
            } catch (FileNotFoundException e) {
                Log.d("Output", String.valueOf(e));
            } catch (IOException e) {
                Log.d("Output", String.valueOf(e));
            } catch (CsvValidationException e) {
                Log.d("Output", String.valueOf(e));
            }
        }
    }

    public static HashMap<String, List<String>> forList() {
        return infoList;
    }

    private static int findIndex(String val) { //for infoDic - vestigial
        for (int i = 0; i < infoDict.get("Painting ID").length; i++) {
            if (infoDict.get("Painting ID")[i].equals(val))
                return i;
        }
        return -1;
    }

    public static ArrayList<String> searchID(String keyword) { // only for Painting ID, Rack, Location type, Artist, Title
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key : info.keySet()) {
            if (info.get(key).containsPaintingID(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public static ArrayList<String> searchLocation(String rackType, boolean type, boolean num, String rack) {
        ArrayList<String> returnVal = new ArrayList<String>();
        if (type) {
            for (String key : info.keySet()) {
                if (info.get(key).isLocationType(rackType.toLowerCase().trim())) {
                    if (rack.equals("None"))
                        returnVal.add(key);
                    else {
                        if (info.get(key).isRack(rack.toLowerCase().trim()))
                            returnVal.add(key);
                    }
                }
            }
        } else {
            for (String key : info.keySet()) {
                if (info.get(key).isRack(rack.toLowerCase().trim()))
                    returnVal.add(key);
            }
        }
        return returnVal;
    }

    public static ArrayList<String> searchLocation(boolean rackType, String val) {
        if (rackType)
            return searchLocation(val, true, false, "None");
        return searchLocation("None", false, true, val);
    }

    public static ArrayList<String> searchTitle(String keyword) {
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key : info.keySet()) {
            if (info.get(key).containsTitle(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public static ArrayList<String> searchArtist(String keyword) {
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key : info.keySet()) {
            if (info.get(key).containsArtist(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public static ArrayList<String> search(String keyword) {
        ArrayList<String> returnVal = new ArrayList<String>();
        returnVal.addAll(searchID(keyword));
        returnVal.addAll(searchTitle(keyword));
        returnVal.addAll(searchArtist(keyword));
        returnVal.addAll(searchLocation(true, keyword)); //go back and made this always searched through - also clean up all the redundancies in the searching algos

        if (keyword.toLowerCase().contains("screen")) {
            for (int i = 0; i < keyword.split(" ").length; i++) {
                if (keyword.split(" ")[i].toLowerCase().contains("screen")) {
                    try {
                        returnVal.addAll(searchLocation(keyword.split(" ")[i], true, true, keyword.split(" ")[i + 1]));
                    } catch (IndexOutOfBoundsException e) {
                        returnVal.addAll(searchLocation(true, keyword.split(" ")[i]));
                    }
                }
            }
        } else {
            for (String item : keyword.split(" ")) {
                returnVal.addAll(searchLocation(false, item));
            }
        }

        return returnVal;
    }

    public static void edit_CSV_Locations(String paintingID, String locationType, String rack) throws FileNotFoundException {
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream("help.csv")));
    }

    public static void changeLoc(String paintingID, String newLock) {
        Log.d("StartEcho", "Echo");
        String[] type_rack = info.get(paintingID).changeLoc(newLock);
        infoList.get(paintingID).set(1, info.get(paintingID).getLocation());
        infoList.get(paintingID).set(2, type_rack[0]);
        infoList.get(paintingID).set(3, type_rack[1]);
        Log.d("ID", paintingID);
        Log.d("Line1", info.get(paintingID).getLocationType() + "\t" + info.get(paintingID).getRack());
        Log.d("Line2", infoList.get(paintingID).get(2) + "\t" + infoList.get(paintingID).get(3));
    }

    public static void editCSVCell(Context context, String CSV_FILE_PATH){
        String baseDir = context.getFilesDir().getAbsolutePath();
        String filePath = baseDir + File.separator + CSV_FILE_PATH;
        //File f = new File(filePath);
        //f.delete();
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath, false));
            for (String id: info.keySet()){
                String[] data = {info.get(id).getPaintingID(), info.get(id).getLocation(), info.get(id).getLocationType(), info.get(id).getRack(), info.get(id).getArtist(), info.get(id).getTitle(), info.get(id).getHeight(), info.get(id).getWidth(), info.get(id).getDepth()};
                //Log.d("Input",Arrays.toString(data));
                writer.writeNext(data);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
