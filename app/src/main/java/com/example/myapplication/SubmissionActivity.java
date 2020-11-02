package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SubmissionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Bub","Hello World");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        final Button send = (Button) this.findViewById(R.id.submitBtn);
        final EditText etPaintingID = (EditText) findViewById(R.id.etObjectID);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etArtist = (EditText) findViewById(R.id.etArtist);
        final EditText etOldLocation = (EditText) findViewById(R.id.etOldLocation);
        final EditText etNewLocation = (EditText) findViewById(R.id.etNewLocation);


        Intent intent = getIntent();
        final String paintingID = intent.getStringExtra("paintingID");
        final String rackID = intent.getStringExtra("rackID");

        String paintingName = ImportDatabase.info.get(paintingID).getTitle();
        String paintingArtist = ImportDatabase.info.get(paintingID).getArtist();
        String paintingLocation = ImportDatabase.info.get(paintingID).getLocation();

        etPaintingID.setText(paintingID);
        etName.setText(paintingName);
        etArtist.setText(paintingArtist);
        etOldLocation.setText(paintingLocation);
        etNewLocation.setText(rackID);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "New move for painting: " +  paintingName + "(" + etPaintingID.getText() + ") from " + paintingLocation + " to " + rackID + ".";

                Toast.makeText(getApplicationContext(),
                        "Email Sent!",
                        Toast.LENGTH_LONG).show();

                new MyTask().execute(message);
            }
        });
    }

    private class MyTask extends AsyncTask<String, Integer, String> {


        //Run in UI before background thread called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //runs in background
        @Override
        protected String doInBackground(String... params) {
            //get string from params
            String message = params[0];

            /* Start of sending email */

            try {


                GMailSender sender = new GMailSender("nasherstorage", "EGR101F20");
                sender.sendMail("This is Subject",
                        message,
                        "nasherstorage",
                        "shinyswampert123@gmail.com");

                Log.i("Send Mail", "Email Sent");


            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }


            /* End of sending email */
            return "postExe";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

}