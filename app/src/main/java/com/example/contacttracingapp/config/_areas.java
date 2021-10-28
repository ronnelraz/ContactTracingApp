package com.example.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class _areas extends StringRequest {

    public static final String con = config.URL + "areas";
    private Map<String,String> params;

    public _areas(String ID,Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("ID",ID);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
