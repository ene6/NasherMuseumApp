package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPageActivity extends AppCompatActivity {

    List<String[]> list = new ArrayList<String[]>();

    Map<String, String[]> users = new HashMap<String, String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Sets the xml file
        setContentView(R.layout.activity_loginpage);

        //Hides the action bar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}


        /*
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(this.getAssets().open("user.csv")));
        } catch (IOException e) {
            e.printStackTrace();
        } */

        //Connects button and textview variable to xml
        final Button btLogin = (Button) findViewById(R.id.btLogin);
        final EditText etNetID = (EditText) findViewById(R.id.etNetID);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        //Puts in the users
        users.put("ayc21", new String[]{"OCEAN", "Annabelle", "Chu"});
        users.put("dipster", new String[]{"OCEAN", "Alan", "Dippy"});
        users.put("brad", new String[]{"OCEAN", "Brad", "Johnson"});
        users.put("pwk2", new String[]{"OCEAN", "Patrick", "Krivacka"});
        users.put("ms402", new String[]{"OCEAN", "Michelle", "Seymour"});
        users.put("ecr33", new String[]{"OCEAN", "Ellen", "Raimond"});
        users.put("kw108", new String[]{"OCEAN", "Kelly", "Woolbright"});
        users.put("ln35", new String[]{"OCEAN", "Lee", "Nisbet"});
        users.put("az94", new String[]{"OCEAN", "Aaron", "Zalonis"});
        users.put("bbh12", new String[]{"OCEAN", "Bryan", "Hilley"});

        //Listener that runs when the login button is clicked
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Users.getInstance().setValue(etNetID.getText().toString().toLowerCase());
                //Log.d("TESTPLZWORK",Users.getInstance().getValue());

                //First checks if the ID put in the field is an existing ID
                if (users.get(etNetID.getText().toString().toLowerCase()) != null)
                {
                    //Then checks if the Password field matches the field in the password database
                    if (users.get(etNetID.getText().toString().toLowerCase())[0].equals(etPassword.getText().toString()))
                    {
                        //Creates an intent to open the MainActivity
                        Intent loginIntent = new Intent(LoginPageActivity.this, MainActivity.class);
                        //Tells this page to execute the intent
                        LoginPageActivity.this.startActivity(loginIntent);
                        finish();
                    }
                    else {
                        //Toast that tells the user that their password is incorrect
                        Toast.makeText(LoginPageActivity.this, "Password Incorrect", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    //Toast that tells the user that no such user with the specified ID exists
                    Toast.makeText(LoginPageActivity.this, "No such user", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}