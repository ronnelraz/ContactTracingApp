package com.example.contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.contacttracingapp.Adapter.Adapter_scanned;
import com.example.contacttracingapp.Adapter.adapter_trucking;
import com.example.contacttracingapp.GetterSetter.gs_scanned_all;
import com.example.contacttracingapp.GetterSetter.gs_trucking;
import com.example.contacttracingapp.config.agency;
import com.example.contacttracingapp.config.brgy;
import com.example.contacttracingapp.config.city;
import com.example.contacttracingapp.config.con_register;
import com.example.contacttracingapp.config.loginAreaLocation;
import com.example.contacttracingapp.config.province;
import com.example.contacttracingapp.config.trucking;
import com.example.contacttracingapp.retroConfig.API;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import retrofit2.Call;
import retrofit2.Callback;


public class Register extends AppCompatActivity {


    function controller;
    @BindView(R.id.opencamera) MaterialButton openCamera;
    @BindView(R.id.imageoutput) ImageView imageOutput;
    @BindViews({R.id.spinnerSelectCompany,R.id.company}) TextView[] SpinnerSelectCompany;
    @BindView(R.id.dob) EditText dob;
    @BindView(R.id.age) TextView age;
    @BindView(R.id.plate) EditText plateNo;
    @BindView(R.id.close) MaterialButton close;
    @BindView(R.id.save) MaterialButton saved;
    @BindView(R.id.radiogroup) RadioGridGroup rgroup;
    @BindView(R.id.title) RadioGroup title;

    private MaterialButton ActionbButton;
    private RadioButton optionRadio;
    private  String currentPhotoPath;


    final Calendar calendar = Calendar.getInstance();
    protected String strSelectedDate;
    private String current = "";
    private String ddmmyyyy = "ddmmyyyy";



    /*@Searchable Spinner*/
    private List<String> list_trucking = new ArrayList<>();
    private ArrayAdapter adapter;
    private EditText truckSearch;
    private ListView trucklist;
    private LottieAnimationView loading;
    private AlertDialog alert;


    /*Province*/
    @BindView(R.id.province) TextView openProvince;
    private List<String> list_province_name = new ArrayList<>();
    private List<String> getList_province_id = new ArrayList<>();
    private ArrayAdapter adapter_province;
    private String provinceSubID = "";

    /*City*/
    @BindView(R.id.municity) TextView opencity;
    private List<String> list_city_name = new ArrayList<>();
    private List<String> getList_city_id = new ArrayList<>();
    private ArrayAdapter adapter_city;
    private String citySubID = "";

    /*City*/
    @BindView(R.id.barangay) TextView openbarangay;
    private List<String> list_barangay_name = new ArrayList<>();
    private ArrayAdapter adapter_barangay;


    private String categpry = "";
    private String titled = "";

    @BindView(R.id.fname) EditText fname;
    @BindView(R.id.lname) EditText lname;
    @BindView(R.id.company) EditText company;
    @BindView(R.id.contact) EditText contact;
    @BindView(R.id.vaccine) RadioGroup vaccine;
    private boolean requiredPlate = false;
    private boolean companySelectedorNot = false;

    private Bitmap bitmap;
    private boolean Captured = false;
    private String vaccinated = "";

    // needed for communication to bluetooth device / network
    OutputStream outputStream;
    InputStream inputStream;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        controller = new function(this);


        OpenCameraModal();

        onRadioButtonCheckChanged(optionRadio, false);
        ViewDob();
        SelectProvince();

        close.setOnClickListener(v -> {
            function.intent(Home.class,this);
            finish();
        });
        save();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            FindBluetoothDevice();
            openBluetoothPrinter();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openBluetoothPrinter() throws IOException {
        try {
            BluetoothSocket createRfcommSocketToServiceRecord = this.bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
            this.bluetoothSocket = createRfcommSocketToServiceRecord;
            createRfcommSocketToServiceRecord.connect();
            this.outputStream = this.bluetoothSocket.getOutputStream();
            this.inputStream = this.bluetoothSocket.getInputStream();
        } catch (Exception unused) {
        }
    }



    public void FindBluetoothDevice() {
        String bluetoothConnection = controller.getBTName();
        Log.d("Bluetooth", bluetoothConnection);
        try {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter = defaultAdapter;
            if (defaultAdapter == null) {
               controller.toastip(R.raw.error_con,"No Bluetooth Adapter found");

            }
            if (bluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
            }
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice next : bondedDevices) {
                    if (next.getName().equals(bluetoothConnection)) {
                        this.bluetoothDevice = next;
                        Log.d("Bluetooth", bluetoothConnection);
                        return;
                    }
                    Log.d("Bluetooth", bluetoothConnection);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    void sendData(String type,String company,String fname,String lname,String address,String contact,String plate,String gender,String dob,String age) throws IOException {

        try {


            outputStream.write(("^XA^FWR^FO0,100^A0,25,20^FD" +
                    gender + " " + fname + " " + lname +
                    "^FS^CF0,30^FO35,40^BQR,2,5^FDQA,cpfp,true,date," + datenow() +
                    ",type," + type +
                    ",Company," + company +
                    ",Name," + fname +
                    ",LName," + lname +
                    ",address," + address +
                    ",Contact," + contact +
                    ",plate," + plate +
                    ",gender," + gender +
                    ",dob," + dob +
                    ",age," + age +
                    ",^FS^XZ").getBytes());
//            outputStream.write(msg.getBytes());
            Toast.makeText(getApplicationContext(), "Printing...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String datenow() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }


    //optional
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
           controller.toastip(R.raw.error_con,"Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void save(){
        try {
            saved.setOnClickListener(view -> {



                if(rgroup.getCheckedRadioButtonId() == -1){
                    function.getInstance(view.getContext()).toastip(R.raw.error_con,"Please Select Category");
                }

                if(title.getCheckedRadioButtonId() != -1){
                    int getTitle = title.getCheckedRadioButtonId();
                    RadioButton getSelectedTitle = findViewById(getTitle);
                    titled = getSelectedTitle.getText().toString();
                }
                else{
                    function.getInstance(view.getContext()).toastip(R.raw.error_con,"Please Title");
                }

                if(vaccine.getCheckedRadioButtonId() == -1){
                    function.getInstance(view.getContext()).toastip(R.raw.error_con,"Select vaccine");
                }
                else{
                    int getVaccine = vaccine.getCheckedRadioButtonId();
                    RadioButton _vaccined = findViewById(getVaccine);
                    vaccinated = _vaccined.getText().toString();

                    int selectedID = rgroup.getCheckedRadioButtonId();
                    if(selectedID == R.id.rad1){
                        categpry = "Visitor/Supplier";
                    }else if(selectedID == R.id.rad2){
                        categpry = "Trucking";
                    }else if(selectedID == R.id.rad3){
                        categpry = "Employee";
                    }else if(selectedID == R.id.rad4){
                        categpry = "Agency";
                    }


                    String _category = categpry;
                    String _vaccine = vaccinated;
                    String _title = titled;
                    String _dob = dob.getText().toString();
                    String _age = age.getText().toString();
                    String _fname = fname.getText().toString();
                    String _lname = lname.getText().toString();
                    String _company = companySelectedorNot ? SpinnerSelectCompany[0].getText().toString() : SpinnerSelectCompany[1].getText().toString();
                    String _province = openProvince.getText().toString();
                    String _city = opencity.getText().toString();
                    String _brgy = openbarangay.getText().toString();
                    String _contact = contact.getText().toString();
                    String _plate = plateNo.getText().toString();
                    String _img = Captured ?  getImg() : "";


                    if(_dob.isEmpty()){
                       dob.requestFocus();
                       function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid Date of Birth");
                    }
                    else if(_fname.isEmpty()){
                        fname.requestFocus();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid First Name");
                    }
                    else if(_lname.isEmpty()){
                        lname.requestFocus();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid Last Name");
                    }
                    else if(_company.isEmpty()){
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid Company Name");
                    }
                    else if(_province.isEmpty()){
                        SpinnerSelectCompany[0].performClick();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Select province");
                    }
                    else if(_city.isEmpty()){
                        opencity.performClick();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Select municipality/City");
                    }
                    else if(_brgy.isEmpty()){
                        openbarangay.performClick();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Selecct Barangay");
                    }
                    else if(_contact.isEmpty() || contact.length() <= 10){
                        contact.requestFocus();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid Contact Number");
                    }
                    else if(_img.isEmpty()){
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Please Capture valid ID");
                        openCameraButtonclick();
                    }
                    else{
                        try {
//                            FindBluetoothDevice();
//                            openBluetoothPrinter();
                            register(_category,_company,"",_plate,_fname,_lname,_title,_dob,_age,_province + " " + _city + " " + _brgy,_contact,controller.getAD(),_province,_city,_brgy,_img,controller.getAREACODE(),_vaccine);
                            sendData(_category,_company,_fname,_lname, _province + " " + _city + " " + _brgy,_contact,_plate,_title,_dob,_age);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }





                }

            });
        }catch (Exception e){
            System.out.println("Error");
        }

    }


    private void register(String type,String cn,String empid,String plate,String name,
                          String lname,String gender,String dob,String age,String address,
                          String contact,String AD,String pro,String mun, String brgy, String img,
                          String plantcode,String vaccine){
    try{
        API.getClient().con_register(type,cn,empid,plate,name,lname,gender,dob,age,address,contact,AD,pro,mun,brgy,img,plantcode,vaccine).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        controller.toastip(R.raw.ok,"Register Successfully");
                    }
                    else{
                        controller.toastip(R.raw.error_con,"something went wrong. Please Try Again Later.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (t instanceof IOException) {
                    function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                }
            }
        });

    }catch (Exception e){

    }

    }



    public String getImg(){
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
      return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
    }


    protected void openCameraButtonclick(){
        String fileName = "Photo";
        File storageDirectory = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
        try {
            File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(Register.this,"com.example.contacttracingapp.fileprovider",imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(intent,1);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    protected void SelectProvince(){

        try{
            openProvince.setOnClickListener(view -> {
                list_province_name.clear();

                API.getClient().province().enqueue(new Callback<Object>() {
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
                                    list_province_name.add(object.getString("name")); // + "-" +object.getString("subid")
                                    getList_province_id.add(object.getString("subid"));

                                }

                                adapter_province = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_province_name);
                                trucklist.setAdapter(adapter_province);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        if (t instanceof IOException) {
                            loading.setVisibility(View.VISIBLE);
                            function.getInstance(Register.this).toastip(R.raw.error_con,t.getMessage());
                        }
                    }
                });


//                Response.Listener<String> response = response1 -> {
//                    try {
//                        JSONObject jsonResponse = new JSONObject(response1);
//                        boolean success = jsonResponse.getBoolean("success");
//                        JSONArray array = jsonResponse.getJSONArray("data");
//
//
//
//                        if(success){
//                            loading.setVisibility(View.GONE);
//                            for (int i = 0; i < array.length(); i++) {
//                                JSONObject object = array.getJSONObject(i);
//                                list_province_name.add(object.getString("name") + "-" +object.getString("subid"));
//
//                            }
//
//                            adapter_province = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_province_name);
//                            trucklist.setAdapter(adapter_province);
//
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                };
//                Response.ErrorListener errorListener = error -> {
//                    loading.setVisibility(View.VISIBLE);
//                };
//                province get = new province(response,errorListener);
//                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                queue.add(get);

                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                View vs = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_company, null);
                trucklist = vs.findViewById(R.id.list_item);
                truckSearch = vs.findViewById(R.id.searchTruck);
                loading = vs.findViewById(R.id.loading);

                truckSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int position, int i1, int i2) {
                        adapter_province.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                trucklist.setOnItemClickListener((adapterView, view1, i, l) -> {
//                    String[] ProvinceList = adapter_province.getItem(i).toString().split("-");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,ProvinceList[0]);
//                    provinceSubID = ProvinceList[1];
//                    openProvince.setText(ProvinceList[0]);
//                    SelectCity(ProvinceList[1]);
//                    alert.dismiss();

                    openProvince.setText(adapter_province.getItem(i).toString());
                    int indexOf = list_province_name.indexOf(adapter_province.getItem(i));
                    SelectCity(getList_province_id.get(indexOf));
                    alert.dismiss();
                });

                dialog.setView(vs);
                alert = dialog.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(true);
                alert.show();
            });
        }catch(Exception e){
            System.out.println("Error");
        }


    }


    protected void SelectCity(String ID){
        try{
            opencity.setOnClickListener(view -> {
                list_city_name.clear();
                API.getClient().city(ID).enqueue(new Callback<Object>() {
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
                                list_city_name.add(object.getString("name"));
                                getList_city_id.add(object.getString("subid"));

                            }

                            adapter_city = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_city_name);
                            trucklist.setAdapter(adapter_city);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        if (t instanceof IOException) {
                            function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                        }
                    }
                });

//
//                Response.Listener<String> response = response1 -> {
//                    try {
//                        JSONObject jsonResponse = new JSONObject(response1);
//                        boolean success = jsonResponse.getBoolean("success");
//                        JSONArray array = jsonResponse.getJSONArray("data");
//
//                        if(success){
//                            loading.setVisibility(View.GONE);
//                            for (int i = 0; i < array.length(); i++) {
//                                JSONObject object = array.getJSONObject(i);
//                                list_city_name.add(object.getString("name") + "-" +object.getString("subid"));
//
//                            }
//
//                            adapter_city = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_city_name);
//                            trucklist.setAdapter(adapter_city);
//
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                };
//                Response.ErrorListener errorListener = error -> {
//                    loading.setVisibility(View.VISIBLE);
//                };
//                city get = new city(ID,response,errorListener);
//                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                queue.add(get);

                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                View vs = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_company, null);
                trucklist = vs.findViewById(R.id.list_item);
                truckSearch = vs.findViewById(R.id.searchTruck);
                loading = vs.findViewById(R.id.loading);

                truckSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int position, int i1, int i2) {
                        adapter_city.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                trucklist.setOnItemClickListener((adapterView, view1, i, l) -> {
//                    String[] CityList = adapter_city.getItem(i).toString().split("-");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,CityList[0]);
//                    citySubID = CityList[1];
//                    opencity.setText(CityList[0]);
//                    SelectBrgy(CityList[1]);
//                    alert.dismiss();
                    opencity.setText(adapter_city.getItem(i).toString());
                    int indexOf = list_city_name.indexOf(adapter_city.getItem(i));
                    SelectBrgy(getList_city_id.get(indexOf));
                    alert.dismiss();
                });






                dialog.setView(vs);
                alert = dialog.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(true);
                alert.show();
            });
        }catch(Exception e){
            System.out.println("Error");
        }

    }

    protected void SelectBrgy(String ID){

        try{
            openbarangay.setOnClickListener(view -> {
                list_barangay_name.clear();

                API.getClient().brgy(ID).enqueue(new Callback<Object>() {
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
                                list_barangay_name.add(object.getString("name"));

                            }

                            adapter_barangay = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_barangay_name);
                            trucklist.setAdapter(adapter_barangay);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        if (t instanceof IOException) {
                            function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                        }
                    }
                });


//                Response.Listener<String> response = response1 -> {
//                    try {
//                        JSONObject jsonResponse = new JSONObject(response1);
//                        boolean success = jsonResponse.getBoolean("success");
//                        JSONArray array = jsonResponse.getJSONArray("data");
//
//                        if(success){
//                            loading.setVisibility(View.GONE);
//                            for (int i = 0; i < array.length(); i++) {
//                                JSONObject object = array.getJSONObject(i);
//                                list_barangay_name.add(object.getString("name") + "-" +object.getString("subid"));
//
//                            }
//
//                            adapter_barangay = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_barangay_name);
//                            trucklist.setAdapter(adapter_barangay);
//
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                };
//                Response.ErrorListener errorListener = error -> {
//                    loading.setVisibility(View.VISIBLE);
//                };
//                brgy get = new brgy(ID,response,errorListener);
//                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                queue.add(get);

                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                View vs = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_company, null);
                trucklist = vs.findViewById(R.id.list_item);
                truckSearch = vs.findViewById(R.id.searchTruck);
                loading = vs.findViewById(R.id.loading);

                truckSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int position, int i1, int i2) {
                        adapter_barangay.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                trucklist.setOnItemClickListener((adapterView, view1, i, l) -> {
//                    String[] BrgyList = adapter_barangay.getItem(i).toString().split("-");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,BrgyList[0]);
//                    openbarangay.setText(BrgyList[0]);
//                    alert.dismiss();

                    openbarangay.setText(adapter_barangay.getItem(i).toString());
                    alert.dismiss();
                });

                dialog.setView(vs);
                alert = dialog.create();
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(true);
                alert.show();
            });
        }catch(Exception e){
            System.out.println("Error");
        }

    }

    protected void SelectedCompanyTrucking(String types){

        try{
            SpinnerSelectCompany[0].setText("");
            SpinnerSelectCompany[0].setOnClickListener(view -> {
                list_trucking.clear();

                if(types.equals("Truck")){

                    API.getClient().trucking().enqueue(new Callback<Object>() {
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
                                        list_trucking.add(object.getString("AGENCY"));

                                    }
                                    adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_trucking);
                                    trucklist.setAdapter(adapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            if (t instanceof IOException) {
                                loading.setVisibility(View.VISIBLE);
                                function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                            }
                        }
                    });

//                    Response.Listener<String> response = response1 -> {
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response1);
//                            boolean success = jsonResponse.getBoolean("success");
//                            JSONArray array = jsonResponse.getJSONArray("data");
//
//
//
//                            if(success){
//                                loading.setVisibility(View.GONE);
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject object = array.getJSONObject(i);
//                                    list_trucking.add(object.getString("AGENCY"));
//
//                                }
//                                adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_trucking);
//                                trucklist.setAdapter(adapter);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    };
//                    Response.ErrorListener errorListener = error -> {
//                        loading.setVisibility(View.VISIBLE);
//                    };
//                    trucking get = new trucking(response,errorListener);
//                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                    queue.add(get);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    View vs = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_company, null);
                    trucklist = vs.findViewById(R.id.list_item);
                    truckSearch = vs.findViewById(R.id.searchTruck);
                    loading = vs.findViewById(R.id.loading);

                    truckSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int position, int i1, int i2) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    trucklist.setOnItemClickListener((adapterView, view1, i, l) -> {
                        SpinnerSelectCompany[0].setText(adapter.getItem(i).toString());
                        function.getInstance(getApplicationContext()).toastip(R.raw.ok,adapter.getItem(i).toString());
                        alert.dismiss();
                    });






                    dialog.setView(vs);
                    alert = dialog.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.setCanceledOnTouchOutside(false);
                    alert.setCancelable(true);
                    alert.show();
                }
                else{

                    API.getClient().agency().enqueue(new Callback<Object>() {
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
    //                            gs_trucking item = new gs_trucking(object.getString("AGENCY"));
    //                            list_trucking.add(item);
                                        list_trucking.add(object.getString("AGENCY"));

                                    }
                                    adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_trucking);
                                    trucklist.setAdapter(adapter);
    //                        adapter = new adapter_trucking(this,list_trucking);
    //                        trucklist.setAdapter(adapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            if (t instanceof IOException) {
                                loading.setVisibility(View.VISIBLE);
                                function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                            }
                        }
                    });


//                    Response.Listener<String> response = response1 -> {
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response1);
//                            boolean success = jsonResponse.getBoolean("success");
//                            JSONArray array = jsonResponse.getJSONArray("data");
//
//
//
//                            if(success){
//                                loading.setVisibility(View.GONE);
//                                for (int i = 0; i < array.length(); i++) {
//                                    JSONObject object = array.getJSONObject(i);
////                            gs_trucking item = new gs_trucking(object.getString("AGENCY"));
////                            list_trucking.add(item);
//                                    list_trucking.add(object.getString("AGENCY"));
//
//                                }
//                                adapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_trucking);
//                                trucklist.setAdapter(adapter);
////                        adapter = new adapter_trucking(this,list_trucking);
////                        trucklist.setAdapter(adapter);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    };
//                    Response.ErrorListener errorListener = error -> {
//                        loading.setVisibility(View.VISIBLE);
//                    };
//                    agency get = new agency(response,errorListener);
//                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                    queue.add(get);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    View vs = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_company, null);
                    trucklist = vs.findViewById(R.id.list_item);
                    truckSearch = vs.findViewById(R.id.searchTruck);
                    loading = vs.findViewById(R.id.loading);

                    truckSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int position, int i1, int i2) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    trucklist.setOnItemClickListener((adapterView, view1, i, l) -> {
                        SpinnerSelectCompany[0].setText(adapter.getItem(i).toString());
                        function.getInstance(getApplicationContext()).toastip(R.raw.ok,adapter.getItem(i).toString());
                        alert.dismiss();
                    });






                    dialog.setView(vs);
                    alert = dialog.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.setCanceledOnTouchOutside(false);
                    alert.setCancelable(true);
                    alert.show();
                }

            });
        }catch(Exception e){
            System.out.println("Error");
        }


    }


    @SuppressLint("ClickableViewAccessibility")
    protected void ViewDob(){
        dob.setOnTouchListener((v, event) -> {
//                0 left, 1 top, 2 right, 3 bttom;
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (dob.getRight() - dob.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    new DatePickerDialog(v.getContext(),R.style.picker,getDateto(), calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                    return true;
                }
            }
            return false;
        });


        /**Ontextchanged**/
        dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int il, int i1, int i2) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                        age.setText("");
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        calendar.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        calendar.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > calendar.getActualMaximum(Calendar.DATE))? calendar.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dob.setText(current);
                    dob.setSelection(sel < current.length() ? sel : current.length());
                    String[] getDOB = dob.getText().toString().split("/");

                    if(getDOB[2].length() >= 4){
                        if (getDOB[2].matches("[0-9]+")) {
                            setAge(Integer.parseInt(getDOB[2]),Integer.parseInt(getDOB[1]),Integer.parseInt(getDOB[0]));
                        }
                    }


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void setAge(int year, int month, int day){
//        Calendar dob = Calendar.getInstance();
//        Calendar today = Calendar.getInstance();
//
//        dob.set(year, month, day);
//
//        int setage = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
//
//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
//            setage--;
//        }
//
//        Integer ageInt = new Integer(setage);
//        String ageS = ageInt.toString();
//        age.setText(ageS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age.setText(String.valueOf(Period.between(LocalDate.of(year, month, day),LocalDate.now()).getYears()));
        }

    }

    private DatePickerDialog.OnDateSetListener getDateto(){
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Formatter();
        };
        return date;
    }

    private void Formatter() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(calendar.getTime()));

//        String FilterFormat = "yyyy-M-d";
//        SimpleDateFormat sdffilter = new SimpleDateFormat(FilterFormat, Locale.US);
//        function.toast(getActivity(),sdffilter.format(myCalendar.getTime()));
//        stc_date_to = sdffilter.format(myCalendar.getTime());
    }




    @OnCheckedChanged({R.id.rad1,R.id.rad2,R.id.rad3,R.id.rad4})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if(checked) {
            switch (button.getId()) {
                case R.id.rad1:
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Visitor/Supplier");
                    SpinnerSelectCompany[0].setVisibility(View.GONE);
                    SpinnerSelectCompany[1].setVisibility(View.VISIBLE);
                    SpinnerSelectCompany[1].setText("");
                    SpinnerSelectCompany[1].setEnabled(true);
                    plateNo.setHint("Required");
                    requiredPlate = true;
                    companySelectedorNot = false;
                    break;

                case R.id.rad2:
                    SelectedCompanyTrucking("Truck");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Trucking");
                    SpinnerSelectCompany[1].setVisibility(View.GONE);
                    SpinnerSelectCompany[0].setVisibility(View.VISIBLE);
                    plateNo.setHint("Required");
                    requiredPlate = true;
                    companySelectedorNot = true;
                    break;
                case R.id.rad3:
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"CPF Employee");
                    SpinnerSelectCompany[1].setText("CHAROEN POKPHAND FOODS PHILIPPINES");

                    SpinnerSelectCompany[1].setEnabled(false);
                    SpinnerSelectCompany[0].setVisibility(View.GONE);
                    SpinnerSelectCompany[1].setVisibility(View.VISIBLE);
                    plateNo.setHint("Optional");
                    companySelectedorNot = false;
                    requiredPlate = false;
                    break;

                case R.id.rad4:
                    SelectedCompanyTrucking("Agency");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Agency");
                    SpinnerSelectCompany[1].setVisibility(View.GONE);
                    SpinnerSelectCompany[0].setVisibility(View.VISIBLE);
                    plateNo.setHint("Optional");
                    requiredPlate = false;
                    companySelectedorNot = true;
                    break;
            }
        }
    }

    protected void OpenCameraModal(){
        openCamera.setOnClickListener(v -> {

                String fileName = "Photo";
                File storageDirectory = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
            try {
                File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                currentPhotoPath = imageFile.getAbsolutePath();
                Uri imageUri = FileProvider.getUriForFile(Register.this,"com.example.contacttracingapp.fileprovider",imageFile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,1);


            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageOutput.setImageBitmap(bitmap);
            Captured = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back(View view) {
        function.intent(Home.class,view.getContext());
    }
}