package com.dwitsolutions.cowincaptest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwitsolutions.cowincaptest.R;
import com.dwitsolutions.cowincaptest.model.Center;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class CenterList extends AppCompatActivity {

    ListView listView;
    listAdapter listAdapter;
    Intent intent;
    String finalList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_list);

        listView = findViewById(R.id.listview);

        intent = getIntent();
        finalList = intent.getStringExtra("data");

        Log.d("TAG 37",finalList);



        try {
            String actualResponse = finalList;
            ObjectMapper mapper = new ObjectMapper();
            List<Center> centersList = mapper.readValue(actualResponse, new TypeReference<ArrayList<Center>>() {
            });
            Log.d("TAG 43",""+centersList.size());
            listAdapter = new listAdapter(getApplicationContext(),centersList);
            listView.setAdapter(listAdapter);
        }
        catch (Exception e)
        {
            Log.d("TAG exception",e.getMessage());
        }

      //


    }
}