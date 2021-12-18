package com.sreesubh.Gems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.ViewHolder> {
    List<HolidayModelView> list;

    public HolidayAdapter(List<HolidayModelView> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HolidayAdapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holiday_view,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  HolidayAdapter.ViewHolder holder, int position) {
        String name = list.get(position).getName();
        String day = list.get(position).getDay();
        String date = list.get(position).getDate();
        holder.setData(name,day,date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView holi_date,holi_day,holi_name;
        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            holi_date = itemView.findViewById(R.id.holi_date);
            holi_day = itemView.findViewById(R.id.holi_day);
            holi_name = itemView.findViewById(R.id.holi_name);

        }
        public void setData(String... data)
        {
            holi_name.setText(data[0]);
            holi_date.setText(data[2]);
            holi_day.setText(data[1]);
        }
    }
}
