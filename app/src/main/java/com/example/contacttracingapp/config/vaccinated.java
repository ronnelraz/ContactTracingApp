package com.example.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class vaccinated extends StringRequest {

    public static final String con = config.URL + "vaccinated";
    private Map<String,String> params;

    public vaccinated(String contact, Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("contact",contact);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
