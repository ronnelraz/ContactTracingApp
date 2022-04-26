package com.razo.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class loginAreaLocation extends StringRequest {

    public static final String con = config.URL + "loginAreaLocation";
    private Map<String,String> params;

    public loginAreaLocation(Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
