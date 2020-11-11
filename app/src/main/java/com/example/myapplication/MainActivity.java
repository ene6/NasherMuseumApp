package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    //Variable initialization
    Button btScan;
    Button btSearch;
    NfcAdapter nfcAdapter;
    TextView tvWelcome;
    TextView tvNFC;
    AnimationDrawable nfcAnimation;
    ImageView nfcImage;

    String fName;
    String lName;

    private static final String CSV_FILE_PATH = "nasher_clean_info.csv";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Database initialization
        ImportDatabase.create(this, CSV_FILE_PATH);
        if (ImportDatabase.updatePaintingCSV) {
            ImportDatabase.editCSVCell(this, CSV_FILE_PATH);
            ImportDatabase.updatePaintingCSV = false;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hides the action bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        //Gets user's name
        fName = Users.getInstance().getFirst();
        lName = Users.getInstance().getLast();

        //Connect button variable to xml
        btScan = findViewById(R.id.btScan);
        btSearch = findViewById(R.id.btSearch);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvNFC = findViewById(R.id.tvNFC);
        nfcImage = findViewById(R.id.ivAnimation);
        nfcImage.setBackgroundResource(R.drawable.nfc);

        //Sets the welcome text for the user
        tvWelcome.setText("Welcome " + fName + " " + lName +"!");

        //Checks if NFC is enabled or not on the phone. Sets up animations/pictures accordingly
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled())
        {
            nfcAnimation = (AnimationDrawable) nfcImage.getBackground();
            nfcAnimation.setExitFadeDuration(75);
            nfcAnimation.start();
            tvNFC.setText("NFC Scanner On!");
        }
        else
        {
            nfcImage.setBackgroundResource(R.drawable.scandisable);
            tvNFC.setText("NFC Disabled");

        }

        //Scan button that brings up the scanning activity
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanID();
            }
        });

        //Search button listener that goes to the search activity
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.putExtra("fName", fName);
                searchIntent.putExtra("lName", lName);
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
        if (result != null) {
            if (result.getContents() != null) {

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
            } else {
                Toast.makeText(this, "No results", Toast.LENGTH_LONG).show();
            }
        } else {
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

            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else {
                Toast.makeText(this, "Empty Tag? No text found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Reads the text from the nfc intent
    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {

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

        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
    }

    //Parses the string from the ndef record
    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
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

    public void parseString(String contents) {
        contents = contents.replaceAll("\\s+", "");

        if (contents.contains(".")) {
            try {
                ImportDatabase.info.get(contents).getTitle();
                Intent PaintingIntent = new Intent(MainActivity.this, ObjectDataActivity.class);
                PaintingIntent.putExtra("paintingID", contents);
                MainActivity.this.startActivity(PaintingIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Incorrect ID? No Painting Found", Toast.LENGTH_SHORT).show();
            }

        } else {
            Intent RackIntent = new Intent(MainActivity.this, SearchActivity.class);
            RackIntent.putExtra("rackID", contents);
            MainActivity.this.startActivity(RackIntent);
        }
    }

}
