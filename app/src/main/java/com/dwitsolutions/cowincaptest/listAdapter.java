package com.dwitsolutions.cowincaptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dwitsolutions.cowincaptest.model.Center;

import java.text.SimpleDateFormat;
import java.util.List;

public class listAdapter extends BaseAdapter {

    List<Center> centerList;
    Context context;

    public listAdapter(@NonNull Context context, List<Center> _centerList) {
        super();
        this.centerList = _centerList;
        this.context = context;

    }
    @Override
    public int getCount() {
        return centerList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.center_item, parent, false);
        }
        // Lookup view for data population
        TextView centerName = (TextView) convertView.findViewById(R.id.center_name);
        TextView centerAge = (TextView) convertView.findViewById(R.id.age_TV);
        TextView centerAvl = (TextView) convertView.findViewById(R.id.cap_TV);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        // Populate the data into the template view using the data object

        centerName.setText(centerList.get(position).getName());
        centerAge.setText("Age"+centerList.get(position).getMinAge());
        centerAvl.setText("AVL"+centerList.get(position).getAvailableCapacity());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date.setText("Date - "+ simpleDateFormat.format(centerList.get(position).getDate()));

        // Return the completed view to render on screen
        return convertView;

    }
}
