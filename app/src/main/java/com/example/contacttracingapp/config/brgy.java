package com.example.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class brgy extends StringRequest {

    public static final String con = config.URL + "brgy";
    private Map<String,String> params;

    public brgy(String subid, Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("id",subid);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
