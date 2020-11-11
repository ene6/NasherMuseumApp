package com.example.myapplication;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private ExpandableListView expandableListView;
    private CustomExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        
        expandableListDetail = ImportDatabase.forList();
        for (String item: expandableListDetail.keySet()){
            if (!expandableListDetail.get(item).get(0).contains("Painting ID"))
                expandableListDetail.get(item).set(0,"Painting ID: " + expandableListDetail.get(item).get(0));
            if (!expandableListDetail.get(item).get(1).contains("Location"))
                expandableListDetail.get(item).set(1,"Location: " + expandableListDetail.get(item).get(1));
            if (!expandableListDetail.get(item).get(2).contains("Location Type / Notes"))
                expandableListDetail.get(item).set(2,"Location Type / Notes: " + expandableListDetail.get(item).get(2));
            if (!expandableListDetail.get(item).get(3).contains("Rack"))
                expandableListDetail.get(item).set(3,"Rack: " + expandableListDetail.get(item).get(3));
            if (!expandableListDetail.get(item).get(4).contains("Artist"))
                expandableListDetail.get(item).set(4,"Artist: " + expandableListDetail.get(item).get(4));
            if (!expandableListDetail.get(item).get(5).contains("Title"))
                expandableListDetail.get(item).set(5,"Title: " + expandableListDetail.get(item).get(5));
            if (!expandableListDetail.get(item).get(6).contains("Height"))
                expandableListDetail.get(item).set(6,"Height: " + expandableListDetail.get(item).get(6));
            if (!expandableListDetail.get(item).get(7).contains("Width"))
                expandableListDetail.get(item).set(7,"Width: " + expandableListDetail.get(item).get(7));
            if (!expandableListDetail.get(item).get(8).contains("Depth"))
                expandableListDetail.get(item).set(8,"Depth: " + expandableListDetail.get(item).get(8));
        }
        expandableListTitle = new ArrayList<String>(ImportDatabase.forList().keySet());
        
        searchView = (SearchView) findViewById(R.id.searchBar);

        searchView.setIconifiedByDefault(false);

        Intent intent = getIntent();
        final String rackID = intent.getStringExtra("rackID");


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        if (rackID != null && !rackID.isEmpty())
        {
            searchView.setQuery(rackID, true);
        }


        //expandAll();
        /*
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        }); */

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                /*
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show(); */

                Intent SubmissionIntent = new Intent(SearchActivity.this, SubmissionActivity.class);
                SubmissionIntent.putExtra("paintingID",expandableListTitle.get(groupPosition));
                SearchActivity.this.startActivity(SubmissionIntent);



                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void expandAll() {
        int count = expandableListAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            expandableListView.expandGroup(i);
        }
    }
    @Override
    public boolean onClose() {
        expandableListAdapter.filterData("");
        //expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        expandableListAdapter.filterData(query);
        //expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        expandableListAdapter.filterData(query);
        //searchView.setQuery(query, false);
        expandAll();
        return true;
    }
}
/*
    ArrayAdapter<String> arrayAdapter;
    List<String> results = new ArrayList<>();

    SearchView searchView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImportDatabase.create(this,"nasher_clean_info.csv");
        results = ImportDatabase.search("");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.searchBar);
        listView = (ListView) findViewById(R.id.listResults);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, results);
        listView.setAdapter(arrayAdapter);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
} */