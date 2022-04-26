package com.razo.contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.retroConfig.API;
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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register_employee extends AppCompatActivity {

    function controller;
    @BindView(R.id.Employee)
    TextView employee;

    TextView title_modal;
    EditText search_modal;
    ListView data_modal;
    LottieAnimationView loading_modal;

    AlertDialog alert;
    List<String> listname = new ArrayList<>();
    ArrayAdapter listnameAdapter;

    @BindView(R.id.title)
    RadioGroup GTitle;
    List<String> listTitle = new ArrayList<>();

    @BindView(R.id.dob) EditText dob;
    List<String> listDob = new ArrayList<>();
    @BindView(R.id.age) TextView age;

    @BindView(R.id.fname) EditText fname;
    List<String> listfname = new ArrayList<>();
    @BindView(R.id.lname) EditText lname;
    List<String> listLname = new ArrayList<>();
    @BindView(R.id.contact) EditText contact;
    List<String> listcontact = new ArrayList<>();



    /*@Searchable Spinner*/
    private List<String> list_trucking = new ArrayList<>();
    private ArrayAdapter adapter;
    private EditText truckSearch;
    private ListView trucklist;
    private LottieAnimationView loading;


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


    private MaterialButton ActionbButton;
    private RadioButton optionRadio;
    private  String currentPhotoPath;


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

    @BindView(R.id.opencamera) MaterialButton openCamera;
    @BindView(R.id.imageoutput)
    ImageView imageOutput;

    @BindView(R.id.save) MaterialButton saved;
    private String titled = "";
    @BindView(R.id.vaccine) RadioGroup vaccine;
    @BindView(R.id.plate) EditText plateNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_employee);
        ButterKnife.bind(this);
        controller = new function(this);


        employee.setOnClickListener(v -> {
            listname.clear();
            API.getClient().Employees().enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        Boolean success = jsonObject.getBoolean("success");
                        JSONArray array = jsonObject.getJSONArray("data");

                        if(success){
                            loading_modal.setVisibility(View.GONE);
                            for(int i = 0; i < array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                listname.add(object.getString("fullname"));
                                listTitle.add(object.getString("title"));
                                listDob.add(object.getString("dob"));
                                listfname.add(object.getString("fname"));
                                listLname.add(object.getString("lname"));
                                listcontact.add(object.getString("contact"));
                            }
                            listnameAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,listname);
                            data_modal.setAdapter(listnameAdapter);

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
            modalSpinner("Select employee",listname);
        });
        save();
        OpenCameraModal();
        SelectProvince();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            FindBluetoothDevice();
            openBluetoothPrinter();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    protected void save(){
        try {
            saved.setOnClickListener(view -> {



                if(GTitle.getCheckedRadioButtonId() != -1){
                    int getTitle = GTitle.getCheckedRadioButtonId();
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

                    String _vaccine = vaccinated;
                    String _title = titled;
                    String _dob = dob.getText().toString();
                    String _age = age.getText().toString();
                    String _fname = fname.getText().toString();
                    String _lname = lname.getText().toString();
                    String _company = "CHAROEN POKPHAND FOODS PHILIPPINES";
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
                            register(_company,"",_plate,_fname,_lname,_title,_dob,_age,_province + " " + _city + " " + _brgy,_contact,controller.getAD(),_province,_city,_brgy,_img,controller.getAREACODE(),_vaccine);
                            sendData(_company,_fname,_lname, _province + " " + _city + " " + _brgy,_contact,_plate,_title,_dob,_age);
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

    public String datenow() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    void sendData(String company,String fname,String lname,String address,String contact,String plate,String gender,String dob,String age) throws IOException {

        try {


            outputStream.write(("^XA^FWR^FO0,100^A0,25,20^FD" +
                    gender + " " + fname + " " + lname +
                    "^FS^CF0,30^FO35,40^BQR,2,5^FDQA,cpfp,true,date," + datenow() +
                    ",type,Employee" +
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


    private void register(String cn,String empid,String plate,String name,
                          String lname,String gender,String dob,String age,String address,
                          String contact,String AD,String pro,String mun, String brgy, String img,
                          String plantcode,String vaccine){


//        Toast.makeText(getApplicationContext(), vaccine + "\n" +
//                 gender + "\n" +
//                dob + "\n" +
//                age + "\n" +
//                name + "\n" +
//                lname + "\n" +
//                cn + "\n" +
//                pro + "\n" +
//                mun + "\n" +
//                brgy + "\n" +
//                contact + "\n" +
//                plate + "\n" +
//                AD + "\n" +
//                plantcode + "\n" +
//                address, Toast.LENGTH_SHORT).show();
        try{
            API.getClient().con_register("Employee",cn,empid,plate,name,lname,gender,dob,age,address,contact,AD,pro,mun,brgy,img,plantcode,vaccine).enqueue(new Callback<Object>() {
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



    protected void openCameraButtonclick(){
        String fileName = "Photo";
        File storageDirectory = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
        try {
            File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.contacttracingapp.fileprovider",imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(intent,1);


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


    protected void OpenCameraModal(){
        openCamera.setOnClickListener(v -> {

            String fileName = "Photo";
            File storageDirectory = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
            try {
                File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                currentPhotoPath = imageFile.getAbsolutePath();
                Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.contacttracingapp.fileprovider",imageFile);

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


    public String getImg(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
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
                            function.getInstance(Register_employee.this).toastip(R.raw.error_con,t.getMessage());
                        }
                    }
                });


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






    private void modalSpinner(String Title,List<String> list){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int i = displayMetrics.heightPixels;
        int i2 = displayMetrics.widthPixels;

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View vs = LayoutInflater.from(this).inflate(R.layout.modal_spinner_employee, null);

        title_modal = vs.findViewById(R.id.titlemodal);
        search_modal = vs.findViewById(R.id.searchmodal);
        data_modal = vs.findViewById(R.id.datamodal);
        loading_modal = vs.findViewById(R.id.loading);
        title_modal.setText(Title);

        data_modal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                alert.dismiss();
                employee.setText(listnameAdapter.getItem(i).toString());
                int indexOf = listname.indexOf(listnameAdapter.getItem(i));
                String getTitle = listTitle.get(indexOf);
               if(getTitle.equals("Mr.")){
                   GTitle.check(R.id.mr);
               }
               else{
                   GTitle.check(R.id.mrs);
               }
                dob.setText(listDob.get(indexOf));
                String[] age_explode = dob.getText().toString().split("/");
                if(age_explode[2].length() >= 4){
                    if (age_explode[2].matches("[0-9]+")) {
                        setAge(Integer.parseInt(age_explode[2]),Integer.parseInt(age_explode[1]),Integer.parseInt(age_explode[0]));
                    }
                }
                fname.setText(listfname.get(indexOf));
                lname.setText(listLname.get(indexOf));
                contact.setText(listcontact.get(indexOf));


            }
        });

        search_modal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listnameAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        dialog.setView(vs);
        alert = dialog.create();
        alert.getWindow().setLayout(i2, i);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(true);
        alert.show();
    }


    protected void setAge(int year, int month, int day){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age.setText(String.valueOf(Period.between(LocalDate.of(year, month, day),LocalDate.now()).getYears()));
        }

    }

    public void back(View view) {
        function.intent(Home.class,view.getContext());
    }
}