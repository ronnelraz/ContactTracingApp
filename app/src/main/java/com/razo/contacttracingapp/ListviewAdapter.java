package com.razo.contacttracingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.razo.contacttracingapp.R;

import java.util.List;

public class ListviewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    List<String> list;

    public ListviewAdapter(Activity context, List<String> list) {
        super(context, R.layout.custom_company_list, list);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.list=list;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.custom_company_list, null,true);
        TextView companylist = rowView.findViewById(R.id.item);
        companylist.setText(list.get(position));
        return rowView;

    };
}
