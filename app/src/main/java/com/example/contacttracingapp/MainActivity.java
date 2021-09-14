package com.example.contacttracingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.password)
    EditText password;
    private  boolean showpassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        togglePassword(password);
    }


    public void login(View view) {
        function.intent(Home.class,view.getContext());
    }


    @SuppressLint("ClickableViewAccessibility")
    protected void togglePassword(EditText editText){
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    if(showpassword){
                        showpassword = false;
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        editText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(),R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.invisible), null);
                    }
                    else{
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext(),R.drawable.password), null, ContextCompat.getDrawable(getApplicationContext(),R.drawable.visible), null);
                        showpassword = true;
                    }
                    return true;
                }
            }
            return false;
        });
    }


}