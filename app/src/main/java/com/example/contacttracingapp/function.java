package com.example.contacttracingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class function {

    private static function app;
    private static Context cont;
    public static SweetAlertDialog pDialog;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String DATA = "data";
    private static final String USER_ID = "ID";
    private static final String FULLNAME = "FULLNAME";
    private static final String AD       = "AD";
    private static final String LOGIN = "false";
    private static final String SET_AREA = "AREA";
    private static final String SET_SHORTCODE = "AREA_CODE";


    public function(Context context){
        cont = context;
    }

    public static synchronized function getInstance(Context context){
        if(app == null){
            app = new function(context);
        }
        return app;
    }


    public boolean setSelectedAreas(String area,String area_code){
        sharedPreferences = cont.getSharedPreferences(DATA,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(SET_AREA,area);
        editor.putString(SET_SHORTCODE,area_code);
        editor.apply();
        return true;
    }

    public String getAreas(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SET_AREA,"");
    }

    public String getAreas_code(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SET_SHORTCODE,"");
    }




    public boolean setAccount(List<String> list){
        sharedPreferences = cont.getSharedPreferences(DATA,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(USER_ID,list.get(0));
        editor.putString(FULLNAME,list.get(1));
        editor.putString(LOGIN,list.get(2));
        editor.putString(AD,list.get(3));
        editor.apply();
        return true;
    }


    public String login(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AD,"");
    }

    public String getUserId(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID,"");
    }

    public String getFullname(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FULLNAME,"");
    }

    public boolean ifLogin(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LOGIN,"").equals("false") ? false : true;
    }

    public static void intent(Class<?> activity, Context context){
        Intent i = new Intent(context,activity);
        context.startActivity(i);
    }

    public void toast(String msg){
        Toast.makeText(cont,msg,Toast.LENGTH_SHORT).show();
    }


    public void Error(String title,String content){
        new SweetAlertDialog(cont, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .show();
    }

    public void SuccessResponse(String msg,Context context){
        new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(msg)
                .show();
    }


    public void errorEffect(){
        MediaPlayer err = MediaPlayer.create(cont,R.raw.error_con);
        err.start();
    }

    public String Errorvolley(VolleyError volleyError){
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        return message;
    }




    public void loading(){
       pDialog = new SweetAlertDialog(cont, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Please Wait...");
        pDialog.setCancelable(true);
        pDialog.show();
    }
}
