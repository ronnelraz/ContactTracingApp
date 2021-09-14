package com.example.contacttracingapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Home extends AppCompatActivity {

    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;

    public static FragmentManager fragmentManager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nvView)
    NavigationView navigationView;
    @BindView(R.id.flContent)
    FrameLayout container;

    TextView fullname, address;


    //qrcode
    CodeScanner mCodeScanner;
    ArrayList<String> qrcodeData = new ArrayList<>();
    //textwatcher
     private boolean enabledSaveQrcodeScan = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        function.getInstance(this);
        drawableMenu();
    }

        protected  void drawableMenu(){
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor("#ffffff"));
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            View headerView = navigationView.getHeaderView(0);
            fullname = headerView.findViewById(R.id.fullname);
            address = headerView.findViewById(R.id.address);
            fullname.setText("Test");
            address.setText("Address");



            navigationView.setNavigationItemSelectedListener(item -> {

                switch (item.getItemId()){
                    case R.id.scan:
                        scanQrcode(getApplicationContext());
                        break;
                    case R.id.register:
                        function.intent(Register.class,Home.this);
                        break;



//                        case R.id.logout:
//                            new SweetAlertDialog(Home.this, SweetAlertDialog.WARNING_TYPE)
//                                    .setTitleText("Are you sure?")
//                                    .setContentText("You want to logout your account?")
//                                    .setConfirmText("Yes")
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sDialog.dismissWithAnimation();
////                                        islogin("false");
////                                        function.intent(Login.class,Home.this);
////                                        animIntent(Home.this,config.rtl);
//                                        }
//                                    })
//                                    .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sDialog.dismissWithAnimation();
//                                        }
//                                    })
//                                    .show();
//                            break;
                }

                return true;
            });

        }


        private void FragmentActivity(Fragment fragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.flContent,fragment).commit();
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START,true);
            }
        }

        private void scanQrcode(Context context) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                                AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
                                View vs = LayoutInflater.from(Home.this).inflate(R.layout.qrscanner_layout, null);

                                CodeScannerView scanner = vs.findViewById(R.id.scanner_view);
                                mCodeScanner = new CodeScanner(getApplicationContext(), scanner);

                                mCodeScanner.startPreview();



                                dialog.setView(vs);
                                AlertDialog alert = dialog.create();

                                mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
                                    MediaPlayer mp = MediaPlayer.create(Home.this,R.raw.scan);
                                    mp.start();
//                                Toast.makeText(Home.this, result.getText(), Toast.LENGTH_SHORT).show();
                                    Log.d("data",result.getText());
                                    String[] data = result.getText().split(",");

                                    //add array method tamad kasi ako
                                    addlistQrcode(Arrays.asList(data[3],data[5],data[7],data[9],data[11],data[13],data[15],data[17],data[19],data[21],data[23]));
                                    if(data[1].equals("true")){
                                        if(data[0].equals("cpfp")){

                                            open_modal_temperature(Arrays.asList(
                                                    qrcodeData.get(0),
                                                    qrcodeData.get(1),
                                                    qrcodeData.get(2),
                                                    qrcodeData.get(3),
                                                    qrcodeData.get(4),
                                                    qrcodeData.get(5),
                                                    qrcodeData.get(6),
                                                    qrcodeData.get(7),
                                                    qrcodeData.get(8),
                                                    qrcodeData.get(9),
                                                    qrcodeData.get(10)));

                                        }
                                        else{
                                            function.getInstance(getApplicationContext()).toast("Invalid QR Code");
                                        }
                                    }
                                    else{
                                        function.getInstance(getApplicationContext()).toast("Invalid QR Code");
                                    }

                                    alert.dismiss();
                                }));

                                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alert.setCanceledOnTouchOutside(false);
                                alert.setCancelable(true);
                                alert.show();

                            }
                            else if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())

                    .onSameThread()
                    .check();
        }



        private void addlistQrcode(List<String> data){
            qrcodeData.addAll(data);
        }

        @SuppressLint("SetTextI18n")
        private void open_modal_temperature(List<String> data){
            AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
            View vs = LayoutInflater.from(Home.this).inflate(R.layout.modal_tempareture, null);

//            19/06/2021,
//            Employee,
//            CHAROEN POKPHAND FOODS PHILIPPINES,
//            RONNEL,
//            RAZO,
//            PANGASINAN MAPANDAN LAMBAYAN,
//            09195464878,
//            NXA-1234,
//            MR,
//            12/05/1996,
//            24,

            TextView fullname = vs.findViewById(R.id.fullname);
            EditText temperature = vs.findViewById(R.id.temperature);
            MaterialButton saveScan = vs.findViewById(R.id.saveScan);
            fullname.setText(data.get(8) + " " + data.get(3) + " " + data.get(4));

            tempTextWatcher(temperature,saveScan);

            dialog.setView(vs);
            AlertDialog alert = dialog.create();
            saveScan.setOnClickListener(v -> {
                double temp = Double.parseDouble(temperature.getText().toString());
                if(temp <= 30.0){
                    AlertDialog(R.drawable.low,"Low Temperature!","Low");
                }
                else if(temp >= 37.5){
                    AlertDialog(R.drawable.warning,"High Temperature!","High");
                }
                else{
                    AlertDialog(R.drawable.passed,"Normal Temperature!","Normal");
                }
                alert.dismiss();
            });
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(true);
            alert.show();
        }


        protected void AlertDialog(int icon,String msg,String title){
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText(title)
                    .setContentText(msg)
                    .setCustomImage(icon)
                    .show();
        }


        private void tempTextWatcher(EditText editText,MaterialButton save){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length() != 0){
                        double temp = Double.valueOf(charSequence.toString());
                        if(temp <= 10.0 || temp >= 50.00){
                            save.setEnabled(false);
                        }
                        else{
                            save.setEnabled(true);
                        }
                    }
                    else{
                        save.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

        private void showSettingsDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Need Permissions");
            builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
            builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }


}