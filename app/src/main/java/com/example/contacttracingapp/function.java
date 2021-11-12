package com.example.contacttracingapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.novoda.merlin.Merlin;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class function {

    private static function app;
    private static Context cont;
    public static SweetAlertDialog pDialog;
    public Merlin merlin;

    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothDevice mBluetoothDevice;
    public BluetoothSocket mBluetoothSocket;

    public static String[] BLUETOOTH = {"iMZ220-A", "iMZ220-B"};

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String DATA = "data";
    private static final String USER_ID = "ID";
    private static final String FULLNAME = "FULLNAME";
    private static final String AD       = "AD";
    private static final String LOGIN = "false";
    private static final String SET_AREA = "AREA";
    private static final String SET_SHORTCODE = "AREA_CODE";

    private static final String BLUETOOTHNAME = "BTNAME";
    private static final String BLUETOOTHADDRESS = "BTADDRESS";
    public static final String CONNECTION_BT = "0";


    public function(Context context){
        cont = context;
    }





    public String getBTAddress(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(BLUETOOTHADDRESS,"");
    }

    public String getBTName(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(BLUETOOTHNAME,"");
    }

    public String getAD(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AD,"");
    }

    public String getAREACODE(){
        sharedPreferences = cont.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SET_SHORTCODE,"");
    }

    public static synchronized function getInstance(Context context){
        if(app == null){
            app = new function(context);
        }
        return app;
    }

    public boolean SETBLUETOOTHDEVICE(List<String> data){
        sharedPreferences = cont.getSharedPreferences(DATA,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(BLUETOOTHNAME,data.get(0));
        editor.putString(BLUETOOTHADDRESS,data.get(1));
        editor.apply();
        return true;
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


    @BindViews({R.id.bantype,R.id.message,R.id.name,R.id.plate,R.id.remark,R.id.start,R.id.end,R.id.footer})
    TextView[] BanContent;
    public void BanModal(String type,String msg,String name,String plate,String remark,String start,String end,String footer){
        AlertDialog.Builder dialog = new AlertDialog.Builder(cont);
        View vs = LayoutInflater.from(cont).inflate(R.layout.ban_dialog, null);
        ButterKnife.bind(this,vs);
        MaterialButton ok = vs.findViewById(R.id.ok);

        BanContent[0].setText(type);
        BanContent[1].setText(msg);
        BanContent[2].setText(name);
        BanContent[3].setText(plate);
        BanContent[4].setText(remark);
        BanContent[5].setText(start);
        BanContent[6].setText(end);
        BanContent[7].setText(footer);



        dialog.setView(vs);
        AlertDialog alert = dialog.create();

        ok.setOnClickListener(v -> {
            alert.dismiss();
        });

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(true);
        alert.show();
    }




    public void toastip(int raw,String body){
        Toast toast = new Toast(cont);
        View vs = LayoutInflater.from(cont).inflate(R.layout.custom_toast, null);
        LottieAnimationView icon = vs.findViewById(R.id.icon);
        TextView msg = vs.findViewById(R.id.body);

        icon.setAnimation(raw);
        icon.loop(false);
        icon.playAnimation();
        msg.setText(body);
        toast.setGravity(Gravity.TOP|Gravity.RIGHT, 50, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(vs);
        toast.show();
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
