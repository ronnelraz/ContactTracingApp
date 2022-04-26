package com.razo.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.retroConfig.API;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;
import lecho.lib.hellocharts.view.PreviewColumnChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Monthly extends AppCompatActivity {

    public static final String[] colors = {"#1abc9c", "#3498db", "#9b59b6", "#34495e", "#f1c40f", "#e67e22", "#e74c3c", "#5f27cd", "#3dc1d3", "#f19066", "#6D214F", "#F8EFBA", "#00b894", "#cf6a87", "#786fa6", "#f3a683", "#d32f2f", "#C2185B", "#7B1FA2", "#512DA8", "#303F9F", "#1976D2", "#0288D1", "#0097A7", "#00796B", "#388E3C", "#689F38", "#AFB42B", "#FBC02D", "#FFA000", "#F57C00", "#E64A19", "#5D4037", "#616161", "#455A64", "#1B5E20"};
    public static final String[] monthName = {
      "January","February","March","April","May","June","July","August","September","October","November","December"
    };

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

    /*Weekly Report*/
    private ValueShape shape = ValueShape.CIRCLE;
    @BindView(R.id.monthly_person_entrance)
    ColumnChartView monthly_person_entrance;
    List<String> month = new ArrayList<>();
    List<Float> Monthlyvalue = new ArrayList<>();
    @BindViews({R.id.loading1,R.id.loading2,R.id.loading3,R.id.loading4})
    LottieAnimationView[] loading;
    int spinnerPosition = 0;
    int check = 0;
    int checkplantcode = 0;
    int checkplantcodes = 0;
    String SelectedMonth = null;

    /*Daily Entrance Report*/
    @BindView(R.id.dailyEntranceProvince)
    ColumnChartView chartBottom;
    @BindView(R.id.chart_preview)
    PreviewColumnChartView previewChart;
    List<String> province = new ArrayList<>();
    List<Float> provinceTotal = new ArrayList<>();


    /*group entrance*/
    @BindView(R.id.EntrannceGroup)
    PieChartView entranceGroup;
    List<String> groupPercent = new ArrayList<>();
    List<Double> groupPercentvalue = new ArrayList<>();
    List<Integer> groupTotal = new ArrayList<>();
    public boolean isExploded = false;
    @BindViews({R.id.groupEntranceColor,R.id.groupEntranceTitle,R.id.groupEntranceTotal,R.id.groupEntrancePercent}) LinearLayout[] groupEntranceDetail;


    @BindView(R.id.Agency)
    PieChartView entranceAgency;
    List<String> agencyPercent = new ArrayList<>();
    List<Double> agencyPercentvalue = new ArrayList<>();
    List<Integer> agencytotal = new ArrayList<>();
    @BindViews({R.id.agencyColor,R.id.agencyTitle,R.id.agencyTotal,R.id.agencyPercent}) LinearLayout[] agencyEntranceDetail;

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    final Calendar today = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);
        ButterKnife.bind(this);
        controller = new function(this);
        setSpinnerView(spinnerView);
        DateRange();


        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        Date date = new Date();
        CharSequence default_to = DateFormat.format("MMMM, yyyy", date.getTime());
        CharSequence default_from = DateFormat.format("MMMM, yyyy", date.getTime());
        CharSequence default_to_1st = DateFormat.format("MMMM, yyyy", date.getTime());
        CharSequence default_from_last = DateFormat.format("MMMM, yyyy", date.getTime());


        swipeRefreshLayout.setOnRefreshListener(() -> {
            loading[0].setVisibility(View.VISIBLE);
            loading[1].setVisibility(View.VISIBLE);
            loading[2].setVisibility(View.VISIBLE);
            loading[3].setVisibility(View.VISIBLE);
            //default
            DataTemp(default_to.toString(),default_from.toString(),"0");
            report_person(default_to.toString(),default_from.toString(),"0");
            weekly_persons_entrance("0");
            Monthly_people_Entrance_province(default_to_1st.toString(),default_from_last.toString(),"0");
            EntranceGroup(default_to.toString(),default_from.toString(),"0");
            EntrancAgency(default_to.toString(),default_from.toString(),"0");
            dateRange[0].setText(default_to);
            dateRange[1].setText(default_from);
            swipeRefreshLayout.setRefreshing(false);
        });


        DataTemp(default_to.toString(),default_from.toString(),"0");
        report_person(default_to.toString(),default_from.toString(),"0");
        weekly_persons_entrance("0");
        Monthly_people_Entrance_province(default_to_1st.toString(),default_from_last.toString(),"0");
        EntranceGroup(default_to.toString(),default_from.toString(),"0");
        EntrancAgency(default_to.toString(),default_from.toString(),"0");
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
            spinnerPosition = monthAdapter.getPosition(TodayMonth);
            SelectedMonth = TodayMonth;
            monthSelect.setSelection(spinnerPosition);
        }



        monthSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    if(++check > 1) {

                        Calendar dateweek = Calendar.getInstance();
                        int positionweek = dateweek.get(Calendar.WEEK_OF_MONTH)-1;
                        int indexOf = list_month.indexOf(monthAdapter.getItem(i));
                        String getMonthname = list_month.get(indexOf);
                        SelectedMonth = getMonthname;
                        spinnerPosition = indexOf;

                        Date date = new Date();
                        CharSequence year = DateFormat.format("yyyy", date.getTime());

                        String filterDate = getMonthname + " 01, " + year;
                        String Plantcode = str_plancode;
//                        Toast.makeText(view.getContext(), filterDate, Toast.LENGTH_SHORT).show();
                        loading[0].setVisibility(View.VISIBLE);
                        weekly_persons_entrance(Plantcode);
                    }

                }
                catch (Exception e){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(++checkplantcodes > 1) {
                    int indexOf = bu.indexOf(buAdapter.getItem(i));
                    String plantcode = buID.get(indexOf);
//                    Toast.makeText(getApplicationContext(), plantcode, Toast.LENGTH_SHORT).show();

                    Date date = new Date();
                    CharSequence default_to = DateFormat.format("MMMM, yyyy", date.getTime());
                    CharSequence default_from = DateFormat.format("MMMM, yyyy", date.getTime());
                    CharSequence default_to_1st = DateFormat.format("MMMM, yyyy", date.getTime());
                    CharSequence default_from_last = DateFormat.format("MMMM, yyyy", date.getTime());
                    //default
                    DataTemp(default_to.toString(),default_from.toString(),plantcode);
                    report_person(default_to.toString(),default_from.toString(),plantcode);
                    weekly_persons_entrance(plantcode);
                    Monthly_people_Entrance_province(default_to_1st.toString(),default_from_last.toString(),plantcode);
                    EntranceGroup(default_to.toString(),default_from.toString(),plantcode);
                    EntrancAgency(default_to.toString(),default_from.toString(),plantcode);

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }



    public void EntranceGroup(String to,String from,String plantcode){
        Handler handler = new Handler();

        handler.post(() -> {
            groupPercent.clear();
            groupPercentvalue.clear();
            groupTotal.clear();
            API.getClient().month_entrance_group(to,from,plantcode).enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        boolean success = jsonObject.getBoolean("success");
                        int count = jsonObject.getInt("count");
                        JSONArray array = jsonObject.getJSONArray("data");

                        if(success){
                            loading[2].setVisibility(View.GONE);
                            groupEntranceDetail[0].removeAllViews();
                            groupEntranceDetail[1].removeAllViews();
                            groupEntranceDetail[2].removeAllViews();
                            groupEntranceDetail[3].removeAllViews();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String string = object.getString("group");
                                double percent = object.getDouble("percent");
                                int total = object.getInt("Total");
                                groupPercent.add(string);
                                groupPercentvalue.add(percent);
                                groupTotal.add(total);
                            }

                            List<SliceValue> sliceValueList = new ArrayList();
                            for (int i = 0; i < count; i++) {
                                SliceValue sliceValue = new SliceValue(groupPercentvalue.get(i).floatValue(), Color(i));
                                sliceValue.setLabel(groupPercentvalue.get(i) + "%");
                                sliceValue.getLabelAsChars();
                                sliceValue.getDarkenColor();
                                sliceValueList.add(sliceValue);
                                ImageView imageView = new ImageView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 50);
                                layoutParams.setMargins(0, 0, 0, 25);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setBackgroundColor(Color(i));
                                groupEntranceDetail[0].addView(imageView);
                                TextView textView = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, 50);
                                layoutParams2.setMargins(0, 0, 0, 25);
                                textView.setLayoutParams(layoutParams2);
                                textView.setText(groupPercent.get(i));
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                textView.setGravity(21);
                                textView.setTextSize(11.0f);
                                groupEntranceDetail[1].addView(textView);
                                TextView textView2 = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, 50);
                                layoutParams3.setMargins(0, 0, 0, 25);
                                textView2.setLayoutParams(layoutParams3);
                                textView2.setText(controller.formatter(groupTotal.get(i).toString()));
                                textView2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                textView2.setGravity(21);
                                textView2.setTextSize(11.0f);
                                groupEntranceDetail[2].addView(textView2);
                                TextView textView3 = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-1, 50);
                                layoutParams4.setMargins(0, 0, 0, 25);
                                textView3.setLayoutParams(layoutParams4);
                                textView3.setText(groupPercentvalue.get(i) + "%");
                                textView3.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                textView3.setGravity(21);
                                textView3.setTextSize(11.0f);
                                groupEntranceDetail[3].addView(textView3);

                                entranceGroup.setOnValueTouchListener(new PieChartOnValueSelectListener() {
                                    @Override
                                    public void onValueSelected(int arcIndex, SliceValue value) {
                                        Toast.makeText(getApplicationContext(), "Group Name : "+groupPercent.get(arcIndex)+" Total : "+groupTotal.get(arcIndex), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onValueDeselected() {

                                    }
                                });


                            }
                            PieChartData data = new PieChartData(sliceValueList);
                            data.setHasLabels(true);
                            data.setHasLabelsOnlyForSelected(false);
                            data.setHasLabelsOutside(false);
                            data.setHasCenterCircle(true);
                            if (isExploded) {
                                data.setSlicesSpacing(24);
                            }
                            data.setCenterText1("Percentage By Entrance Group");
                            data.setCenterText1FontSize(14);
                            data.setCenterText1Color(Color.parseColor("#2c3e50"));
                            data.setAxisYLeft(new Axis().setHasLines(false).setMaxLabelChars(3).setHasSeparationLine(true));
                            data.setValues(sliceValueList);
                            data.setSlicesSpacing(2);
                            entranceGroup.setPieChartData(data);



                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });

        });



    }

    public void EntrancAgency(String to,String from,String plantcode){
        Handler handler = new Handler();

        handler.post(() -> {
            agencyPercent.clear();
            agencyPercentvalue.clear();
            agencytotal.clear();
            API.getClient().month_entrance_agency(to,from,plantcode).enqueue(new Callback<Object>() {

                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        boolean success = jsonObject.getBoolean("success");
                        int count = jsonObject.getInt("count");
                        JSONArray array = jsonObject.getJSONArray("data");

                        if(success){
                            loading[3].setVisibility(View.GONE);
                            agencyEntranceDetail[0].removeAllViews();
                            agencyEntranceDetail[1].removeAllViews();
                            agencyEntranceDetail[2].removeAllViews();
                            agencyEntranceDetail[3].removeAllViews();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String string = object.getString("group");
                                double percent = object.getDouble("percent");
                                int total = object.getInt("Total");
                                agencyPercent.add(string);
                                agencyPercentvalue.add(percent);
                                agencytotal.add(total);
                            }

                            List<SliceValue> sliceValueList = new ArrayList();
                            for (int i = 0; i < count; i++) {
                                SliceValue sliceValue = new SliceValue(agencyPercentvalue.get(i).floatValue(), Color(i));
                                sliceValue.setLabel(agencyPercentvalue.get(i) + "%");
                                sliceValue.getLabelAsChars();
                                sliceValue.getDarkenColor();
                                sliceValueList.add(sliceValue);
                                ImageView imageView = new ImageView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 50);
                                layoutParams.setMargins(0, 0, 0, 25);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setBackgroundColor(Color(i));
                                agencyEntranceDetail[0].addView(imageView);
                                TextView textView = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, 50);
                                layoutParams2.setMargins(0, 0, 0, 25);
                                textView.setLayoutParams(layoutParams2);
                                textView.setText(agencyPercent.get(i));
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                                textView.setGravity(21);
                                textView.setTextSize(11.0f);
                                agencyEntranceDetail[1].addView(textView);
                                TextView textView2 = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, 50);
                                layoutParams3.setMargins(0, 0, 0, 25);
                                textView2.setLayoutParams(layoutParams3);
                                textView2.setText(controller.formatter(agencytotal.get(i).toString()));
                                textView2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                textView2.setGravity(21);
                                textView2.setTextSize(11.0f);
                                agencyEntranceDetail[2].addView(textView2);
                                TextView textView3 = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-1, 50);
                                layoutParams4.setMargins(0, 0, 0, 25);
                                textView3.setLayoutParams(layoutParams4);
                                textView3.setText(agencyPercentvalue.get(i) + "%");
                                textView3.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                textView3.setGravity(21);
                                textView3.setTextSize(11.0f);
                                agencyEntranceDetail[3].addView(textView3);

                                entranceAgency.setOnValueTouchListener(new PieChartOnValueSelectListener() {
                                    @Override
                                    public void onValueSelected(int arcIndex, SliceValue value) {
                                        Toast.makeText(getApplicationContext(), "Group Name : "+agencyPercent.get(arcIndex)+" Total : "+agencytotal.get(arcIndex), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onValueDeselected() {

                                    }
                                });


                            }
                            PieChartData data = new PieChartData(sliceValueList);
                            data.setHasLabels(true);
                            data.setHasLabelsOnlyForSelected(false);
                            data.setHasLabelsOutside(false);
                            data.setHasCenterCircle(true);
                            if (isExploded) {
                                data.setSlicesSpacing(24);
                            }
                            data.setCenterText1("Percentage By Agency");
                            data.setCenterText1FontSize(14);
                            data.setCenterText1Color(Color.parseColor("#2c3e50"));
                            data.setAxisYLeft(new Axis().setHasLines(false).setMaxLabelChars(3).setHasSeparationLine(true));
                            data.setValues(sliceValueList);
                            data.setSlicesSpacing(2);
                            entranceAgency.setPieChartData(data);



                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });

        });


    }


    public CharSequence getSelectedWeek(int postion){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Calendar.YEAR);
        cal.set(Calendar.MONTH, spinnerPosition);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, postion);

        return DateFormat.format("MMMM dd, yyyy ",cal.getTime());
    }


    public void weekly_persons_entrance(String plantcode){

        Handler handler = new Handler();
        month.clear();
        Monthlyvalue.clear();
        handler.post(() -> {
            API.getClient().monthly_person_entrance(plantcode).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        Boolean success = jsonObject.getBoolean("success");
                        JSONArray array = jsonObject.getJSONArray("data");


                        if(success){
                            loading[0].setVisibility(View.GONE);
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);
                                float f_total = object.getInt("value");
                                month.add(object.getString("month"));
                                Monthlyvalue.add(f_total);
                            }

                            List<AxisValue> label = new ArrayList();
                            List<Column> columnListData = new ArrayList();
                            List<Column> subcolumn = new ArrayList();
                            List<AxisValue> axisValues = new ArrayList();
                            for (int i = 0; i <  array.length(); i++) {
                                List<SubcolumnValue> subcolumnValues = new ArrayList();
                                for (int j = 0; j < 1; j++) {
                                    subcolumnValues.add(new SubcolumnValue(Monthlyvalue.get(i).intValue(), Color(i)));
                                    axisValues.add(new AxisValue(Monthlyvalue.get(i).intValue()));
                                     label.add(new AxisValue((float) i).setLabel(month.get(i)));
                                }

                                SimpleColumnChartValueFormatter formatter = new SimpleColumnChartValueFormatter();
//                                formatter.setDecimalSeparator(',');
//                                formatter.setDecimalDigitsNumber(-3);
//                                formatter.setPrependedText(new char[]{'$'});
//                                formatter.setAppendedText(new char[]{'k'});

                                label.add(new AxisValue((float) i).setLabel(month.get(i)));
                                subcolumn.add(new Column(subcolumnValues));
                                Column column = new Column(subcolumnValues);
                                column.update(10.0f);
                                column.setHasLabels(true);
                                column.setHasLabelsOnlyForSelected(false).setFormatter(formatter);
                                column.setFormatter(formatter);
                                columnListData.add(column);
                            }




                            ColumnChartData columnData = new ColumnChartData(columnListData);
                            columnData.setAxisXBottom(new Axis(label).setHasLines(false).setMaxLabelChars(3));
                            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));
                            columnData.setStacked(false);
                            columnData.setValueLabelBackgroundAuto(true);
                            columnData.setValueLabelBackgroundEnabled(true);
                            columnData.getAxisYLeft().setTextColor(Color.parseColor("#2c3e50"));
                            columnData.getAxisXBottom().setTextColor(Color.parseColor("#2c3e50"));
                            columnData.getAxisXBottom().setTextSize(14);
                            columnData.setValueLabelTextSize(14);
                            columnData.setValueLabelsTextColor(Color.parseColor("#ffffff"));
                            columnData.setValueLabelBackgroundColor(Color.parseColor("#2c3e50"));
                            columnData.setValueLabelBackgroundEnabled(true);
                            columnData.setAxisXTop(new Axis(label).setHasLines(false).setMaxLabelChars(0));
                            columnData.getAxisXTop().setTextColor(Color.parseColor("#2c3e50"));
                            columnData.getAxisXTop().setTextSize(14);
                            columnData.setValueLabelTextSize(14);
                            columnData.setValueLabelsTextColor(Color.parseColor("#ffffff"));
                            columnData.setValueLabelBackgroundColor(Color.parseColor("#2c3e50"));
                            monthly_person_entrance.setZoomEnabled(true);
                            monthly_person_entrance.setColumnChartData(columnData);
                            monthly_person_entrance.setValueSelectionEnabled(true);
                            monthly_person_entrance.setViewportCalculationEnabled(false);
                            monthly_person_entrance.setZoomType(ZoomType.HORIZONTAL);



                        }
                        else{
                            loading[0].setAnimation(R.raw.no_connection);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });

        });




    }
    public void Monthly_people_Entrance_province(String to, String from, String plantcode){

        Handler handler = new Handler();

        handler.post(() -> {
            province.clear();
            provinceTotal.clear();
            API.getClient().Monthly_people_entrance_province_report(to,from,plantcode).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {

                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        boolean success = jsonObject.getBoolean("success");
                        int count = jsonObject.getInt("count");
                        int total = jsonObject.getInt("total");
                        JSONArray array = jsonObject.getJSONArray("data");

                        if(success){
                            loading[1].setVisibility(View.GONE);


                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);
                                float f_total = object.getInt("total");
                                province.add(object.getString("province"));
                                provinceTotal.add(f_total);
                            }

                            List<AxisValue> label = new ArrayList();
                            List<Column> columnListData = new ArrayList();
                            List<Column> subcolumn = new ArrayList();
                            List<AxisValue> axisValues = new ArrayList();
                            for (int i = 0; i < count; i++) {
                                List<SubcolumnValue> subcolumnValues = new ArrayList();
                                for (int j = 0; j < 1; j++) {
                                    subcolumnValues.add(new SubcolumnValue(provinceTotal.get(i).intValue(), Color(i)));
                                    axisValues.add(new AxisValue(provinceTotal.get(i).intValue()));
                                }
                                label.add(new AxisValue((float) i).setLabel(province.get(i)));
                                subcolumn.add(new Column(subcolumnValues));
                                Column column = new Column(subcolumnValues);
                                column.setHasLabels(true);
                                column.setHasLabelsOnlyForSelected(false);
                                columnListData.add(column);
                            }

                            ColumnChartData columnData = new ColumnChartData(columnListData);
                            columnData.setAxisXBottom(new Axis(label).setHasLines(false).setMaxLabelChars(10));
                            columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));
                            columnData.setStacked(false);
                            columnData.setValueLabelBackgroundAuto(true);
                            columnData.setValueLabelBackgroundEnabled(true);
                            columnData.getAxisYLeft().setTextColor(Color.parseColor("#2c3e50"));
                            columnData.getAxisXBottom().setTextColor(Color.parseColor("#2c3e50"));
                            columnData.getAxisXBottom().setTextSize(10);
                            columnData.setValueLabelTextSize(10);
                            columnData.setValueLabelsTextColor(Color.parseColor("#ffffff"));
                            columnData.setValueLabelBackgroundColor(Color.parseColor("#2c3e50"));
                            columnData.setValueLabelBackgroundEnabled(true);
                            chartBottom.setZoomEnabled(true);
                            chartBottom.setZoomLevel(0.0f, 0.0f, 0.0f);
                            ColumnChartData columnDataPreview = new ColumnChartData(subcolumn);
                            previewChart.setColumnChartData(columnDataPreview);
                            previewChart.setViewportChangeListener(new ViewportChangeListener() {
                                @Override
                                public void onViewportChanged(Viewport viewport) {
                                    chartBottom.setCurrentViewport(viewport);
                                }
                            });
                            chartBottom.setColumnChartData(columnData);
                            chartBottom.setValueSelectionEnabled(true);
                            Viewport viewport = new Viewport(chartBottom.getMaximumViewport());
                            chartBottom.setMaximumViewport(viewport);
                            chartBottom.setCurrentViewport(viewport);
                            chartBottom.invalidate();
                            chartBottom.setViewportCalculationEnabled(true);
                            viewport.right = (float) (count / 5);
                            previewChart.setCurrentViewport(viewport);
                            previewChart.setZoomType(ZoomType.HORIZONTAL);
                            chartBottom.setZoomType(ZoomType.HORIZONTAL);



                        }
                        else{
                            loading[0].setAnimation(R.raw.no_connection);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                }
            });

        });


    }
    private void DateRange(){
        dateRange[0].setOnClickListener(v -> {
            new MonthPickerDialog.Builder(this, (Month, Year) -> {
                String FilterDateTo = monthName[Month] + ", " + Year;
                String FilterDateFrom = dateRange[1].getText().toString();
                String plantcode = str_plancode;
                dateRange[0].setText(FilterDateTo);

                DataTemp(FilterDateTo,FilterDateFrom,plantcode);
                report_person(FilterDateTo,FilterDateFrom,plantcode);
                Monthly_people_Entrance_province(FilterDateTo,FilterDateFrom,plantcode);
                EntranceGroup(FilterDateTo,FilterDateFrom,plantcode);
                EntrancAgency(FilterDateTo,FilterDateFrom,plantcode);


            }, today.get(Calendar.YEAR),today.get(Calendar.MONTH))
                    .setActivatedMonth(today.get(Calendar.MONTH))
                    .setMinYear(2021)
                    .setActivatedYear(today.get(Calendar.YEAR))
                    .setMaxYear(today.get(Calendar.YEAR))
                    .setMinMonth(Calendar.MONTH)
                    .setTitle("Select month")
                    .setMonthRange(0, 11)
                    .setOnMonthChangedListener(i -> {

                    })
                    .setOnYearChangedListener(i -> {

                    }).build().show();

        });

        dateRange[1].setOnClickListener(v -> {
            new MonthPickerDialog.Builder(this, (Month, Year) -> {
                String FilterDateFrom = monthName[Month] + ", " + Year;
                String FilterDateTo = dateRange[0].getText().toString();
                String plantcode = str_plancode;
                dateRange[1].setText(FilterDateFrom);

                DataTemp(FilterDateTo,FilterDateFrom,plantcode);
                report_person(FilterDateTo,FilterDateFrom,plantcode);
                Monthly_people_Entrance_province(FilterDateTo,FilterDateFrom,plantcode);
                EntranceGroup(FilterDateTo,FilterDateFrom,plantcode);
                EntrancAgency(FilterDateTo,FilterDateFrom,plantcode);


            }, today.get(Calendar.YEAR),today.get(Calendar.MONTH))
                    .setActivatedMonth(today.get(Calendar.MONTH))
                    .setMinYear(2021)
                    .setActivatedYear(today.get(Calendar.YEAR))
                    .setMaxYear(today.get(Calendar.YEAR))
                    .setMinMonth(Calendar.MONTH)
                    .setTitle("Select month")
                    .setMonthRange(0, 11)
                    .setOnMonthChangedListener(i -> {

                    })
                    .setOnYearChangedListener(i -> {

                    }).build().show();
        });

    }

    @BindViews({R.id.avg,R.id.highest,R.id.lowest})
    TextView[] temp;
    private void DataTemp(String start,String end,String plantcode){
        API.getClient().Temp_monthly(start,end,plantcode).enqueue(new Callback<Object>() {

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
                    controller.toastip(R.raw.error_con,t.getMessage()  + " Temp");
                }
            }
        });
    }

    @BindViews({R.id.total_entry,R.id.total_person,R.id.total_entry_avg,R.id.trucking,R.id.agency,R.id.visitor,R.id.employee,R.id.male,R.id.avg_male_age,R.id.max_age,
            R.id.female,R.id.female_avg_age,R.id.female_max_age})
    TextView[] person;
    private void report_person(String start,String end,String plantcode){
        API.getClient().report_person_monthly(start,end,plantcode).enqueue(new Callback<Object>() {

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



//
//    void init(DateRangeCalendarView calendar){
//        calendar.setCalendarListener(new CalendarListener() {
//            @Override
//            public void onFirstDateSelected(@NonNull Calendar calendar) {
////                calendarOk.setEnabled(true);
////                str_toSelected = DateFormat.format("MMMM dd, yyyy ", calendar.getTime());
////                str_fromSelected = DateFormat.format("MMMM dd, yyyy ", calendar.getTime());
//            }
//
//            @Override
//            public void onDateRangeSelected(@NonNull Calendar calendar, @NonNull Calendar calendar1) {
//                calendarOk.setEnabled(true);
//                str_toSelected = DateFormat.format("MMMM dd, yyyy ", calendar.getTime());
//                str_fromSelected = DateFormat.format("MMMM dd, yyyy ", calendar1.getTime());
//                dateRange[0].setText(DateFormat.format("MMMM dd, yyyy ", calendar.getTime()));
//                dateRange[1].setText(DateFormat.format("MMMM dd, yyyy ", calendar1.getTime()));
//            }
//        });
//    }





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

                if(++checkplantcode > 0){
                    int indexOf = bu.indexOf(buAdapter.getItem(i));
                    String to = dateRange[0].getText().toString();
                    String from = dateRange[1].getText().toString();
                    str_plancode = buID.get(indexOf);
                    DataTemp(to,from,buID.get(indexOf));
                    report_person(to,from,buID.get(indexOf));
                    Monthly_people_Entrance_province(to,from,buID.get(indexOf));
                    EntranceGroup(to,from,buID.get(indexOf));
                    EntrancAgency(to,from,buID.get(indexOf));
                    // Toast.makeText(view.getContext(), buID.get(indexOf) + " " + dateRange[0].getText().toString() + " " + dateRange[1].getText().toString(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public int Color(int i) {
        return Color.parseColor(colors[i]);
    }
}