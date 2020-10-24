package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
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
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ImportDatabase{

    private HashMap<String, String[]> infoDict;

    public ImportDatabase(Context context, String CSV_FILE_PATH){
        infoDict = new HashMap<String, String[]>();
        ArrayList<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(context.getAssets().open(CSV_FILE_PATH)))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            for (int i = 0; i < records.size(); i++){
                int [] a = {2,3,4,5,6,7};
                String [] vals = new String [a.length];
                for (int j = 0 ; j < vals.length; j++)
                    vals[j] = (String) records.get(i).toArray()[a[j]];
                infoDict.put((String)records.get(i).toArray()[0], vals);

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

    private int findIndex(String val){ //for infoDic
        for (int i = 0; i < infoDict.get("Painting ID").length; i++){
           if (infoDict.get("Painting ID")[i].equals(val))
               return i;
        }
        return -1;
    }

    public ArrayList<String> searchID(String keyword){ // only for Painting ID, Rack, and
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key: infoDict.keySet()){
            if (key.toLowerCase().trim().contains(keyword.toLowerCase().trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public ArrayList<String> searchLocation(String rackType, String num){
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key: infoDict.keySet()){
            if (infoDict.get(key)[findIndex("Location Type")].toLowerCase().trim().equals(rackType.toLowerCase().trim())){
                if (num.equals("None"))
                    returnVal.add(key);
                else{
                    try{
                        Integer.parseInt(num);
                        if (num.length() <2)
                            num = '0'+num;
                    }
                    catch (NumberFormatException E){
                        if (num.length() < 3)
                            num = '0'+num;
                    }
                    if (infoDict.get(key)[findIndex("Rack")].toLowerCase().trim().equals(num.toLowerCase().trim()))
                        returnVal.add(key);
                }
            }
        }
        return returnVal;
    }

    public ArrayList<String> searchLocation(String rackType){
        return searchLocation(rackType, "None");
    }

    public ArrayList<String> searchTitle(String keyword){
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key: infoDict.keySet()){
            if (infoDict.get(key)[findIndex("Title")].toLowerCase().trim().contains(keyword.toLowerCase().trim()))
                returnVal.add(key);
        }
        return returnVal;
    }


}
