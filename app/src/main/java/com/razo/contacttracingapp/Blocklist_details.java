package com.razo.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.retroConfig.API;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;

public class Blocklist_details extends AppCompatActivity {

    public static String str_id,str_name,str_category,str_company,str_plate,str_banType,str_start,str_end,str_remark;

    @BindViews({R.id.name,R.id.address,R.id.caetgory,R.id.plate})
    TextView[] view_details;

    @BindView(R.id.start)
    EditText startDate;
    @BindView(R.id.end) EditText endDate;
    final Calendar myCalendar = Calendar.getInstance();
    @BindView(R.id.blocktype)
    MaterialSpinner blocktype;
    ArrayAdapter banAdapter;
    List<String> listType = new ArrayList<>();
    List<String> listId = new ArrayList<>();
    @BindView(R.id.remark) EditText remark;
    @BindView(R.id.saveblock)
    MaterialButton saveblock;
    private String str_typeID = null;

    final Calendar calendar = Calendar.getInstance();
    function controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocklist_details);
        ButterKnife.bind(this);
        controller = new function(this);

        view_details[0].setText("Name : "+str_name);
        view_details[1].setText("Company : "+str_company);
        view_details[2].setText("Category : "+str_category);
        view_details[3].setText("Plate No. : "+str_plate);
        startDate.setText(str_start);
        endDate.setText(str_end);
        remark.setText(str_remark);



        API.getClient().GetBanTypes().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonObject.getBoolean("success");
                    JSONArray array = jsonObject.getJSONArray("data");

                    if(success){
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            listType.add(object.getString("type"));
                            listId.add(object.getString("id"));

                        }

                        banAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,listType);
                        banAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        blocktype.setAdapter(banAdapter);


                    }
                    else{

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });

        int pos = str_banType.equals("Trucking") ? 0 : 1;
        blocktype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                str_typeID = listId.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        Toast.makeText(this, pos+" " + str_banType, Toast.LENGTH_SHORT).show();
        BlockPerson();
    }


    @SuppressLint("ClickableViewAccessibility")
    public void BlockPerson(){
        startDate.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (startDate.getRight() - startDate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    String[] strdate = str_start.split("-");
                    int y = Integer.valueOf(strdate[0]);
                    int m = Integer.valueOf(strdate[1]);
                    int d = Integer.valueOf(strdate[2]);

                    new DatePickerDialog(v.getContext(),R.style.picker,cal_start(), y, m,
                            d).show();

                    return true;
                }
            }
            return false;
        });

        startDate.setOnClickListener(view -> {
            String[] strdate = str_start.split("-");
            int y = Integer.valueOf(strdate[0]);
            int m = Integer.valueOf(strdate[1]);
            int d = Integer.valueOf(strdate[2]);

            new DatePickerDialog(view.getContext(),R.style.picker,cal_start(), y, m,
                    d).show();
        });

        endDate.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (startDate.getRight() - startDate.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    String[] strdate = str_end.split("-");
                    int y = Integer.valueOf(strdate[0]);
                    int m = Integer.valueOf(strdate[1]);
                    int d = Integer.valueOf(strdate[2]);

                    new DatePickerDialog(v.getContext(),R.style.picker,cal_end(), y, m,
                            d).show();
                    return true;
                }
            }
            return false;
        });

        endDate.setOnClickListener(v -> {
            String[] strdate = str_end.split("-");
            int y = Integer.valueOf(strdate[0]);
            int m = Integer.valueOf(strdate[1]);
            int d = Integer.valueOf(strdate[2]);

            new DatePickerDialog(v.getContext(),R.style.picker,cal_end(), y, m,
                    d).show();
        });



        saveblock.setOnClickListener(v -> {
            String getstart = startDate.getText().toString();
            String getEnd = endDate.getText().toString();
            String getTypeID = str_typeID;
            String getRemark = remark.getText().toString();


            if(getstart.isEmpty()){
                startDate.performClick();
            }
            else if(getEnd.isEmpty()){
                endDate.performClick();
            }
            else if(getRemark.isEmpty()){
                remark.requestFocus();
                controller.toastip(R.raw.error_con,"Please Enter remark");
            }
            else{
                new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Are you sure you want to modify this person?")
                        .setConfirmText("Update")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
//                                Toast.makeText(v.getContext(), str_id +
//                                        getstart +
//                                        getEnd +
//                                        getTypeID +
//                                        getRemark +
//                                        controller.getAD(), Toast.LENGTH_SHORT).show();
                                controller.loading();
                                API.getClient().UpdateBanned(str_id,getstart,getEnd,getTypeID,getRemark,controller.getAD()).enqueue(new Callback<Object>() {
                                    @Override
                                    public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                            Boolean success = jsonObject.getBoolean("success");

                                            if(success){
                                                function.pDialog.dismissWithAnimation();
                                                sDialog.dismissWithAnimation();
                                                controller.toastip(R.raw.ok,"Updated Successfully");

                                            }
                                            else{
                                                function.pDialog.dismissWithAnimation();
                                                sDialog.dismissWithAnimation();
                                                controller.toastip(R.raw.error_con,"Sorry This person already banned");
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Object> call, Throwable t) {
                                        if(t instanceof IOException){
                                            controller.toastip(R.raw.error_con,t.getMessage());
                                        }
                                    }
                                });

                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();


            }

        });


    }


    private DatePickerDialog.OnDateSetListener cal_start(){
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Formatter_start();
        };
        return date;
    }

    private DatePickerDialog.OnDateSetListener cal_end(){
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Formatter_end();
        };
        return date;
    }


    private void Formatter_start() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(calendar.getTime()));
    }

    private void Formatter_end() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDate.setText(sdf.format(calendar.getTime()));
    }

    public void back(View view) {
        function.intent(Blocklist.class,view.getContext());
    }
}