package com.example.contacttracingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Inherited;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class Register extends AppCompatActivity{

    @BindView(R.id.opencamera) MaterialButton openCamera;
    @BindView(R.id.imageoutput) ImageView imageOutput;
    @BindViews({R.id.spinnerSelectCompany,R.id.company}) TextView[] SpinnerSelectCompany;
    @BindView(R.id.dob) EditText dob;
    @BindView(R.id.age) TextView age;
    private MaterialButton ActionbButton;
    private RadioButton optionRadio;
    private  String currentPhotoPath;
    private Bitmap bitmap;

    final Calendar calendar = Calendar.getInstance();
    protected String strSelectedDate;
    private String current = "";
    private String ddmmyyyy = "ddmmyyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        OpenCameraModal();

        onRadioButtonCheckChanged(optionRadio, false);
        ViewDob();
        ActionbuttonFunct(ActionbButton);

    }

    @OnClick({R.id.save,R.id.close})
    protected void ActionbuttonFunct(MaterialButton button){
        if(button.getId() == R.id.save){

        }

    }




    @SuppressLint("ClickableViewAccessibility")
    protected void ViewDob(){
        dob.setOnTouchListener((v, event) -> {
//                0 left, 1 top, 2 right, 3 bttom;
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (dob.getRight() - dob.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    new DatePickerDialog(v.getContext(),R.style.picker,getDateto(), calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                    return true;
                }
            }
            return false;
        });


        /**Ontextchanged**/
        dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int il, int i1, int i2) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                        age.setText("");
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        calendar.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        calendar.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > calendar.getActualMaximum(Calendar.DATE))? calendar.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    dob.setText(current);
                    dob.setSelection(sel < current.length() ? sel : current.length());
                    String[] getDOB = dob.getText().toString().split("/");

                    if(getDOB[2].length() >= 4){
                        if (getDOB[2].matches("[0-9]+")) {
                            setAge(Integer.parseInt(getDOB[2]),Integer.parseInt(getDOB[1]),Integer.parseInt(getDOB[0]));
                        }
                    }


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void setAge(int year, int month, int day){
//        Calendar dob = Calendar.getInstance();
//        Calendar today = Calendar.getInstance();
//
//        dob.set(year, month, day);
//
//        int setage = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
//
//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
//            setage--;
//        }
//
//        Integer ageInt = new Integer(setage);
//        String ageS = ageInt.toString();
//        age.setText(ageS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            age.setText(String.valueOf(Period.between(LocalDate.of(year, month, day),LocalDate.now()).getYears()));
        }

    }

    private DatePickerDialog.OnDateSetListener getDateto(){
        DatePickerDialog.OnDateSetListener date = (view1, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Formatter();
        };
        return date;
    }

    private void Formatter() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(sdf.format(calendar.getTime()));

//        String FilterFormat = "yyyy-M-d";
//        SimpleDateFormat sdffilter = new SimpleDateFormat(FilterFormat, Locale.US);
//        function.toast(getActivity(),sdffilter.format(myCalendar.getTime()));
//        stc_date_to = sdffilter.format(myCalendar.getTime());
    }




    @OnCheckedChanged({R.id.rad1,R.id.rad2,R.id.rad3,R.id.rad4})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if(checked) {
            switch (button.getId()) {
                case R.id.rad1:
                    SpinnerSelectCompany[0].setVisibility(View.GONE);
                    SpinnerSelectCompany[1].setVisibility(View.VISIBLE);
                    SpinnerSelectCompany[1].setText("");
                    SpinnerSelectCompany[1].setEnabled(true);
                    break;

                case R.id.rad2:

                    SpinnerSelectCompany[1].setVisibility(View.GONE);
                    SpinnerSelectCompany[0].setVisibility(View.VISIBLE);
                    break;
                case R.id.rad3:
                    SpinnerSelectCompany[1].setText("CHAROEN POKPHAND FOODS PHILIPPINES");
                    SpinnerSelectCompany[1].setEnabled(false);
                    SpinnerSelectCompany[0].setVisibility(View.GONE);
                    SpinnerSelectCompany[1].setVisibility(View.VISIBLE);
                    break;

                case R.id.rad4:
                    SpinnerSelectCompany[1].setVisibility(View.GONE);
                    SpinnerSelectCompany[0].setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    protected void OpenCameraModal(){
        openCamera.setOnClickListener(v -> {

                String fileName = "Photo";
                File storageDirectory = getExternalFilesDir((Environment.DIRECTORY_PICTURES));
            try {
                File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                currentPhotoPath = imageFile.getAbsolutePath();
                Uri imageUri = FileProvider.getUriForFile(Register.this,"com.example.contacttracingapp.fileprovider",imageFile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,1);


            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageOutput.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}