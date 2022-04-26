package com.razo.contacttracingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.razo.contacttracingapp.GetterSetter.gs_trucking;
import com.razo.contacttracingapp.R;

import java.util.List;

public class adapter_trucking extends BaseAdapter {

    private Context context;
    private List<gs_trucking> list;

    public adapter_trucking(Context context,List<gs_trucking> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = LayoutInflater.from(context).inflate(R.layout.custom_company_list, parent, false);
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_company_list,parent,false);
        TextView item = convertView.findViewById(R.id.item);
        final gs_trucking getData = list.get(position);
        item.setText(getData.getTrucking_agency());

        // returns the view for the current row
        return convertView;
    }

}
