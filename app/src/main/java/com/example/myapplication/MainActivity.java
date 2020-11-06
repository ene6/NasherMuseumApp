package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;

import static android.view.Window.FEATURE_NO_TITLE;

public class MainActivity extends AppCompatActivity{

    Button scanButton;
    Button searchButton;
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Database initialization
        ImportDatabase.create(this, "nasher_clean_info.csv");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        //Connect button variable to xml
        scanButton = findViewById(R.id.scan);
        searchButton = findViewById(R.id.searchPage);

        //Nfc Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Scan button that brings up the scanning activity
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanID();
            }
        });

        //Search button listener that goes to the search activity
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                MainActivity.this.startActivity(searchIntent);
            }
        });
    }

    //Starts scanning page
    private void scanID() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(PictureCapture.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX_TYPES);
        integrator.setPrompt("Scanning");
        integrator.initiateScan();
    }

    //Gets the result of the scanning page
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
                        parseString(result.getContents());

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

    //NFC commands for resuming if the program is interrupted
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    //NFC commands for pausing if the program is interrupted
    @Override
    protected void onPause() {

        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            //Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(parcelables != null && parcelables.length > 0)
            {
                readTextFromMessage((NdefMessage) parcelables[0]);
            }else{
                Toast.makeText(this, "Empty Tag? No text found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Reads the text from the nfc intent
    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            //Toast.makeText(this, tagContent, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(tagContent);
            builder.setTitle("Scanning Result:");
            builder.setPositiveButton("Rescan", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    parseString(tagContent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }else
        {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
    }

    //Parses the string from the ndef record
    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    public void parseString(String contents)
    {
        contents = contents.replaceAll("\\s+","");

        if (contents.contains("."))
        {
            try {
                ImportDatabase.info.get(contents).getTitle();
                Intent PaintingIntent = new Intent(MainActivity.this, ObjectDataActivity.class);
                PaintingIntent.putExtra("paintingID", contents);
                MainActivity.this.startActivity(PaintingIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Incorrect ID? No Painting Found", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
                Intent RackIntent = new Intent(MainActivity.this, SearchActivity.class);
                RackIntent.putExtra("rackID", contents);
                MainActivity.this.startActivity(RackIntent);
        }
    }

}
