package com.razo.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class con_imgUpdate extends StringRequest {

    public static final String con = config.URL + "img";
    private Map<String,String> params;

    public con_imgUpdate(String id,String img,Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("id",id);
        params.put("img",img);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
