package com.example.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.contacttracingapp.config._login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.username)
    EditText username;
    private  boolean showpassword = true;

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

    }


    public void login(View view) {
        String getUsername = username.getText().toString();
        String getPassword = password.getText().toString();

        if(getUsername.isEmpty()){
            function.getInstance(this).Error("Username","Please Enter your username");
            username.requestFocus();
        }
        else if(getPassword.isEmpty()){
            function.getInstance(this).Error("Password","Please Enter your Password");
            password.requestFocus();
        }
        else{

            function.getInstance(this).loading();
            login(getUsername,getPassword);
        }


    }




    protected void login(String getusername,String getpassword){

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
                function.getInstance(MainActivity.this).toast(result);
                function.pDialog.dismissWithAnimation();
            }
        };
        _login get = new _login(getusername,getpassword,response,errorListener);
        get.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(get);
    }


    @SuppressLint("ClickableViewAccessibility")
    protected void togglePassword(EditText editText){
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