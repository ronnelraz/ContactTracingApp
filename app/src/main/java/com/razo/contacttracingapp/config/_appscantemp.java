package com.razo.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class _appscantemp extends StringRequest {

    public static final String con = config.URL + "appscantemp";
    private Map<String,String> params;

    public _appscantemp(String name,String lname,String AD,String contact,String temp, String vaccine,Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("name",name);
        params.put("lname",lname);
        params.put("AD",AD);
        params.put("contact",contact);
        params.put("temp",temp);
        params.put("vaccine",vaccine);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
