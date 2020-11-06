package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    ;@Override
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

        //Listener that runs when the login button is clicked
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //First checks if the ID put in the field is an existing ID
                if (users.get(etNetID.getText().toString()) != null)
                {
                    //Then checks if the Password field matches the field in the password database
                    if (users.get(etNetID.getText().toString())[0].equals(etPassword.getText().toString()))
                    {
                        //Creates an intent to open the MainActivity
                        Intent loginIntent = new Intent(LoginPageActivity.this, MainActivity.class);
                        //Tells this page to execute the intent
                        LoginPageActivity.this.startActivity(loginIntent);
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