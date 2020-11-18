package com.example.myapplication;

import android.util.Log;

import java.lang.reflect.Array;

public class Painting {

    //Declare private variables
    private final String paintingID;
    private String location;
    private String locationType;
    private String rack;
    private final String artist;
    private final String title;
    private final String height;
    private final String width;
    private final String depth;


    /**
     *
     * @param paintingID ID associated with Nasher paintings
     * @param info Array containing information that charactarizes paintings
     */
    public Painting(String paintingID, String[] info){

        //Initialize private variables
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

    //Define accessor methods for painting charactaristics
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


    /**
     * Checks if the Object Painting ID contains the given keyword
     * @param paintingID keyword to check
     * @return True if true, False if false
     */
    public boolean containsPaintingID(String paintingID){
        //Log.d("Testing", nasher.searchID("20").toString());
        return this.paintingID.trim().toLowerCase().contains(paintingID.trim().toLowerCase());
    }

    /**
     * Checks if Object locationType contains the given keyword
     * @param locationType keyword to check
     * @return True if true, False if false
     */
    public boolean isLocationType(String locationType){
        return this.locationType.trim().toLowerCase().contains(locationType.trim().toLowerCase());
    }

    /**
     * Checks if Object rack is the same as the given keyword
     * @param rack keyword to check
     * @return True if true, False if false
     */
    public boolean isRack(String rack){
        try {
            Integer.parseInt(rack);
            if (rack.length() < 2)
                rack = '0'+rack;
        }
        catch (NumberFormatException E){
            if (rack.length() < 3)
                rack = '0'+rack;
        }

        return this.rack.toLowerCase().trim().equals(rack.toLowerCase().trim());
    }

    /**
     * Checks if Object artist contains the same as the given keyword
     * @param artist keyword
     * @return True if true, False if false
     */
    public boolean containsArtist(String artist){
        return this.artist.trim().toLowerCase().contains(artist.trim().toLowerCase());
    }

    /**
     * Checks if the Object title contains the given keyword
     * @param title keyword
     * @return True if true, False if false
     */
    public boolean containsTitle(String title){
        return this.title.trim().toLowerCase().contains(title.trim().toLowerCase());
    }

    /**
     * Edits the Object locationType to given parameter
     * @param locationType new locationType
     */
    public void changeLocationType (String locationType){
        this.locationType = locationType;
    }

    /**
     * Edits the Object rack to be given parameter
     * @param rack new rack
     */
    public void changeRack (String rack){
        this.rack = rack;
    }

    /**
     * Edits the location, locationType, rack to reflect given parameter
     * @param newLoc the new location to be updated to
     * @return 2 cell array where the first cell is new locationType and second is rack
     */
    public String[] changeLoc (String newLoc) {
        String[] returnVal = new String[2];
        if (newLoc.toLowerCase().trim().contains("wall")){
            changeLocationType("Wall Screen");
            returnVal[0] = "Wall Screen";
            for (String word: newLoc.split(" ")){
                try{
                    int a = Integer.parseInt(word);
                    changeRack(word);
                    returnVal[1] = word;
                    return returnVal;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            changeRack("None");
            returnVal[1] = "None";
            return returnVal;
        }
        for (String word: newLoc.trim().split(" ")){
            try {
                int a = Integer.parseInt(word);
                changeLocationType("Wall Screen");
                returnVal[0] = "Wall Screen";
                changeRack(word);
                returnVal[1] = word;
            } catch (NumberFormatException e) {
                try{
                    int b = Integer.parseInt(word.substring(0,word.length()-1));
                    changeLocationType("Screen");
                    returnVal[0] = "Screen";
                    changeRack(word);
                    returnVal[1] = word;
                } catch (NumberFormatException f) {
                    changeLocationType(newLoc);
                    returnVal[0] = newLoc;
                    changeRack("None");
                    returnVal[1] = "None";
                }
            }
        }

        updateLocation();
        return returnVal;
    }

    /**
     * Update locationType with proper formatting
     */
    private void updateLocation(){
        if (this.rack == "None"){
            this.location = this.locationType;
            return;
        }
        this.location = "Nasher Museum Building, Nasher Painting Storage Room, " + this.locationType + ", " + this.rack;
    }

    @Override
    public String toString() {
        return "Object: " + this.paintingID + " (" + this.title + " by " + this.artist + ")" +
                " is stored at " + this.location +
                ".\nIt is on " + this.locationType + ' ' + this.rack +
                ".\tIt has has dimensions as follows Height: " + this.height + "\tWidth: " + this.width +
                "\tDepth: "+ this.depth;
    }
}

/*
Other potentially useful statements


ImportDatabase.create(this,"nasher_clean_info.csv")
ImportDatabase.info.get(--paintinID--).getTitle();

searching for everything in a rack:

ImportDatabase.searchLocation(false,--stringwithrack--);
ImportDatabase.search(--keyword as a string--);

        */