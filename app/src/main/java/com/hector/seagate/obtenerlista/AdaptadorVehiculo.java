package com.hector.seagate.obtenerlista;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
//ver http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
/**
 * Created by seagate on 02/06/2017.
 */
public class AdaptadorVehiculo extends ArrayAdapter<Vehiculo> {

    Context context;
    int layoutResourceId;
    ArrayList<Vehiculo> data = null;

    public AdaptadorVehiculo(Context context, int layoutResourceId, ArrayList<Vehiculo> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VehiculoHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new VehiculoHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (VehiculoHolder)row.getTag();
        }

        Vehiculo vehiculo = data.get(position);
        holder.txtTitle.setText(vehiculo.title);
        holder.imgIcon.setImageResource(vehiculo.icon);

        return row;
    }

    static class VehiculoHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}