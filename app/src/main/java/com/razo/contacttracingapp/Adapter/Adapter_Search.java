package com.razo.contacttracingapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.razo.contacttracingapp.Details;
import com.razo.contacttracingapp.GetterSetter.gs_allRegistered;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.function;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class Adapter_Search extends RecyclerView.Adapter<Adapter_Search.ViewHolder> {
    Context mContext;
    Animation myAnim;
    List<gs_allRegistered> newsList;


    public Adapter_Search(List<gs_allRegistered> list, Context context) {
        super();
        this.newsList = list;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final gs_allRegistered getData = newsList.get(position);

//        myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
//        Mybounce interpolator = new Mybounce(0.2, 20);
//        myAnim.setInterpolator(interpolator);
//        holder.card.setAnimation(myAnim);

        holder.name.setText(getData.getGender() + " " + getData.getName()); //+ " " + getData.getLname()
        holder.address.setText("Address : " + getData.getAddress());
        holder.company.setText("Company : " + getData.getCn());

        holder.card.setOnClickListener(v -> {
            Details.type = getData.getType();
            Details.cn = getData.getCn();
            Details.plate = getData.getPlate();
            Details.fname = getData.getFname();
            Details.lname = getData.getLname();
            Details.gender = getData.getGender();
            Details.dob = getData.getDob();
            Details.age = getData.getAge();
            Details.address = getData.getAddress();
            Details.contact = getData.getContact();
            Details.img = getData.getImg();
            Details.id = getData.getId();
            Details.privince = getData.getProvince();
            Details.city = getData.getCity();
            Details.brgy = getData.getBrgy();
            Details.vaccinated = getData.getVaccinated();
            function.intent(Details.class,v.getContext());
        });






    }

    @Override
    public int getItemCount() {
        return newsList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

       public TextView name,address,company;
       public ImageView icon;
       public MaterialCardView card;
       public LottieAnimationView loading;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            address = itemView.findViewById(R.id.address);
            name = itemView.findViewById(R.id.name);
            company = itemView.findViewById(R.id.company);
            card = itemView.findViewById(R.id.card);
            loading = itemView.findViewById(R.id.loading);


        }
    }
}
