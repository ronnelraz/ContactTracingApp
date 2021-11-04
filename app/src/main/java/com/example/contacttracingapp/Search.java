package com.example.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.contacttracingapp.Adapter.Adapter_Search;
import com.example.contacttracingapp.Adapter.Adapter_scanned;
import com.example.contacttracingapp.GetterSetter.gs_allRegistered;
import com.example.contacttracingapp.GetterSetter.gs_scanned_all;
import com.example.contacttracingapp.config.all_registered;
import com.example.contacttracingapp.config.all_scanned;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Search extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    function controller;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<gs_allRegistered> list = new ArrayList<>();
    @BindView(R.id.loading)
    LottieAnimationView loading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        controller = new function(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
              startActivity(new Intent(Search.this, Home.class));
            }
        });


        list = new ArrayList<>();
        recyclerView = findViewById(R.id.data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(999999999);

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_Search(list,getApplicationContext());
        recyclerView.setAdapter(adapter);

        AllData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem searchss = menu.findItem(R.id.search);
        MenuItem refreshss = menu.findItem(R.id.refresh);

        refreshss.setOnMenuItemClickListener(menuItem -> {
           try{
               AllData();
           }catch (Exception e){

           }
            return true;
        });

        ((SearchView) MenuItemCompat.getActionView(searchss)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList arrayList = new ArrayList();
                for (gs_allRegistered next : list) {
                    if (next.getName().toLowerCase().contains(newText)) {
                        arrayList.add(next);
                    }
                    adapter = new Adapter_Search(arrayList,getApplicationContext());
                    recyclerView.setAdapter(adapter);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    protected void AllData(){
        try {
            list.clear();
            Response.Listener<String> response = response1 -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response1);
                    boolean success = jsonResponse.getBoolean("success");
                    JSONArray array = jsonResponse.getJSONArray("data");



                    if(success){
                        loading.setVisibility(View.GONE);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            gs_allRegistered item = new gs_allRegistered(
                                    object.getString("type"),
                                    object.getString("company_name"),
                                    object.getString("plate_number"),
                                    object.getString("name"),
                                    object.getString("gender"),
                                    object.getString("dob"),
                                    object.getString("age"),
                                    object.getString("address"),
                                    object.getString("contact_no"),
                                    object.getString("lname"),
                                    object.getString("fname"),
                                    object.getString("img"),
                                    object.getString("id"),
                                    object.getString("province"),
                                    object.getString("city"),
                                    object.getString("brgy"),
                                    object.getString("vaccinated")
                            );

                            list.add(item);
                        }

                        adapter = new Adapter_Search(list,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            Response.ErrorListener errorListener = error -> {
                String result = function.getInstance(getApplicationContext()).Errorvolley(error);
                function.getInstance(getApplicationContext()).toastip(R.raw.error_con,result);
                loading.setVisibility(View.VISIBLE);
                no_connection();
            };
            all_registered get = new all_registered(response,errorListener);
            RequestQueue queue = Volley.newRequestQueue(Search.this);
            queue.add(get);
        } catch(Exception e){
            System.out.println("error");
        }

    }

    protected void no_connection(){
        loading.setAnimation(R.raw.no_connection);
        loading.playAnimation();
        loading.loop(true);
    }




}