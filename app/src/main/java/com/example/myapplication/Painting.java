package com.example.myapplication;

public class Painting {
    private String paintingID;
    private String locationType;
    private String rack;
    private String artist;
    private String title;
    private String height;
    private String width;
    private String depth;

    public Painting(String paintingID, String[] info){
        this.paintingID = paintingID.trim();
        this.locationType = info[0].trim();
        this.rack = info[1].trim();
        this.artist = info[2].trim();
        this.title = info[3].trim();
        this.height = info[4].trim();
        this.width = info[5].trim();
        this.depth = info[6].trim();
    }

    public String getPaintingID() {
        return paintingID;
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
}
