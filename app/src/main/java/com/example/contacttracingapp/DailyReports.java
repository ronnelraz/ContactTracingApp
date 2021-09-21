package com.example.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.skydoves.powerspinner.PowerSpinnerView;
import com.skydoves.powerspinner.PowerSpinnerView_LifecycleAdapter;
import com.skydoves.powerspinner.SpinnerAnimation;
import com.skydoves.powerspinner.SpinnerGravity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyReports extends AppCompatActivity {

    @BindView(R.id.bu)
    Spinner spinnerView;
    List<String> bu = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_reports);
        ButterKnife.bind(this);
        setSpinnerView(spinnerView);
    }



    public void setSpinnerView(Spinner spinner){
        bu.add("All");
        bu.add("Feedmill");
        bu.add("Swine Farm");



        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,bu); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}