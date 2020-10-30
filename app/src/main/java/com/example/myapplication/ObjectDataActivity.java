package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ObjectDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_data);

        Intent intent = getIntent();
        String paintingID = intent.getStringExtra("result");


        final TextView ID = (TextView) findViewById(R.id.tvID);
        final TextView name = (TextView) findViewById(R.id.tvName);
        final TextView artist = (TextView) findViewById(R.id.tvArtist);
        final TextView location = (TextView) findViewById(R.id.tvLocation);

        ImportDatabase.create(this,"nasher_clean_info.csv");

        ID.setText(paintingID);
        name.setText(ImportDatabase.info.get(paintingID).getTitle());
        artist.setText(ImportDatabase.info.get(paintingID).getArtist());
        location.setText(ImportDatabase.info.get(paintingID).getLocation());

    }
}