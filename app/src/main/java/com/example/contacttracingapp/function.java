package com.example.contacttracingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class function {

    public static function app;
    public static Context cont;


    public function(Context context){
        cont = context;
    }

    public static synchronized function getInstance(Context context){
        if(app == null){
            app = new function(context);
        }
        return app;
    }

    public static void intent(Class<?> activity, Context context){
        Intent i = new Intent(context,activity);
        context.startActivity(i);
    }

    public void toast(String msg){
        Toast.makeText(cont,msg,Toast.LENGTH_SHORT).show();
    }
}
