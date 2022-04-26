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

import com.google.android.material.card.MaterialCardView;
import com.razo.contacttracingapp.GetterSetter.gs_received_vitamins;
import com.razo.contacttracingapp.GetterSetter.gs_scanned_all;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.animation.Mybounce;

import java.util.List;

public class Adapter_received extends RecyclerView.Adapter<Adapter_received.ViewHolder> {
    Context mContext;
    Animation myAnim;
    List<gs_received_vitamins> newsList;


    public Adapter_received(List<gs_received_vitamins> list, Context context) {
        super();
        this.newsList = list;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_received_vitamins,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final gs_received_vitamins getData = newsList.get(position);

        myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
        Mybounce interpolator = new Mybounce(0.2, 20);
        myAnim.setInterpolator(interpolator);
        holder.card.setAnimation(myAnim);
        holder.qty.setText("Quantity : "+getData.getQty());
        holder.name.setText("Employee Name : "+getData.getName());
        holder.date.setText("Received Date : "+getData.getDate());

    }

    @Override
    public int getItemCount() {
        return newsList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

       public TextView name,qty,date;
       public MaterialCardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            card = itemView.findViewById(R.id.card);
            qty = itemView.findViewById(R.id.qty);


        }
    }
}
