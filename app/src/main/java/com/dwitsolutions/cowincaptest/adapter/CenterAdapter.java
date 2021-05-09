package com.dwitsolutions.cowincaptest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dwitsolutions.cowincaptest.R;
import com.dwitsolutions.cowincaptest.model.Center;

import java.text.SimpleDateFormat;
import java.util.List;

public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.CenterViewHolder> {
    Context context;
    List<Center> centers;

    public CenterAdapter(Context context, List<Center> centers) {
        this.context = context;
        this.centers = centers;
    }

    @Override
    public CenterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.center_item, parent, false);
        return new CenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CenterAdapter.CenterViewHolder holder, int position) {
        Center center = centers.get(position);
        holder.centerAge.setText("Min Age " + center.getMinAge().toString());
        holder.centerAvl.setText("AVL " + center.getAvailableCapacity().toString());
        holder.centerName.setText(center.getName().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.date.setText("Date - " + simpleDateFormat.format(center.getDate()));
    }

    @Override
    public int getItemCount() {
        return centers.size();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder {
        TextView centerName;
        TextView centerAge;
        TextView centerAvl;
        TextView date;

        public CenterViewHolder(View itemView) {
            super(itemView);
            centerName = (TextView) itemView.findViewById(R.id.center_name);
            centerAge = (TextView) itemView.findViewById(R.id.age_TV);
            centerAvl = (TextView) itemView.findViewById(R.id.cap_TV);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}

