package com.example.contacttracingapp.Adapter;

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
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.contacttracingapp.Blocklist;
import com.example.contacttracingapp.Blocklist_details;
import com.example.contacttracingapp.GetterSetter.gs_blocklist;
import com.example.contacttracingapp.GetterSetter.gs_scanned_all;
import com.example.contacttracingapp.R;
import com.example.contacttracingapp.animation.Mybounce;
import com.example.contacttracingapp.function;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class Adapter_block extends RecyclerView.Adapter<Adapter_block.ViewHolder> {
    Context mContext;
    List<gs_blocklist> newsList;


    public Adapter_block(List<gs_blocklist> list, Context context) {
        super();
        this.newsList = list;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_blocklist,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final gs_blocklist getData = newsList.get(position);

        holder.company.setText(getData.getCompany());
        holder.name.setText(getData.getName());
        holder.card.setOnClickListener(v -> {
            Blocklist_details.str_id = getData.getId();
            Blocklist_details.str_name = getData.getName();
            Blocklist_details.str_category = getData.getType();
            Blocklist_details.str_company = getData.getCompany();
            Blocklist_details.str_plate = getData.getPlate();
            Blocklist_details.str_banType = getData.getBan_type();
            Blocklist_details.str_start = getData.getStart();
            Blocklist_details.str_end = getData.getEnd();
            Blocklist_details.str_remark = getData.getRemark();
            function.intent(Blocklist_details.class,v.getContext());
        });


    }

    @Override
    public int getItemCount() {
        return newsList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

       public TextView name,company;
       public MaterialCardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            company = itemView.findViewById(R.id.company);
            card = itemView.findViewById(R.id.card);


        }
    }
}
