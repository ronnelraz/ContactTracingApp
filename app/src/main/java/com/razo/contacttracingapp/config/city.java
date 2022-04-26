package com.razo.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class city extends StringRequest {

    public static final String con = config.URL + "city";
    private Map<String,String> params;

    public city(String subid,Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("id",subid);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
