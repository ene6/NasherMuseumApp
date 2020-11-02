package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ObjectDataActivity extends AppCompatActivity {

    String paintingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_data);

        Intent intent = getIntent();
        paintingID = intent.getStringExtra("result");

        Button moveButton = findViewById(R.id.btMoving);
        Button returnButton = findViewById(R.id.btReturning);

        final TextView ID = (TextView) findViewById(R.id.tvID);
        final TextView name = (TextView) findViewById(R.id.tvName);
        final TextView artist = (TextView) findViewById(R.id.tvArtist);
        final TextView location = (TextView) findViewById(R.id.tvLocation);

        ImportDatabase.create(this,"nasher_clean_info.csv");

        ID.setText(paintingID);
        name.setText(ImportDatabase.info.get(paintingID).getTitle());
        artist.setText(ImportDatabase.info.get(paintingID).getArtist());
        location.setText(ImportDatabase.info.get(paintingID).getLocation());

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanID();
            }
        });

    }

    private void scanID() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(PictureCapture.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX_TYPES);
        integrator.setPrompt("Scanning");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result:");
                builder.setPositiveButton("Rescan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanID();
                    }
                }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            Intent submissionIntent = new Intent(ObjectDataActivity.this, SubmissionActivity.class);
                                submissionIntent.putExtra("paintingID", paintingID);
                                submissionIntent.putExtra("rackID", result.getContents());

                            ObjectDataActivity.this.startActivity(submissionIntent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Toast.makeText(this, "No results", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}