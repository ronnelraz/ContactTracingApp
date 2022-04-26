package com.razo.contacttracingapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.razo.contacttracingapp.GetterSetter.gs_area;
import com.razo.contacttracingapp.R;
import com.razo.contacttracingapp.function;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class Adapter_area extends RecyclerView.Adapter<Adapter_area.ViewHolder> {
    Context mContext;
    public int mSelectedItem = -1;
    List<gs_area> newsList;
    MaterialButton ok;
    AlertDialog alert;
    TextView address;

    public Adapter_area(List<gs_area> list, Context context, MaterialButton ok, AlertDialog alert, TextView address) {
        super();
        this.newsList = list;
        this.mContext = context;
        this.ok = ok;
        this.alert = alert;
        this.address = address;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_areas,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final gs_area getData = newsList.get(position);
        holder.item_areas.setText(getData.getNAME() + " - Location Name");
        holder.item_areas.setChecked(position == mSelectedItem);
        ok.setOnClickListener(v -> {
           if(mSelectedItem != -1){
                String getSelected = newsList.get(mSelectedItem).getNAME();
                String getShortCode = newsList.get(mSelectedItem).getID();
                function.getInstance(v.getContext()).setSelectedAreas(getSelected,getShortCode);
                alert.dismiss();
                address.setText(getSelected);
                function.getInstance(v.getContext()).SuccessResponse("Save" ,v.getContext());
           }

        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

       public RadioButton item_areas;

        public ViewHolder(View itemView) {
            super(itemView);
            item_areas = itemView.findViewById(R.id.item_areas);
            View.OnClickListener clickListener = v -> {
                ok.setEnabled(true);
                mSelectedItem = getAdapterPosition();
                notifyDataSetChanged();
            };
            itemView.setOnClickListener(clickListener);
            item_areas.setOnClickListener(clickListener);

        }
    }
}
