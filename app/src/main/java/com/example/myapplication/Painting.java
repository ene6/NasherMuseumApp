package com.example.myapplication;

import android.util.Log;

public class Painting {
    private String paintingID;
    private String location;
    private String locationType;
    private String rack;
    private String artist;
    private String title;
    private String height;
    private String width;
    private String depth;

    public Painting(String paintingID, String[] info){
        this.paintingID = paintingID.trim();
        this.location = info[0].trim();
        this.locationType = info[1].trim();
        this.rack = info[2].trim();
        this.artist = info[3].trim();
        this.title = info[4].trim();

        if (info[5].trim().length() > 0)
            this.height = info[5].trim();
        else
            this.height = "None";
        if (info[6].trim().length() > 0)
            this.width = info[6].trim();
        else
            this.width = "None";
        if (info[7].trim().length() > 0)
            this.depth = info[7].trim();
        else
            this.depth = "None";

    }

    public String getPaintingID() {
        return paintingID;
    }

    public String getLocation() {
        return location;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getRack() {
        return rack;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public String getDepth() {
        return depth;
    }

    public boolean containsPaintingID(String paintingID){
        //Log.d("Testing", nasher.searchID("20").toString());
        return this.paintingID.trim().toLowerCase().contains(paintingID.trim().toLowerCase());
    }

    public boolean isLocationType(String locationType){
        return locationType.trim().toLowerCase().equals(this.locationType.trim().toLowerCase());
    }

    public boolean isRack(String rack){
        try{
            Integer.parseInt(rack);
            if (rack.length() <2)
                rack = '0'+rack;
        }
        catch (NumberFormatException E){
            if (rack.length() < 3)
                rack = '0'+rack;
        }

        return this.rack.toLowerCase().trim().equals(rack.toLowerCase().trim());
    }

    public boolean containsArtist(String artist){
        return this.artist.trim().toLowerCase().contains(artist.trim().toLowerCase());
    }

    public boolean containsTitle(String title){
        return this.title.trim().toLowerCase().contains(title.trim().toLowerCase());
    }

    @Override
    public String toString() {
        return "Object: " + paintingID + " (" + title + " by " + artist + ")" +
                " is stored at " + location +
                ".\nIt is on " + locationType + ' ' + rack +
                ".\tIt has has dimensions as follows Height: " +height + "\tWidth: " + width +
                "\tDepth: "+ depth;
    }
}

/*

ImportDatabase.create(this,"nasher_clean_info.csv")
ImportDatabase.info.get(--paintinID--).getTitle();

searching for everything in a rack:

ImportDatabase.searchLocation(false,--stringwithrack--);
ImportDatabase.search(--keyword as a string--);

        */