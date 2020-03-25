package com.example.bebo2.studio_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class CustomAdapter_Department extends ArrayAdapter<String> {
    ArrayList<String> contry_models;
    Context context;
    public CustomAdapter_Department(Context context, ArrayList<String> depart_models ) {
        super(context, 0,depart_models);
        this.context = context;
        this.contry_models = depart_models;

    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater =LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_lay, parent, false);
        }






            TextView drinkName = (TextView) row.findViewById(R.id.textname);


            if(drinkName != null){
                drinkName.setText(contry_models.get(position));

                Log.d("find me ", drinkName.toString());

        }

        return row;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {
        LayoutInflater layoutInflater =  LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.row_lay,parent,false);

        TextView id = (TextView)convertView.findViewById(R.id.textname);

        id.setText(contry_models.get(position));

        return convertView;
    }
}
