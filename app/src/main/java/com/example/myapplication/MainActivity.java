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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button ;
    TextView paintingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //paintingID = (TextView) findViewById(R.id.textView);
        button = findViewById(R.id.scan);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        scanID();
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
        //paintingID.setText(result.getContents());

        if (result != null){
            if (result.getContents() != null){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);


                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanID();
                    }
                }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
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
