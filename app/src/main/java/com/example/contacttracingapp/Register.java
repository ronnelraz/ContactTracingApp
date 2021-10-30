package com.example.contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.contacttracingapp.Adapter.Adapter_scanned;
import com.example.contacttracingapp.Adapter.adapter_trucking;
import com.example.contacttracingapp.GetterSetter.gs_scanned_all;
import com.example.contacttracingapp.GetterSetter.gs_trucking;
import com.example.contacttracingapp.config.agency;
import com.example.contacttracingapp.config.brgy;
import com.example.contacttracingapp.config.city;
import com.example.contacttracingapp.config.province;
import com.example.contacttracingapp.config.trucking;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class Register extends AppCompatActivity{

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
    private ArrayAdapter adapter_province;
    private String provinceSubID = "";

    /*City*/
    @BindView(R.id.municity) TextView opencity;
    private List<String> list_city_name = new ArrayList<>();
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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        OpenCameraModal();

        onRadioButtonCheckChanged(optionRadio, false);
        ViewDob();
        SelectProvince();

        close.setOnClickListener(v -> {
            function.intent(Home.class,this);
            finish();
        });
        save();
    }


    protected void save(){
        try {
            saved.setOnClickListener(view -> {
                if(rgroup.getCheckedRadioButtonId() == -1){
                    function.getInstance(view.getContext()).toastip(R.raw.error_con,"Please Select Category");
                }else{
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



                    if(title.getCheckedRadioButtonId() != -1){
                        int getTitle = title.getCheckedRadioButtonId();
                        RadioButton getSelectedTitle = findViewById(getTitle);
                        titled = getSelectedTitle.getText().toString();
                    }
                    else{
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Please Title");
                    }

                    if(vaccine.getCheckedRadioButtonId() == -1){
                        int getVaccine = vaccine.getCheckedRadioButtonId();
                        RadioButton _vaccined = findViewById(getVaccine);
                        vaccinated = _vaccined.getText().toString();
                    }
                    else{
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Select vaccine if yes");
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
                       function.getInstance(view.getContext()).toast(
                                       _category + " " +
                                           _title + " " +
                                           _dob + " " +
                                           _age + " " +
                                           _fname + " " +
                                           _lname + " " +
                                           _company + " " +
                                           _province + " " +
                                           _city + " " +
                                           _brgy + " " +
                                           _contact + " " +
                                           _plate);
                    }





                }

            });
        }catch (Exception e){
            System.out.println("Error");
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
                Response.Listener<String> response = response1 -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response1);
                        boolean success = jsonResponse.getBoolean("success");
                        JSONArray array = jsonResponse.getJSONArray("data");



                        if(success){
                            loading.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                list_province_name.add(object.getString("name") + "-" +object.getString("subid"));

                            }

                            adapter_province = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_province_name);
                            trucklist.setAdapter(adapter_province);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                };
                Response.ErrorListener errorListener = error -> {
                    loading.setVisibility(View.VISIBLE);
                };
                province get = new province(response,errorListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(get);

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
                    String[] ProvinceList = adapter_province.getItem(i).toString().split("-");
                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,ProvinceList[0]);
                    provinceSubID = ProvinceList[1];
                    openProvince.setText(ProvinceList[0]);
                    SelectCity(ProvinceList[1]);
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
                Response.Listener<String> response = response1 -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response1);
                        boolean success = jsonResponse.getBoolean("success");
                        JSONArray array = jsonResponse.getJSONArray("data");

                        if(success){
                            loading.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                list_city_name.add(object.getString("name") + "-" +object.getString("subid"));

                            }

                            adapter_city = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_city_name);
                            trucklist.setAdapter(adapter_city);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                };
                Response.ErrorListener errorListener = error -> {
                    loading.setVisibility(View.VISIBLE);
                };
                city get = new city(ID,response,errorListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(get);

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
                    String[] CityList = adapter_city.getItem(i).toString().split("-");
                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,CityList[0]);
                    citySubID = CityList[1];
                    opencity.setText(CityList[0]);
                    SelectBrgy(CityList[1]);
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
                Response.Listener<String> response = response1 -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response1);
                        boolean success = jsonResponse.getBoolean("success");
                        JSONArray array = jsonResponse.getJSONArray("data");

                        if(success){
                            loading.setVisibility(View.GONE);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                list_barangay_name.add(object.getString("name") + "-" +object.getString("subid"));

                            }

                            adapter_barangay = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1,list_barangay_name);
                            trucklist.setAdapter(adapter_barangay);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                };
                Response.ErrorListener errorListener = error -> {
                    loading.setVisibility(View.VISIBLE);
                };
                brgy get = new brgy(ID,response,errorListener);
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(get);

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
                    String[] BrgyList = adapter_barangay.getItem(i).toString().split("-");
                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,BrgyList[0]);
                    openbarangay.setText(BrgyList[0]);
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
                    Response.Listener<String> response = response1 -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response1);
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
                    };
                    Response.ErrorListener errorListener = error -> {
                        loading.setVisibility(View.VISIBLE);
                    };
                    trucking get = new trucking(response,errorListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(get);

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
                    Response.Listener<String> response = response1 -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(response1);
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
                    };
                    Response.ErrorListener errorListener = error -> {
                        loading.setVisibility(View.VISIBLE);
                    };
                    agency get = new agency(response,errorListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(get);

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
    }

    public void back(View view) {
        function.intent(Home.class,view.getContext());
    }
}