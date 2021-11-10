package com.example.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.contacttracingapp.config._login;
import com.example.contacttracingapp.config.loginAreaLocation;
import com.example.contacttracingapp.retroConfig.API;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.username)
    EditText username;
    private  boolean showpassword = true;
    @BindView(R.id.location)
    Spinner location;
    List<String> list_location_code = new ArrayList<>();;
    List<String> list_location_name = new ArrayList<>();;
    SpinnerAdapter locationAdapter;


    private String getPlant_code,getPlant_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        togglePassword(password);

        if(function.getInstance(this).ifLogin()){
            function.intent(Home.class,MainActivity.this);
            finish();
        }
        loadLocation();

    }


    public void login(View view) {
        String getUsername = username.getText().toString();
        String getPassword = password.getText().toString();
        String getCode = getPlant_code;
        String getName = getPlant_name;

        if(getUsername.isEmpty()){
            function.getInstance(this).Error("Username","Please Enter your username");
            username.requestFocus();
        }
        else if(getPassword.isEmpty()){
            function.getInstance(this).Error("Password","Please Enter your Password");
            password.requestFocus();
        }
        else if(getCode.equals("0")){
            function.getInstance(this).Error("Location","Please Select location");
            location.performClick();
        }
        else{
            function.getInstance(this).loading();
//            login(getUsername,getPassword,getCode,getName);
            re_login(getUsername,getPassword,getCode,getName);
        }


    }



    private void loadLocation(){
        list_location_name.clear();
        list_location_code.clear();
        API.getClient().AREAS().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonResponse.getBoolean("success");
                    JSONArray array = jsonResponse.getJSONArray("data");

                    if(success){
                        list_location_code.add("0");
                        list_location_name.add("Select Location");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            list_location_code.add(object.getString("plant_code"));
                            list_location_name.add(object.getString("plant_name"));
                        }

                        locationAdapter = new ArrayAdapter<>(MainActivity.this,R.layout.spinner_area, list_location_name);
                        location.setAdapter(locationAdapter);

                        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String code = list_location_code.get(position);
                                String name = list_location_name.get(position);

                                getPlant_code = code;
                                getPlant_name = name;

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });



//        list_location_name.clear();
//        list_location_code.clear();
//        Response.Listener<String> response = response1 -> {
//            try {
//                JSONObject jsonResponse = new JSONObject(response1);
//                boolean success = jsonResponse.getBoolean("success");
//                JSONArray array = jsonResponse.getJSONArray("data");
//
//
//
//                if(success){
//                    list_location_code.add("0");
//                    list_location_name.add("Select Location");
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject object = array.getJSONObject(i);
//
//                        list_location_code.add(object.getString("plant_code"));
//                        list_location_name.add(object.getString("plant_name"));
//                    }
//
//
//                    locationAdapter = new ArrayAdapter<>(this,R.layout.spinner_area, list_location_name);
//                    location.setAdapter(locationAdapter);
//
//
//
//                    location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            String code = list_location_code.get(position);
//                            String name = list_location_name.get(position);
//
//                            getPlant_code = code;
//                            getPlant_name = name;
//
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        };
//        Response.ErrorListener errorListener = new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        };
//        loginAreaLocation get = new loginAreaLocation(response,errorListener);
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        get.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(get);

    }


    private void re_login(String username,String password,String Code,String name){
        API.getClient().LOGIN(username,password).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonResponse.getBoolean("success");
                    JSONArray array = jsonResponse.getJSONArray("data");

                    if(success){

                        function.pDialog.dismissWithAnimation();
                        function.intent(Home.class,MainActivity.this);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("ID");
                            String fullname = object.getString("FULLNAME");
                            String status = object.getString("STATUS");
                            function.getInstance(getApplicationContext()).setAccount(Arrays.asList(id,fullname,"true",username));
                            function.getInstance(getApplicationContext()).setSelectedAreas(name,Code);
                        }

                    }
                    else{
                        function.pDialog.dismissWithAnimation();
                        Toast.makeText(getApplicationContext(),"Invalid Username and Password!",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }

    private void login(String getusername,String getpassword,String Code,String name){

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    JSONArray array = jsonResponse.getJSONArray("data");

                    if(success){

                        function.pDialog.dismissWithAnimation();
                        function.intent(Home.class,MainActivity.this);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String id = object.getString("ID");
                            String fullname = object.getString("FULLNAME");
                            String status = object.getString("STATUS");
                            function.getInstance(getApplicationContext()).setAccount(Arrays.asList(id,fullname,"true",getusername));
                            function.getInstance(getApplicationContext()).setSelectedAreas(name,Code);
                        }

                    }
                    else{
                        function.pDialog.dismissWithAnimation();
                        Toast.makeText(getApplicationContext(),"Invalid Username and Password!",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String result = function.getInstance(MainActivity.this).Errorvolley(error);
                function.getInstance(MainActivity.this).toastip(R.raw.error_con,result);
                function.pDialog.dismissWithAnimation();
            }
        };
        _login get = new _login(getusername,getpassword,response,errorListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        get.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(get);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void togglePassword(EditText editText){
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    if(showpassword){
                        showpassword = false;
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        editText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(),R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.invisible), null);
                    }
                    else{
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(),R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.visible), null);
                        showpassword = true;
                    }
                    return true;
                }
            }
            return false;
        });
    }


}