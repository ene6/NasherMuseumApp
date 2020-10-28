package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SubmissionActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Bub","Hello World");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        final Button send = (Button) this.findViewById(R.id.submitBtn);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MyTask().execute("This is the email message");
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
                        "chrisjknotek@gmail.com");
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