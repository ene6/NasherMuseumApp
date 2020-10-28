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
    private HashMap<String, Painting> info;

    public ImportDatabase(Context context, String CSV_FILE_PATH){
        infoDict = new HashMap<String, String[]>();
        info = new HashMap<String, Painting>();
        ArrayList<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(context.getAssets().open(CSV_FILE_PATH)))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            for (int i = 0; i < records.size(); i++){
                int [] a = {1,2,3,4,5,6,7,8};
                String [] vals = new String [a.length];
                for (int j = 0 ; j < vals.length; j++)
                    vals[j] = (String) records.get(i).toArray()[a[j]];
                Log.d("help", new Painting((String)records.get(i).toArray()[0],vals).toString());
                infoDict.put((String)records.get(i).toArray()[0], vals);
                info.put((String)records.get(i).toArray()[0], new Painting((String)records.get(i).toArray()[0],vals));

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

    private int findIndex(String val){ //for infoDic - vestigial
        for (int i = 0; i < infoDict.get("Painting ID").length; i++){
           if (infoDict.get("Painting ID")[i].equals(val))
               return i;
        }
        return -1;
    }

    public ArrayList<String> searchID(String keyword){ // only for Painting ID, Rack, Location type, Artist, Title
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key: info.keySet()){
            if (info.get(key).containsPaintingID(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public ArrayList<String> searchLocation(String rackType, boolean type, boolean num, String rack){
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
        }
        else {
            for (String key : info.keySet()){
                if (info.get(key).isRack(rack.toLowerCase().trim()))
                    returnVal.add(key);
            }
        }
        return returnVal;
    }

    public ArrayList<String> searchLocation(boolean rackType, String val){
        if (rackType)
            return searchLocation(val, true, false, "None");
        return searchLocation("None", false, true, val);
    }

    public ArrayList<String> searchTitle(String keyword){
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key: info.keySet()){
            if (info.get(key).containsTitle(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public ArrayList<String> searchArtist(String keyword){
        ArrayList<String> returnVal = new ArrayList<String>();
        for (String key: info.keySet()){
            if (info.get(key).containsArtist(keyword.trim()))
                returnVal.add(key);
        }
        return returnVal;
    }

    public ArrayList<String> search(String keyword){
        ArrayList<String> returnVal = new ArrayList<String>();
        returnVal.addAll(searchID(keyword));
        returnVal.addAll(searchTitle(keyword));
        returnVal.addAll(searchArtist(keyword));

        if (keyword.toLowerCase().contains("screen")){
            for (int i = 0; i < keyword.split(" ").length; i++){
                if (keyword.split(" ")[i].toLowerCase().contains("screen")){
                    try {
                        returnVal.addAll(searchLocation(keyword.split(" ")[i],true,true,keyword.split(" ")[i+1]));
                    }
                    catch (IndexOutOfBoundsException e){
                        returnVal.addAll(searchLocation(true,keyword.split(" ")[i]));
                    }
                }
            }
        }
        else{
            for (String item: keyword.split(" ")){
                returnVal.addAll(searchLocation(false,item));
            }
        }

        return returnVal;
    }


}
