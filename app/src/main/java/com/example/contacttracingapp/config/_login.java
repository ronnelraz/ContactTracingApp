package com.example.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class _login extends StringRequest {

    public static final String con = config.URL + "login";
    private Map<String,String> params;

    public _login(String username, String password, Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("username",username);
        params.put("password",password);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
