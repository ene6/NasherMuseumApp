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

    /**
    * Initializing variables that store data.
    *
    * */
    private static HashMap<String, String[]> infoDict; //HashMaps with PaintingID (as key) and Array of painting charactaristics (as values)
    public static HashMap<String, Painting> info; //HashMaps with PaintingID (as key) and Painting object (as values)
    public static HashMap<String, List<String>> infoList; //HashMaps with PaintingID (as key) and ArrayList of painting charactaristics with formatting to show (as values)

    public static boolean updatePaintingCSV = false; //boolean variable checked at activity main to see if the csv needs to be updated with permanent changes.

    private static boolean initialized = false; //boolean variable that makes makes csv be read only once (when app is started) every time app is run.

    /**
     * Function to createCSV if there is none in the storage.
     * @param context
     * Context - for getting the internal storage path.
     * @param CSV_FILE_PATH
     * Name of file in the final directory.
     *
     */
    public static void createCSV(Context context, String CSV_FILE_PATH) {
        /*
         * Initializing permissions needed for writing to external storage (SD card)
         */
        //String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        //requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        // FOR EXTERNAL STORAGE: String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        // FOR INTERNAL STORAGE: String baseDir = context.getFilesDir().getAbsolutePath();

        /*
         * Initialize variables with internal path to storage + writer for csv
         */
        String baseDir = context.getFilesDir().getAbsolutePath();
        String filePath = baseDir + File.separator + CSV_FILE_PATH;
        File f = new File(filePath);
        CSVWriter writer;

        try {

            /*
             * do nothing if file exists (to ensure that changes are not written over)
             */
            if (f.exists() && !f.isDirectory()) {
                //f.delete();
                Log.d("test1", "EXISTS");
                return;
            }

            /*
             * If the file does not exist, this implies that the app has never been run before.
             * If app has never been run before, the CSV needs to be translated from assets folder to internal storage.
             */
            writer = new CSVWriter(new FileWriter(filePath));
            CSVReader csvReader = new CSVReader(new InputStreamReader(context.getAssets().open(CSV_FILE_PATH)));
            String[] values;
            Log.d("test1", "CREATED");
            while ((values = csvReader.readNext()) != null) {
                //Log.d("Output",Arrays.toString(values));
                writer.writeNext(values);
            }
            writer.close();


        }
        /*
         * catch to go with try.
         * Should never be triggered, otherwise something is very wrong.
         */

        catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            Log.d("test1", "Fail");
            Log.d("test1", e.toString());
        }

    }

    /**
     *Function to create load the HashMaps when main activity is called for the first time every time the app is run
     *
     * @param context
     * Context - for getting the internal storage path.
     * @param CSV_FILE_PATH
     * Name of file in the final directory.
     *
     */
    public static void create(Context context, String CSV_FILE_PATH) {
        /*
        Check if the HashMaps have been initialized.
         */
        if (!initialized) {

            /*
            change initialized to true - to signify that main method has been called before.
            create instances of the HashMaps.
            translate csv information to HashMaps.

             */
            initialized = true;
            infoDict = new HashMap<String, String[]>();
            info = new HashMap<String, Painting>();
            infoList = new HashMap<String, List<String>>();
            ArrayList<List<String>> records = new ArrayList<List<String>>();
            createCSV(context, CSV_FILE_PATH);
            try (CSVReader csvReader = new CSVReader(new FileReader(context.getFilesDir().getAbsolutePath() + File.separator + CSV_FILE_PATH))) {
                String[] values;
                while ((values = csvReader.readNext()) != null) {
                    records.add(Arrays.asList(values)); //stuff information into records
                }

                /*
                 *Fill out information in HashMaps from the records ArrayList.
                 */
                for (int i = 0; i < records.size(); i++) {
                    int[] a = {1, 2, 3, 4, 5, 6, 7, 8};
                    String[] vals = new String[a.length]; //information to create Painting objects.
                    for (int j = 0; j < vals.length; j++)
                        vals[j] = (String) records.get(i).toArray()[a[j]];
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

    /**
     * @return infoList
     */
    public static HashMap<String, List<String>> forList() {
        return infoList;
    }

    /**
     *
     * @param val
     * The key that you are looking for (Painting ID, Artist, Title, etc.)
     * @return
     * The index of the key in infoDict.
     */
    private static int findIndex(String val) { //for infoDic - now vestigial
        for (int i = 0; i < infoDict.get("Painting ID").length; i++) {
            if (infoDict.get("Painting ID")[i].equals(val))
                return i;
        }
        return -1;
    }


    /**
     * Search function to identify all PaintingIDs that contain the given keyword
     * @param keyword Part of the ID that the user is looking for.
     * @return ArrayList of search results
     */
    public static ArrayList<String> searchID(String keyword) { // only for Painting ID, Rack, Location type, Artist, Title
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key : info.keySet()) {
            if (info.get(key).containsPaintingID(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    /**
     * Function that uses helper function from painting object to search for all objects with locations that contain part of the keyword
     * @param rackType Displays whether it's a screen or wall screen
     * @param type Boolean, true if rackType is not "None"
     * @param num Dummy parameter
     * @param rack Rack number (if rackType is "None")
     * @return ArrayList of search results
     */
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

    /**
     * Overloaded version of previous function
     * @param rackType Displays whether it's a screen or wall screen
     * @param val Keyword to check
     * @return ArrayList of search results
     */
    public static ArrayList<String> searchLocation(boolean rackType, String val) {
        if (rackType)
            return searchLocation(val, true, false, "None");
        return searchLocation("None", false, true, val);
    }

    /**
     * returns an ArrayList of paintingIDs with Titles that contain the given keyword
     * @param keyword Keyword to check
     * @return ArrayList of search results
     */
    public static ArrayList<String> searchTitle(String keyword) {
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key : info.keySet()) {
            if (info.get(key).containsTitle(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    /**
     * returns an ArrayList of paintingIDs with Artists that contain the given keyword
     * @param keyword Keyword to check
     * @return ArrayList of search results
     */
    public static ArrayList<String> searchArtist(String keyword) {
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key : info.keySet()) {
            if (info.get(key).containsArtist(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    /**
     * Combines previous search functions to return an ArrayList that contains all the ArrayLists of the other search functions
     * @param keyword Keyword to check
     * @return ArrayList of search results
     */
    public static ArrayList<String> search(String keyword) {

        //Initialize ArrayLists and combine results from other search functions
        ArrayList<String> returnVal = new ArrayList<String>();
        returnVal.addAll(searchID(keyword));
        returnVal.addAll(searchTitle(keyword));
        returnVal.addAll(searchArtist(keyword));
        returnVal.addAll(searchLocation(true, keyword));

        //filtering for locationType searching
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


    /**
     * Edits location for painting objects in info HashMap and the information stored in the infoList HashMap
     * @param paintingID paintingID
     * @param newLock new Location to be inputted
     */
    public static void changeLoc(String paintingID, String newLock) {
        //Log.d("StartEcho", "Echo"); //echo printing

        //altering information in the HashMaps
        String[] type_rack = info.get(paintingID).changeLoc(newLock);
        infoList.get(paintingID).set(1, info.get(paintingID).getLocation());
        infoList.get(paintingID).set(2, type_rack[0]);
        infoList.get(paintingID).set(3, type_rack[1]);
        Log.d("ID", paintingID);
        Log.d("Line1", info.get(paintingID).getLocationType() + "\t" + info.get(paintingID).getRack());
        Log.d("Line2", infoList.get(paintingID).get(2) + "\t" + infoList.get(paintingID).get(3));
    }

    /**
     * Edits the CSV to make all changes permanent
     * @param context Context
     * @param CSV_FILE_PATH Filepath within getFilesDir()
     */
    public static void editCSVCell(Context context, String CSV_FILE_PATH){
        //Find path of storage
        String baseDir = context.getFilesDir().getAbsolutePath();
        String filePath = baseDir + File.separator + CSV_FILE_PATH;
        //File f = new File(filePath);
        //f.delete();

        //write changes into CSV
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
