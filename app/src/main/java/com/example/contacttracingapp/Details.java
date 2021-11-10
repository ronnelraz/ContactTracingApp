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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.contacttracingapp.config.agency;
import com.example.contacttracingapp.config.brgy;
import com.example.contacttracingapp.config.city;
import com.example.contacttracingapp.config.con_imgUpdate;
import com.example.contacttracingapp.config.con_register;
import com.example.contacttracingapp.config.con_updateregister;
import com.example.contacttracingapp.config.config;
import com.example.contacttracingapp.config.province;
import com.example.contacttracingapp.config.trucking;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class Details extends AppCompatActivity {

    function controller;

    public static String type,cn,plate,gender,dob,age,address,contact,lname,fname,img,id,privince,city,brgy,vaccinated;
    @BindViews({R.id.btnprint,R.id.btnmodify,R.id.btnlog,R.id.printnow})
    MaterialButton[] actions;

    @BindView(R.id.qrcode)
    ImageView qrcode;
    @BindView(R.id.modify)
    RelativeLayout modify;
    @BindView(R.id.logs) RelativeLayout logs;

    @BindViews({R.id.htitle,R.id.hname,R.id.hcontact,R.id.haddress,R.id.hdob,R.id.hage,R.id.hcn,R.id.hplate})
    TextView[] header;
    @BindView(R.id.himg) ImageView himg;


    // needed for communication to bluetooth device / network
    OutputStream outputStream;
    InputStream inputStream;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    @BindView(R.id.radiogroup) RadioGridGroup category;
    @BindView(R.id.vaccine) RadioGroup vaccine;
    @BindView(R.id.title) RadioGroup title;

    @BindView(R.id.opencamera) ImageView openCamera;
    @BindViews({R.id.spinnerSelectCompany,R.id.company}) TextView[] SpinnerSelectCompany;
    @BindView(R.id.dob) EditText mdob;
    @BindView(R.id.age) TextView mage;
    @BindView(R.id.plate) EditText mplateNo;
    @BindView(R.id.save) MaterialButton saved;

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

    @BindView(R.id.fname) EditText mfname;
    @BindView(R.id.lname) EditText mlname;
    @BindView(R.id.company) EditText mcompany;
    @BindView(R.id.contact) EditText mcontact;
    private boolean requiredPlate = false;
    private boolean companySelectedorNot = false;

      private Bitmap bitmap;
      private boolean Captured = false;
    private String mvaccinated = "";
    private RadioButton moptionRadio;
    private  String mcurrentPhotoPath;


    final Calendar mcalendar = Calendar.getInstance();
    protected String mstrSelectedDate;
    private String mcurrent = "";
    private String mddmmyyyy = "ddmmyyyy";
    @BindView(R.id.back) MaterialButton close;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        controller = new function(this);
        header[0].setText("Title : " + gender);
        header[1].setText("Name : " + fname + " " + lname);
        header[2].setText("Contact : " + contact);
        header[3].setText("Address : " + address);
        header[4].setText("Date of Birth : " + dob);
        header[5].setText("Age : " + age);
        header[6].setText("Company : " + cn);
        header[7].setText("Plate No. : " + plate);

        mdob.setText(dob);
        mage.setText(age);
        mfname.setText(fname);
        mlname.setText(lname);
        openProvince.setText(privince);
        opencity.setText(city);
        openbarangay.setText(brgy);
        mcontact.setText(contact);
        mplateNo.setText(plate);

        OpenCameraModal();

        onRadioButtonCheckChanged(optionRadio, false);
        ViewDob();
        SelectProvince();

        close.setOnClickListener(v -> {
            function.intent(Home.class,this);
            finish();
        });

        if(img.isEmpty()){
            himg.setImageResource(R.drawable.user__1_);
        }
        else{
            final int radius = 5;
            final int margin = 5;
            final Transformation transformation = new RoundedCornersTransformation(radius, margin);
            Picasso.get().load(config.IMGURL + img).transform(new CropCircleTransformation()).fit().centerInside().rotate(90.0f).into(himg);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        save();
        try {
            FindBluetoothDevice();
            openBluetoothPrinter();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Actions(actions);

    }



    public void Actions(MaterialButton[] button){
       button[0].setOnClickListener(v-> {
           qrcode.setVisibility(View.VISIBLE);
           modify.setVisibility(View.GONE);
           logs.setVisibility(View.GONE);
           button[3].setVisibility(View.VISIBLE);
       });

       button[1].setOnClickListener(v-> {
           qrcode.setVisibility(View.GONE);
           modify.setVisibility(View.VISIBLE);
           logs.setVisibility(View.GONE);
           button[3].setVisibility(View.GONE);
           modity();
       });

       button[2].setOnClickListener(v-> {
           qrcode.setVisibility(View.GONE);
           modify.setVisibility(View.GONE);
           logs.setVisibility(View.VISIBLE);
           button[3].setVisibility(View.GONE);
       });

       button[3].setOnClickListener(v -> {
           try {
               sendData(type,cn,fname,lname,address,contact,plate,gender,dob,age);
           }catch (Exception e){

           }
       });
    }


    void modity(){

        if(type.equals("Visitor/Supplier")){
            category.check(R.id.rad1);
        }else if(type.equals("Trucking")){
            category.check(R.id.rad2);
        }else if(type.equals("Employee")){
            category.check(R.id.rad3);
        }else if(type.equals("Agency")){
            category.check(R.id.rad4);
        }

        if(vaccinated.equals("N")){
            vaccine.check(R.id.no);
        }
        else{
            vaccine.setClickable(false);
            vaccine.check(R.id.yes);
        }

        if(gender.equals("MR")){
            title.check(R.id.mr);
        }
        else{
            title.check(R.id.mrs);
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

    protected void save(){
        try {
            saved.setOnClickListener(view -> {



                if(category.getCheckedRadioButtonId() == -1){
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

                    int selectedID = category.getCheckedRadioButtonId();
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
                    String _vaccine = mvaccinated;
                    String _title = titled;
                    String _dob = mdob.getText().toString();
                    String _age = mage.getText().toString();
                    String _fname = mfname.getText().toString();
                    String _lname = mlname.getText().toString();
                    String _company = companySelectedorNot ? SpinnerSelectCompany[0].getText().toString() : SpinnerSelectCompany[1].getText().toString();
                    String _province = openProvince.getText().toString();
                    String _city = opencity.getText().toString();
                    String _brgy = openbarangay.getText().toString();
                    String _contact = mcontact.getText().toString();
                    String _plate = mplateNo.getText().toString();
                   // String _img = Captured ?  getImg() : "";


                    if(_dob.isEmpty()){
                        mdob.requestFocus();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid Date of Birth");
                    }
                    else if(_fname.isEmpty()){
                        mfname.requestFocus();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid First Name");
                    }
                    else if(_lname.isEmpty()){
                        mlname.requestFocus();
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
                        mcontact.requestFocus();
                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Invalid Contact Number");
                    }
//                    else if(_img.isEmpty()){
//                        function.getInstance(view.getContext()).toastip(R.raw.error_con,"Please Capture valid ID");
//                        openCameraButtonclick();
//                    }
                    else{
                        try {
                            FindBluetoothDevice();
                            openBluetoothPrinter();
                            register(id,_category,_company,"",_plate,_fname,_lname,_title,_dob,_age,_province + " " + _city + " " + _brgy,_contact,controller.getAD(),_province,_city,_brgy,_vaccine);

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


    private void register(String sid,String type,String cn,String empid,String plate,String name,
                          String lname,String gender,String dob,String age,String address,
                          String contact,String AD,String pro,String mun, String brgy,String vaccine){
        Response.Listener<String> response = response1 -> {
            try {
                JSONObject jsonResponse = new JSONObject(response1);
                boolean success = jsonResponse.getBoolean("success");
                if(success){
                    controller.toastip(R.raw.ok,"Updated Successfully");
                    sendData(type,cn,name,lname, pro + " " + mun + " " + brgy,contact,plate,gender,dob,age);

                    header[0].setText("Title : " + gender);
                    header[1].setText("Name : " + name + " " + lname);
                    header[2].setText("Contact : " + contact);
                    header[3].setText("Address : " + pro + " " + mun + " " + brgy);
                    header[4].setText("Date of Birth : " + dob);
                    header[5].setText("Age : " + age);
                    header[6].setText("Company : " + cn);
                    header[7].setText("Plate No. : " + plate);
                }
                else{
                    controller.toastip(R.raw.error_con,"something went wrong. Please Try Again Later.");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> {

        };
        con_updateregister get = new con_updateregister(sid,type,cn,empid,plate,name,lname,gender,dob,age,address,contact,AD,pro,mun,brgy,vaccine
                ,response,errorListener);
        RequestQueue queue = Volley.newRequestQueue(Details.this);
        queue.add(get);
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
            Uri imageUri = FileProvider.getUriForFile(Details.this,"com.example.contacttracingapp.fileprovider",imageFile);

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
                com.example.contacttracingapp.config.city get = new city(ID,response,errorListener);
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
                com.example.contacttracingapp.config.brgy get = new brgy(ID,response,errorListener);
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
        mdob.setOnTouchListener((v, event) -> {
//                0 left, 1 top, 2 right, 3 bttom;
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (mdob.getRight() - mdob.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    new DatePickerDialog(v.getContext(),R.style.picker,getDateto(), calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                    return true;
                }
            }
            return false;
        });


        /**Ontextchanged**/
        mdob.addTextChangedListener(new TextWatcher() {
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
                        mage.setText("");
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
                    mdob.setText(current);
                    mdob.setSelection(sel < current.length() ? sel : current.length());
                    String[] getDOB = mdob.getText().toString().split("/");

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mage.setText(String.valueOf(Period.between(LocalDate.of(year, month, day),LocalDate.now()).getYears()));
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
        mdob.setText(sdf.format(calendar.getTime()));
    }




    @OnCheckedChanged({R.id.rad1,R.id.rad2,R.id.rad3,R.id.rad4})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if(checked) {
            switch (button.getId()) {
                case R.id.rad1:
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Visitor/Supplier");
                    SpinnerSelectCompany[0].setVisibility(View.GONE);
                    SpinnerSelectCompany[1].setVisibility(View.VISIBLE);
                    SpinnerSelectCompany[1].setText(cn);
                    SpinnerSelectCompany[1].setText("");
                    SpinnerSelectCompany[1].setEnabled(true);
                    mplateNo.setHint("Required");
                    requiredPlate = true;
                    companySelectedorNot = false;
                    break;

                case R.id.rad2:
                    SelectedCompanyTrucking("Truck");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Trucking");
                    SpinnerSelectCompany[1].setVisibility(View.GONE);
                    SpinnerSelectCompany[0].setVisibility(View.VISIBLE);
                    SpinnerSelectCompany[0].setText(cn);
                    mplateNo.setHint("Required");
                    requiredPlate = true;
                    companySelectedorNot = true;
                    break;
                case R.id.rad3:
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"CPF Employee");
                    SpinnerSelectCompany[1].setText("CHAROEN POKPHAND FOODS PHILIPPINES");
                    SpinnerSelectCompany[1].setEnabled(false);
                    SpinnerSelectCompany[0].setVisibility(View.GONE);
                    SpinnerSelectCompany[1].setVisibility(View.VISIBLE);
                    SpinnerSelectCompany[1].setText("CHAROEN POKPHAND FOODS PHILIPPINES");
                    mplateNo.setHint("Optional");
                    companySelectedorNot = false;
                    requiredPlate = false;
                    break;

                case R.id.rad4:
                    SelectedCompanyTrucking("Agency");
//                    function.getInstance(getApplicationContext()).toastip(R.raw.ok,"Agency");
                    SpinnerSelectCompany[1].setVisibility(View.GONE);
                    SpinnerSelectCompany[0].setVisibility(View.VISIBLE);
                    SpinnerSelectCompany[0].setText(cn);
                    mplateNo.setHint("Optional");
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
                Uri imageUri = FileProvider.getUriForFile(Details.this,"com.example.contacttracingapp.fileprovider",imageFile);

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
            himg.setImageBitmap(bitmap);

            String img = getImg();
            Response.Listener<String> response = response1 -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response1);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        controller.toastip(R.raw.ok,"Image Updated");
                    }
                    else{
                        controller.toastip(R.raw.error_con,"something went wrong. Please Try Again Later.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            Response.ErrorListener errorListener = error -> {

            };
            con_imgUpdate get = new con_imgUpdate(id,img,response,errorListener);
            RequestQueue queue = Volley.newRequestQueue(Details.this);
            queue.add(get);

        }
    }

}