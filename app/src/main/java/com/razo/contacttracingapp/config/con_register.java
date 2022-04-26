package com.razo.contacttracingapp.config;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class con_register extends StringRequest {

    public static final String con = config.URL + "appscann_new";
    private Map<String,String> params;

    public con_register(
            String type,String cn,String empid,String plate,String name,String lname,String gender,
            String dob,String age,String address,String contact,String AD,String pro,String mun,String brgy,
            String img,String plantcode,String vaccine,
            Response.Listener<String> Listener, Response.ErrorListener errorListener){
        super(Method.POST,con,Listener,errorListener);
        params = new HashMap<>();
        params.put("type", type);
        params.put("cn", cn);
        params.put("empid", empid);
        params.put("plate", plate);
        params.put("name", name);
        params.put("lname", lname);
        params.put("gender", gender);
        params.put("address", address);
        params.put("dob", dob);
        params.put("age", age);
        params.put("contact", contact);
        params.put("AD",AD);
        params.put("pro", pro);
        params.put("mun", mun);
        params.put("brgy", brgy);
        params.put("img", img);
        params.put("plantcode",plantcode);
        params.put("vaccine",vaccine);
    }

    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
