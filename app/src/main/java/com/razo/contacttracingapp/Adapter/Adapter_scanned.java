package com.razo.contacttracingapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.razo.contacttracingapp.GetterSetter.gs_scanned_all;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.animation.Mybounce;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class Adapter_scanned extends RecyclerView.Adapter<Adapter_scanned.ViewHolder> {
    Context mContext;
    Animation myAnim;
    List<gs_scanned_all> newsList;


    public Adapter_scanned(List<gs_scanned_all> list, Context context) {
        super();
        this.newsList = list;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_record_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final gs_scanned_all getData = newsList.get(position);

        myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
        Mybounce interpolator = new Mybounce(0.2, 20);
        myAnim.setInterpolator(interpolator);
        holder.card.setAnimation(myAnim);

        holder.category.setText(getData.getType());
        holder.temp.setText(getData.getTemp() + "°C");
        holder.name.setText(getData.getName());
        holder.company.setText(getData.getCompany());
        holder.date.setText(getData.getDate());



        if(getData.getVaccine().equals("No")){
            holder.vaccine.setTextColor(Color.parseColor("#e74c3c"));
            holder.vaccine.setText("Vaccinated : "+getData.getVaccine());
        }
        else{
            holder.vaccine.setTextColor(Color.parseColor("#2ecc71"));
            holder.vaccine.setText("Vaccinated : "+getData.getVaccine());
        }

        String ifpassed = Double.parseDouble(getData.getTemp()) >= 37.5 ? "Warning" : "Passed";
        int iconpassed = Double.parseDouble(getData.getTemp()) >= 37.5 ? R.drawable.warning_1 : R.drawable.icons8_ok_3;
        Drawable img = mContext.getResources().getDrawable(iconpassed);
        holder.passed.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        holder.passed.setText(ifpassed);

        if(Double.parseDouble(getData.getTemp()) >= 37.5){
            holder.card.setBackgroundColor(Color.parseColor("#F4E2E2"));
        }


    }

    @Override
    public int getItemCount() {
        return newsList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

       public TextView category,temp,passed,name,company,date,vaccine;
       public MaterialCardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            temp = itemView.findViewById(R.id.temp);
            passed = itemView.findViewById(R.id.passed);
            name = itemView.findViewById(R.id.name);
            company = itemView.findViewById(R.id.company);
            date = itemView.findViewById(R.id.date);
            card = itemView.findViewById(R.id.card);
            vaccine = itemView.findViewById(R.id.vaccine);


        }
    }
}
