package com.example.contacttracingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.example.contacttracingapp.retroConfig.API;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyReports extends AppCompatActivity {

    function controller;
    @BindView(R.id.bu)
    Spinner spinnerView;
    List<String> bu = new ArrayList<>();
    List<String> buID = new ArrayList<>();
    ArrayAdapter buAdapter;


    @BindViews({R.id.to,R.id.from})
    EditText[] dateRange;
    DateRangeCalendarView calendar;
    MaterialButton calendarOk;
    //filter
    private CharSequence str_toSelected = null;
    private CharSequence str_fromSelected = null;
    private String str_plancode = "0";



    @BindView(R.id.month) Spinner monthSelect;
    List<String> list_month = new ArrayList<>();
    ArrayAdapter monthAdapter;
    @BindView(R.id.weeks) Spinner weeks;
    List<String> list_Weeks = new ArrayList<>();
    ArrayAdapter weeksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_reports);
        ButterKnife.bind(this);
        controller = new function(this);
        setSpinnerView(spinnerView);
        DateRange();

        Date date = new Date();
        CharSequence default_to = DateFormat.format("MMMM dd, yyyy ", date.getTime());
        CharSequence default_from = DateFormat.format("MMMM dd, yyyy ", date.getTime());
        dateRange[0].setText(default_to);
        dateRange[1].setText(default_from);


        String[] months = new DateFormatSymbols().getMonths();
        for (String month : months) {
            list_month.add(month);
        }


        monthAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,list_month);
        monthAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        monthSelect.setAdapter(monthAdapter);

        String TodayMonth = (String) DateFormat.format("MMMM", date.getTime());
        if (TodayMonth != null) {
            int spinnerPosition = monthAdapter.getPosition(TodayMonth);
            monthSelect.setSelection(spinnerPosition);
        }
        list_Weeks.add("1st Week");
        list_Weeks.add("2nd Week");
        list_Weeks.add("3rd Week");
        list_Weeks.add("4th Week");
        list_Weeks.add("5th Week");

        weeksAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,list_Weeks);
        weeksAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        weeks.setAdapter(weeksAdapter);


        DataTemp(default_to.toString(),default_from.toString(),"0");
        report_person(default_to.toString(),default_from.toString(),"0");
    }


    private void DateRange(){
        dateRange[0].setOnClickListener(v -> {
            CalendarPopup();

        });

        dateRange[1].setOnClickListener(v -> {
            CalendarPopup();
        });

    }

    @BindViews({R.id.avg,R.id.highest,R.id.lowest})
    TextView[] temp;
    private void DataTemp(String start,String end,String plantcode){
        API.getClient().Temp(start,end,plantcode).enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    Boolean success = jsonObject.getBoolean("success");
                    JSONArray array = jsonObject.getJSONArray("data");

                    if(success){
                        for(int i = 0; i < array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            temp[0].setText(object.getString("avg")+"\nAVG");
                            temp[1].setText(object.getString("highest")+"\nHighest");
                            temp[2].setText(object.getString("lowest")+"\nLowest");
                        }
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

    @BindViews({R.id.total_entry,R.id.total_person,R.id.total_entry_avg,R.id.trucking,R.id.agency,R.id.visitor,R.id.employee,R.id.male,R.id.avg_male_age,R.id.max_age,
    R.id.female,R.id.female_avg_age,R.id.female_max_age})
    TextView[] person;
    private void report_person(String start,String end,String plantcode){
        API.getClient().report_person(start,end,plantcode).enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    Boolean success = jsonObject.getBoolean("success");


                    if(success){
                        person[0].setText(jsonObject.getString("tol_entry"));
                        person[1].setText(jsonObject.getString("tol_person"));
                        person[2].setText(jsonObject.getString("avg"));
                        person[3].setText(jsonObject.getString("truck"));
                        person[4].setText(jsonObject.getString("agency"));
                        person[5].setText(jsonObject.getString("visitor"));
                        person[6].setText(jsonObject.getString("employee"));
                        person[7].setText(jsonObject.getString("male"));
                        person[8].setText(jsonObject.getString("male_avg_age"));
                        person[9].setText(jsonObject.getString("male_max_age"));
                        person[10].setText(jsonObject.getString("female"));
                        person[11].setText(jsonObject.getString("female_avg_age"));
                        person[12].setText(jsonObject.getString("female_max_age"));
                    }
                    else{

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


    private void CalendarPopup(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(DailyReports.this);
        View vs = LayoutInflater.from(DailyReports.this).inflate(R.layout.modal_date_range, null);
        calendar = vs.findViewById(R.id.calendar);
        calendarOk = vs.findViewById(R.id.ok);
        init(calendar);



        dialog.setView(vs);
        AlertDialog alert = dialog.create();
        calendarOk.setOnClickListener(v -> {
            String to = str_toSelected.toString();
            String from = str_fromSelected.toString();
            String plantcode = str_plancode;
            DataTemp(to,from,plantcode);
            report_person(to,from,plantcode);
            alert.dismiss();
        });
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(true);
        alert.show();
    }

    void init(DateRangeCalendarView calendar){
        calendar.setCalendarListener(new CalendarListener() {
            @Override
            public void onFirstDateSelected(@NonNull Calendar calendar) {
//                calendarOk.setEnabled(true);
//                str_toSelected = DateFormat.format("MMMM dd, yyyy ", calendar.getTime());
//                str_fromSelected = DateFormat.format("MMMM dd, yyyy ", calendar.getTime());
            }

            @Override
            public void onDateRangeSelected(@NonNull Calendar calendar, @NonNull Calendar calendar1) {
                calendarOk.setEnabled(true);
                str_toSelected = DateFormat.format("MMMM dd, yyyy ", calendar.getTime());
                str_fromSelected = DateFormat.format("MMMM dd, yyyy ", calendar1.getTime());
                dateRange[0].setText(DateFormat.format("MMMM dd, yyyy ", calendar.getTime()));
                dateRange[1].setText(DateFormat.format("MMMM dd, yyyy ", calendar1.getTime()));
            }
        });
    }





    public void setSpinnerView(Spinner spinner){

        API.getClient().AREAS().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonObject.getBoolean("success");
                    JSONArray array = jsonObject.getJSONArray("data");

                    if(success){
                        bu.add("All");
                        buID.add("0");
                        for(int i = 0; i < array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            bu.add(object.getString("plant_name"));
                            buID.add(object.getString("plant_code"));
                        }

                        buAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,bu);
                        buAdapter.setDropDownViewResource(android.R.layout
                                .simple_spinner_dropdown_item);
                        spinner.setAdapter(buAdapter);
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int indexOf = bu.indexOf(buAdapter.getItem(i));
                String to = dateRange[0].getText().toString();
                String from = dateRange[1].getText().toString();
                str_plancode = buID.get(indexOf);
                DataTemp(to,from,buID.get(indexOf));
                report_person(to,from,buID.get(indexOf));
               // Toast.makeText(view.getContext(), buID.get(indexOf) + " " + dateRange[0].getText().toString() + " " + dateRange[1].getText().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}