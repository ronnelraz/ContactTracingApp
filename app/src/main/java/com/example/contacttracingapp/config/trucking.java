package com.example.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class trucking extends StringRequest {

    public static final String con = config.URL + "trucking";
    private Map<String,String> params;

    public trucking(Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
