package com.razo.contacttracingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.razo.contacttracingapp.Adapter.Adapter_received;
import com.razo.contacttracingapp.Adapter.Adapter_scanned;
import com.razo.contacttracingapp.GetterSetter.gs_received_vitamins;
import com.razo.contacttracingapp.GetterSetter.gs_scanned_all;
import com.razo.contacttracingapp.retroConfig.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class received_vitamins extends AppCompatActivity {

    CodeScanner mCodeScanner;
    @BindView(R.id.loading)
    LottieAnimationView loading;
    @BindViews({R.id.month,R.id.daily,R.id.vitamins}) TextView[] counted;
    @BindView(R.id.datefilter) EditText datefilter;
    DateRangeCalendarView calendar;
    MaterialButton calendarOk;
    private CharSequence str_toSelected = null;

    @BindView(R.id.nested)
    NestedScrollView scrollView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    public static List<gs_received_vitamins> list = new ArrayList<>();


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_vitamins);
        ButterKnife.bind(this);
        recyclerView = findViewById(R.id.data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(999999999);

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_received(list,getApplicationContext());
        recyclerView.setAdapter(adapter);


        Date date = new Date();
        CharSequence default_to = DateFormat.format("MMMM dd, yyyy", date.getTime()); //yyyy-MM-dd

        datefilter.setText(default_to.toString());

        String toformat = formatDate(default_to.toString());
        loadcounted(toformat);
        getallScanned(toformat);

        datefilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarPopup();
            }
        });
        datefilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (datefilter.getRight() - datefilter.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        CalendarPopup();
                        return true;
                    }
                }
                return false;
            }
        });

       scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
           @Override
           public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
               if (scrollY > oldScrollY) {
                   fab.hide();
               } else {
                   fab.show();
               }
           }
       });

    }

    void init(DateRangeCalendarView calendar){
        calendar.setCalendarListener(new CalendarListener() {
            @Override
            public void onFirstDateSelected(@NonNull Calendar calendar) {
                calendarOk.setEnabled(true);
                str_toSelected = DateFormat.format("MMMM dd, yyyy", calendar.getTime());
            }

            @Override
            public void onDateRangeSelected(@NonNull Calendar calendar, @NonNull Calendar calendar1) {

            }
        });
    }


    private void CalendarPopup(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(received_vitamins.this);
        View vs = LayoutInflater.from(received_vitamins.this).inflate(R.layout.modal_date_range, null);
        calendar = vs.findViewById(R.id.calendar);
        calendarOk = vs.findViewById(R.id.ok);
        init(calendar);
        dialog.setView(vs);
        AlertDialog alert = dialog.create();
        calendarOk.setOnClickListener(v -> {
            String to = str_toSelected.toString();
            datefilter.setText(to);
//            Toast.makeText(v.getContext(), formatDate(to), Toast.LENGTH_SHORT).show();
            loadcounted(formatDate(to));
            getallScanned(formatDate(to));

            alert.dismiss();
        });
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(true);
        alert.show();
    }

    public String formatDate(String dateString){
        Date date = null;
        try {
            date = new SimpleDateFormat("MMMM dd, yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return formattedDate;
    }

    protected void getallScanned(String date){
        list.clear();
        API.getClient().received_vitamins_empo(date).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonResponse.getBoolean("success");
                    JSONArray array = jsonResponse.getJSONArray("data");
                    if(success){
                        loading.setVisibility(View.GONE);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            gs_received_vitamins item = new gs_received_vitamins(
                                    object.getString("name"),
                                    object.getString("qty"),
                                    object.getString("received_date")
                            );

                            list.add(item);
                        }

                        adapter = new Adapter_received(list,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        recyclerView.setAdapter(null);
                        list.clear();
                        loading.setVisibility(View.VISIBLE);
                        loading.setAnimation(R.raw.nodata);
                        loading.playAnimation();
                        loading.loop(true);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (t instanceof IOException) {
                    function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage() + " Load Data");
                    loading.setVisibility(View.VISIBLE);
//                    no_connection();
                }
            }
        });
    }

    public void back(View view) {
        function.intent(Home.class,view.getContext());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        function.intent(Home.class,this);
    }

    public void scan(View view) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(received_vitamins.this);
                            View vs = LayoutInflater.from(received_vitamins.this).inflate(R.layout.qrscanner_layout, null);

                            CodeScannerView scanner = vs.findViewById(R.id.scanner_view);
                            mCodeScanner = new CodeScanner(getApplicationContext(), scanner);

                            mCodeScanner.startPreview();



                            dialog.setView(vs);
                            AlertDialog alert = dialog.create();

                            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                                MediaPlayer mp = MediaPlayer.create(received_vitamins.this,R.raw.scan);
                                mp.start();
//                                Toast.makeText(Home.this, result.getText(), Toast.LENGTH_SHORT).show();
                                Log.d("data",result.getText());
                                String[] data = result.getText().split(",");



                                if(data[0].equals("cpfp")){

                                    if(data[1].equals("true")){
//                                        function.getInstance(received_vitamins.this).toastip(R.raw.error_con,data[19] + " " + data[9]+ " " +data[11]+ " " +data[15]);
//                                        Log.d("pukingina",data[15]);
                                        openmodalVitamins(data[15],data[19] + " " + data[9]+ " " +data[11]);
//
                                    } //end
                                    else{
                                        function.getInstance(getApplicationContext()).errorEffect();
                                        function.getInstance(getApplicationContext()).toast("Invalid QR Code");
                                    }
                                }
                                else{
                                    function.getInstance(getApplicationContext()).errorEffect();
                                    function.getInstance(getApplicationContext()).toast("Invalid QR Code");
                                }
                                alert.dismiss();
                            }));

                            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alert.setCanceledOnTouchOutside(false);
                            alert.setCancelable(true);
                            alert.show();

                        }
                        else if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())

                .onSameThread()
                .check();
    }


    private  void openmodalVitamins(String contact,String name){
        AlertDialog.Builder dialog = new AlertDialog.Builder(received_vitamins.this);
        View vs = LayoutInflater.from(received_vitamins.this).inflate(R.layout.modal_vitamins, null);

        TextView sname = vs.findViewById(R.id.name);
        EditText qty = vs.findViewById(R.id.qty);
        MaterialButton save = vs.findViewById(R.id.save);
        MaterialButton cancel = vs.findViewById(R.id.cancel);

        sname.setText(name);

        dialog.setView(vs);
        AlertDialog alert = dialog.create();
        cancel.setOnClickListener(v -> {
            alert.dismiss();
        });
        save.setOnClickListener(v -> {


            Integer int_qty = Integer.parseInt(qty.getText().toString()) == 0 ? 0 : Integer.parseInt(qty.getText().toString());
            String user = function.getInstance(received_vitamins.this).getUserId();
            String getcontact = contact;
            if(int_qty == 0){
                function.getInstance(received_vitamins.this).toastip(R.raw.error_con,"Invalid Quantity");
            }
            else{
                API.getClient().received_vitamins(getcontact,String.valueOf(int_qty),user).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                        boolean success = jsonResponse.getBoolean("success");

                        if(success){
                            qty.setText("");
                            alert.dismiss();
                            Date date = new Date();
                            CharSequence default_to = DateFormat.format("MMMM dd, yyyy", date.getTime()); //yyyy-MM-dd
                            String toformat = formatDate(default_to.toString());
                            recyclerView.setAdapter(null);
                            getallScanned(toformat);
                            new SweetAlertDialog(received_vitamins.this)
                                    .setTitleText("Save successfully!")
                                    .show();

                        }
                        else{
                            qty.setText("");
                            alert.dismiss();
//                            getallScanned();
                            new SweetAlertDialog(received_vitamins.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Sorry")
                                    .setContentText("This person already received vitamins")
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    if (t instanceof IOException) {
                        function.getInstance(received_vitamins.this).toastip(R.raw.error_con,t.getMessage());
                    }
                }
            });
            }


//



        });
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(true);
        alert.show();

    }


    private void loadcounted(String to){
        API.getClient().vitamins_total_received(to).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonResponse.getBoolean("success");
                    String daily = jsonResponse.getString("daily");
                    String monthly = jsonResponse.getString("month");
                    String vitamins = jsonResponse.getString("vita");

                    if(success){
                        counted[0].setText(monthly);
                        counted[1].setText(daily);
                        counted[2].setText(vitamins);
                    }
                    else{
                        counted[0].setText("0");
                        counted[1].setText("0");
                        counted[2].setText("0");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (t instanceof IOException) {
                    function.getInstance(received_vitamins.this).toastip(R.raw.error_con,t.getMessage());
                }
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}