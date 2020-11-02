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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.UnsupportedEncodingException;

public class ObjectDataActivity extends AppCompatActivity {

    String paintingID;
    NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_data);

        Intent intent = getIntent();
        paintingID = intent.getStringExtra("result");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

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

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent submissionIntent = new Intent(ObjectDataActivity.this, SubmissionActivity.class);
                submissionIntent.putExtra("paintingID", paintingID);

                ObjectDataActivity.this.startActivity(submissionIntent);            }
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
                    Intent ObjectDataIntent = new Intent(ObjectDataActivity.this, SubmissionActivity.class);
                    ObjectDataIntent.putExtra("result", tagContent);
                    ObjectDataActivity.this.startActivity(ObjectDataIntent);
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



}