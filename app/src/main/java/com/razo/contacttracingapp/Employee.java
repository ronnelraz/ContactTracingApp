package com.razo.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.razo.contacttracingapp.R;

public class Employee extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}