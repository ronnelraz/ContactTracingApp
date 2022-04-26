package com.razo.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.Adapter.Adapter_block;
import com.razo.contacttracingapp.GetterSetter.gs_blocklist;
import com.razo.contacttracingapp.retroConfig.API;
import com.google.gson.Gson;
import com.novoda.merlin.Merlin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class Blocklist extends AppCompatActivity {

    private List<gs_blocklist> list;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    @BindView(R.id.loading)
    LottieAnimationView loading;

    @BindView(R.id.search)
    EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocklist);
        ButterKnife.bind(this);

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(999999999);

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_block(list,getApplicationContext());
        recyclerView.setAdapter(adapter);



        function.getInstance(this).merlin = new Merlin.Builder().withAllCallbacks().build(this);
        function.getInstance(this).merlin.registerConnectable(() -> {
            getBlockList();
            function.getInstance(this).toastip(R.raw.wifi,"Connecting to the server...");
        });

        function.getInstance(this).merlin.registerDisconnectable(() -> {
            getBlockList();
//            function.getInstance(this).toastip(R.raw.error_con,"Could not connect to the server.");
        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<gs_blocklist> newList = new ArrayList<>();

                for (gs_blocklist sub : list)
                {

                    String name = sub.getName().toLowerCase();
                    if(name.contains(charSequence)){
                        newList.add(sub);
                    }
                    adapter = new Adapter_block(newList, getApplicationContext());
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        function.getInstance(this).merlin.bind();
    }

    @Override
    protected void onPause() {
        function.getInstance(this).merlin.unbind();
        super.onPause();
    }


    private void getBlockList(){
        list.clear();
        API.getClient().Blocklisted().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                try {
                    JSONObject jsonResponse = new JSONObject(new Gson().toJson(response.body()));
                    boolean success = jsonResponse.getBoolean("success");
                    JSONArray array = jsonResponse.getJSONArray("data");
                    if(success){
                        loading.setVisibility(View.GONE);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            gs_blocklist item = new gs_blocklist(
                                    object.getString("id"),
                                    object.getString("name"),
                                    object.getString("type"),
                                    object.getString("company"),
                                    object.getString("plate"),
                                    object.getString("ban_type"),
                                    object.getString("start"),
                                    object.getString("end"),
                                    object.getString("remark")

                            );

                            list.add(item);
                        }

                        adapter = new Adapter_block(list,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }
                    else{
                        loading.setAnimation(R.raw.nodata);
                        loading.playAnimation();
                        loading.loop(false);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                if (t instanceof IOException) {
                    function.getInstance(getApplicationContext()).toastip(R.raw.error_con,t.getMessage());
                    loading.setVisibility(View.VISIBLE);
                    no_connection();
                }
            }
        });

    }

    protected void no_connection(){
        loading.setAnimation(R.raw.no_connection);
        loading.playAnimation();
        loading.loop(true);
    }

    public void back(View view) {
        function.intent(Home.class,view.getContext());
    }
}