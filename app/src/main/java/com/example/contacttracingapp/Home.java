package com.example.contacttracingapp;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.contacttracingapp.Adapter.Adapter_area;
import com.example.contacttracingapp.Adapter.Adapter_scanned;
import com.example.contacttracingapp.GetterSetter.gs_area;
import com.example.contacttracingapp.GetterSetter.gs_scanned_all;
import com.example.contacttracingapp.config._appscantemp;
import com.example.contacttracingapp.config._areas;
import com.example.contacttracingapp.config.all_scanned;
import com.example.contacttracingapp.config.vaccinated;
import com.example.contacttracingapp.retroConfig.API;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class Home extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    public static FragmentManager fragmentManager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nvView)
    NavigationView navigationView;
    @BindView(R.id.flContent)
    FrameLayout container;


    TextView fullname, address;
    private AlertDialog alert;

    RadioButton yes;
    RadioButton no;
    LinearLayout radioGroup;


    function controller;
    //qrcode
    CodeScanner mCodeScanner;
    ArrayList<String> qrcodeData = new ArrayList<>();
    //textwatcher
    private boolean enabledSaveQrcodeScan = false;
    private MaterialButton saveScan;


    private List<gs_area> gs_areaList;
    private int totalAreas = 0;
    private RecyclerView recyclerView_Areas;
    private RecyclerView.Adapter areas_adapter;
    private MaterialButton areas_ok;
    private String ifvaccinated = "";
    private String ScanUserID = "";


    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    public static List<gs_scanned_all> list;
    @BindView(R.id.loading)
    LottieAnimationView loading;

    @BindView(R.id.search)
    TextInputEditText search;
    @BindView(R.id.tabs)
    TabLayout tabItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        function.getInstance(this);
        drawableMenu();
        controller = new function(this);

        gs_areaList = new ArrayList<>();
        //checkAreas();

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(999999999);

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_scanned(list,getApplicationContext());
        recyclerView.setAdapter(adapter);

        tabsLayout();
//        getallScanned();
        filterScanned();
        function.getInstance(this).merlin = new Merlin.Builder().withAllCallbacks().build(this);
        function.getInstance(this).merlin.registerConnectable(() -> {
            getallScanned();
            function.getInstance(this).toastip(R.raw.wifi,"Connecting to the server...");
        });

        function.getInstance(this).merlin.registerDisconnectable(() -> {
            getallScanned();
//            function.getInstance(this).toastip(R.raw.error_con,"Could not connect to the server.");
        });
    }

    private void tabsLayout(){
       tabItem.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
               if(tab.getPosition() == 0){
                   scanQrcode();
               }
               else if(tab.getPosition() == 1){
                   function.intent(Register.class,Home.this);
//                   function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Register");
               }
               else if(tab.getPosition() == 2){
                   Toast.makeText(Home.this, "Not Available", Toast.LENGTH_SHORT).show();
               }
               else if(tab.getPosition() == 3){
                   function.intent(Search.class,Home.this);
               }
           }

           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }

           @Override
           public void onTabReselected(TabLayout.Tab tab) {

           }
       });
    }


    private void filterScanned(){

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<gs_scanned_all> newList = new ArrayList<>();

                for (gs_scanned_all sub : list)
                {

                    String name = sub.getName().toLowerCase();
                    if(name.contains(charSequence)){
                        newList.add(sub);
                    }
                    adapter = new Adapter_scanned(newList, getApplicationContext());
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        function.getInstance(this).merlin.bind();
    }

    @Override
    protected void onPause() {
        function.getInstance(this).merlin.unbind();
        super.onPause();
    }

    protected void getallScanned(){
        list.clear();
        API.getClient().ScannedPerson().enqueue(new Callback<Object>() {
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

                            gs_scanned_all item = new gs_scanned_all(
                                    object.getString("type"),
                                    object.getString("name"),
                                    object.getString("temp"),
                                    object.getString("dt"),
                                    object.getString("cn"),
                                    object.getString("vaccinated")
                            );

                            list.add(item);
                        }

                        adapter = new Adapter_scanned(list,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (t instanceof IOException) {
                    function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                    loading.setVisibility(View.VISIBLE);
                    no_connection();
                }
            }
        });

    }


    protected void no_connection(){
        loading.setAnimation(R.raw.no_connection);
        loading.playAnimation();
        loading.loop(true);
    }


    protected void checkAreas(){
        if(function.getInstance(getApplicationContext()).getAreas().isEmpty()){
            gs_areaList.clear();
            ShowAreasModal();
        }
    }


    protected void ShowAreasModal(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
        View vs = LayoutInflater.from(Home.this).inflate(R.layout.modal_areas, null);
        recyclerView_Areas = vs.findViewById(R.id.data);
        recyclerView_Areas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        areas_ok = vs.findViewById(R.id.ok);
        areas_ok.setEnabled(false);
        areas_adapter = new Adapter_area(gs_areaList,getApplicationContext(),areas_ok,alert,address);
        recyclerView_Areas.setAdapter(areas_adapter);

        Response.Listener<String> response = response1 -> {
            try {
                JSONObject jsonResponse = new JSONObject(response1);
                boolean success = jsonResponse.getBoolean("success");
                totalAreas = jsonResponse.getInt("count");
                JSONArray array = jsonResponse.getJSONArray("data");



                if(success){
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        gs_area item = new gs_area(
                                object.getString("ID"),
                                object.getString("AREA")
                        );

                        gs_areaList.add(item);
                    }

                    areas_adapter = new Adapter_area(gs_areaList,getApplicationContext(),areas_ok,alert,address);
                    recyclerView_Areas.setAdapter(areas_adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        _areas get = new _areas(function.getInstance(Home.this).getUserId(),response,errorListener);
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        queue.add(get);

        dialog.setView(vs);
        alert = dialog.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
    }








    protected  void drawableMenu(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#ffffff"));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        fullname = headerView.findViewById(R.id.fullname);
        address = headerView.findViewById(R.id.address);
        fullname.setText(function.getInstance(this).getFullname());
        address.setText(function.getInstance(this).getAreas());



        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.scan:
                    scanQrcode();
                    drawerLayout.closeDrawer(Gravity.LEFT,true);
                    break;
                case R.id.register:
                    function.intent(Register.class,Home.this);
                    break;
                case R.id.employee:
                    function.intent(Register_employee.class,Home.this);
//                    function.intent(Employee.class,Home.this);
                    break;
                case R.id.blocklist:
                    function.intent(Blocklist.class,Home.this);
                    break;
                case R.id.search:
                    function.intent(Search.class,Home.this);
                    break;
                case R.id.bluetooth:
                    function.intent(BluetoothConnection.class,Home.this);
                    break;
                case R.id.logout:
                        new SweetAlertDialog(Home.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("You want to logout your account?")
                                .setConfirmText("Yes")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        function.getInstance(Home.this).setAccount(Arrays.asList("","","false",""));
                                        function.intent(MainActivity.class,Home.this);
                                        finish();
                                    }
                                })
                                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    break;
                case R.id.daily:
                    function.intent(DailyReports.class,Home.this);
                    break;
            }

            return true;
        });

    }




    private void FragmentActivity(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent,fragment).commit();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START,true);
        }
    }

    public void scanQrcode() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
                            View vs = LayoutInflater.from(Home.this).inflate(R.layout.qrscanner_layout, null);

                            CodeScannerView scanner = vs.findViewById(R.id.scanner_view);
                            mCodeScanner = new CodeScanner(getApplicationContext(), scanner);

                            mCodeScanner.startPreview();



                            dialog.setView(vs);
                            AlertDialog alert = dialog.create();

                            mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                                MediaPlayer mp = MediaPlayer.create(Home.this,R.raw.scan);
                                mp.start();
//                                Toast.makeText(Home.this, result.getText(), Toast.LENGTH_SHORT).show();
                                Log.d("data",result.getText());
                                String[] data = result.getText().split(",");



                             if(data[0].equals("cpfp")){
                                 addlistQrcode(Arrays.asList(data[3],data[5],data[7],data[9],data[11],data[13],data[15],data[17],data[19],data[21],data[23]));
                                 if(data[1].equals("true")){
                                     
                                     
                                     API.getClient().ScannedID(qrcodeData.get(6),function.getInstance(Home.this).getUserId()).enqueue(new Callback<Object>() {

                                         @Override
                                         public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                                             try {
                                                 JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                                                 boolean success = jsonResponse.getBoolean("success");
                                                 if(success){
//                                                     TextView textView = new TextView(Home.this);
////
//                                                     textView.setText(new StringBuilder().append(jsonResponse.getString("name")).append(" has been Banned until " + jsonResponse.getString("end")).append("\n").append("Plate No : ").append(jsonResponse.getString("plate")).append("\nRemark : ").append(jsonResponse.getString("remark")).append("\n\nPlease do not allow this person go inside in the feedmill.").toString());
//                                                     textView.setTextSize(18);
//                                                     textView.setWidth(500);
//                                                     textView.setHeight(500);
//                                                     new SweetAlertDialog(Home.this, SweetAlertDialog.WARNING_TYPE)
//                                                             .setTitleText(jsonResponse.getString("ban_type"))
//                                                             .setCustomView(textView.getRootView())
//                                                             .setConfirmText("ok")
//                                                             .setCancelClickListener(null)
//
//                                                             .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                                                 @Override
//                                                                 public void onClick(SweetAlertDialog sDialog) {
//                                                                     sDialog.dismissWithAnimation();
//                                                                 }
//                                                             })
//                                                             .show();
                                                     controller.BanModal(
                                                             jsonResponse.getString("ban_type"),
                                                             "This person has been banned until " + jsonResponse.getString("end"),
                                                             "Name : " + jsonResponse.getString("name"),
                                                             "Plate no. : " + jsonResponse.getString("plate"),
                                                             "Remark : " + jsonResponse.getString("remark"),
                                                             "Start : " + jsonResponse.getString("start"),
                                                             "End : " + jsonResponse.getString("end"),
                                                             "Thank you."
                                                     );

                                                 }
                                                 else{
                                                     API.getClient().VACCINE(qrcodeData.get(6),data[9],data[11]).enqueue(new Callback<Object>() {
                                                         @Override
                                                         public void onResponse(Call<Object> callok, retrofit2.Response<Object> responseok) {
                                                             try {
                                                                 JSONObject jsonResponse = new JSONObject(new Gson().toJson(responseok.body()));
                                                                 boolean success = jsonResponse.getBoolean("success");
                                                                 JSONArray array = jsonResponse.getJSONArray("data");
                                                                 if(success){
                                                                     for (int i = 0; i < array.length(); i++) {
//                                                                         Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                                                                         JSONObject object = array.getJSONObject(i);
                                                                         open_modal_temperature(data[19],data[9],data[11],data[15],object.getString("vaccinated"));
                                                                     }
                                                                 }


                                                             } catch (JSONException e) {
                                                                 e.printStackTrace();
                                                             }
                                                         }

                                                         @Override
                                                         public void onFailure(Call<Object> callok, Throwable tt) {
                                                             if (tt instanceof IOException) {
                                                                 function.getInstance(Home.this).toastip(R.raw.error_con,tt.getMessage());
                                                             }
                                                         }
                                                     });
                                                    //end
                                                 }


                                             } catch (JSONException e) {
                                                 e.printStackTrace();
                                             }
                                         }

                                         @Override
                                         public void onFailure(Call<Object> call, Throwable t) {
                                             if (t instanceof IOException) {
                                                 function.getInstance(Home.this).toastip(R.raw.error_con,t.getMessage());
                                             }
                                         }
                                     });



//
                                 }
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



    private void addlistQrcode(List<String> data){
        qrcodeData.addAll(data);
    }



    private void open_modal_temperature(String title,String name,String lname,String contact,String vaccine){

        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
        View vs = LayoutInflater.from(Home.this).inflate(R.layout.modal_tempareture, null);

        TextView fullname = vs.findViewById(R.id.fullname);
        EditText temperature = vs.findViewById(R.id.temperature);
        saveScan = vs.findViewById(R.id.saveScan);
        fullname.setText(title + " " + name + " " + lname);
        yes = vs.findViewById(R.id.yes);
        no = vs.findViewById(R.id.no);
        radioGroup = vs.findViewById(R.id.radioGroup);
//        function.getInstance(getApplicationContext()).toast(data.get(11));

//
        if(vaccine.equals("Y")){
            radioGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F1E9")));
            yes.setEnabled(false);
            no.setEnabled(false);

            yes.setChecked(true);
            no.setChecked(false);
        }else{
            radioGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F6E5DF")));
            yes.setChecked(false);
            no.setChecked(true);
        }


        tempTextWatcher(temperature,saveScan);

        dialog.setView(vs);
        alert = dialog.create();
        saveScan.setOnClickListener(v -> {
            String Get_vaccinated = yes.isChecked() ? "Y" : "N";

            API.getClient()._appscantemp(name,lname,function.getInstance(Home.this).getUserId(),contact,temperature.getText().toString(),Get_vaccinated).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                        boolean success = jsonResponse.getBoolean("success");

                        if(success){
                            alert.dismiss();
                            double temp = Double.parseDouble(temperature.getText().toString());
                            if(temp <= 30.0){
                                function.getInstance(getApplicationContext()).toastip(R.raw.low,"Low Temperature!");
                            }
                            else if(temp >= 37.5){
                                function.getInstance(getApplicationContext()).toastip(R.raw.high,"High Temperature!");
                            }
                            else{
                                function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Normal Temperature!");
                            }
                            getallScanned(); //reload after scanned and saved the record from the database

                        }
                        else{
                            saveScan.setEnabled(true);
                            function.getInstance(Home.this).toast("Error");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    if (t instanceof IOException) {
                        function.getInstance(Home.this).toastip(R.raw.error_con,t.getMessage());
                    }
                }
            });

//            Response.Listener<String> response = new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    try {
//                        JSONObject jsonResponse = new JSONObject(response);
//                        boolean success = jsonResponse.getBoolean("success");
//
//                        if(success){
//                            alert.dismiss();
//                            double temp = Double.parseDouble(temperature.getText().toString());
//                            if(temp <= 30.0){
//                                function.getInstance(getApplicationContext()).toastip(R.raw.low,"Low Temperature!");
//                            }
//                            else if(temp >= 37.5){
//                                function.getInstance(getApplicationContext()).toastip(R.raw.high,"High Temperature!");
//                            }
//                            else{
//                                function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Normal Temperature!");
//                            }
//                            getallScanned(); //reload after scanned and saved the record from the database
//
//                        }
//                        else{
//                            saveScan.setEnabled(true);
//                            function.getInstance(Home.this).toast("Error");
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            Response.ErrorListener errorListener = error -> {
//                String result = function.getInstance(Home.this).Errorvolley(error);
//                function.getInstance(Home.this).toastip(R.raw.error_con,result);
//            };
//            _appscantemp get = new _appscantemp(name,lname,function.getInstance(Home.this).getUserId(),contact,temperature.getText().toString(),Get_vaccinated,response,errorListener);
//            RequestQueue queue = Volley.newRequestQueue(this);
//            queue.add(get);
            saveScan.setEnabled(false);

        });
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(true);
        alert.show();
    }



    protected void AlertDialog(int icon,String msg,String title){
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .setCustomImage(icon)
                .show();
    }




    private void tempTextWatcher(EditText editText,MaterialButton save){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() != 0){
                    double temp = Double.valueOf(charSequence.toString());
                    if(temp <= 10.0 || temp >= 50.00){
                        save.setEnabled(false);
                    }
                    else{
                        save.setEnabled(true);
                    }
                }
                else{
                    save.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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


    public void scan(View view) {
        scanQrcode();
    }

    public void register(View view) {
        function.intent(Register.class,Home.this);
    }

    public void search(View view) {
        function.intent(Search.class,Home.this);
    }

    public void employee(View view) {
        function.intent(Register_employee.class,Home.this);
    }

    public void blocklist(View view) {
        function.intent(Blocklist.class,Home.this);
    }
}