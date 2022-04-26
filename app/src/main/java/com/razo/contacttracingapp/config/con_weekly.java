package com.razo.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class con_weekly extends StringRequest {

    public static final String con = config.URL + "report_weekly_person_entrance";
    private Map<String,String> params;

    public con_weekly(String to,String from, String plantcode,Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("to",to);
        params.put("from",from);
        params.put("plantcode",plantcode);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
