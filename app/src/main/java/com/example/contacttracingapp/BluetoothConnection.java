package com.example.contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothConnection extends AppCompatActivity {


    function controller;
    @BindView(R.id.bluetooth)
    ListView listData;
    List<String> s = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        ButterKnife.bind(this);
        controller = new function(this);

        Bluetooth();
    }


    private void Bluetooth(){
        s.clear();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
        else{
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            controller.mBluetoothAdapter = defaultAdapter;
            if (defaultAdapter == null) {
                controller.toastip(R.raw.error_con,"No Bluetooth Adapter found");
            }
            if (controller.mBluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 0);
            }
            Set<BluetoothDevice> bondedDevices = controller.mBluetoothAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice next : bondedDevices) {
                    s.add(next.getName()); //+ "-" + next.getAddress()
                }

                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,s);
                listData .setAdapter(adapter);

                listData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String data = adapter.getItem(i);
                        Toast.makeText(getApplicationContext(), adapter.getItem(i), Toast.LENGTH_SHORT).show();
                        controller.SETBLUETOOTHDEVICE(Arrays.asList(data,""));
                        controller.toastip(R.raw.ok,data);
                    }
                });


            }
        }


    }
}