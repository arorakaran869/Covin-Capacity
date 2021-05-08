package com.dwitsolutions.cowincaptest.dao;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dwitsolutions.cowincaptest.R;

public class CenterList extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_list);

        mTextView = (TextView) findViewById(R.id.text);


    }
}