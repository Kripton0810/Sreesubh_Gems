package com.sreesubh.Gems;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttandanceAdapterView extends RecyclerView.Adapter<AttandanceAdapterView.ViewHolder> {
    List<AttandanceModelClass> list;

    public AttandanceAdapterView(List<AttandanceModelClass> list) {
        this.list = list;
    }

    @NonNull
    
    @Override
    public AttandanceAdapterView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.updated_attandance_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AttandanceAdapterView.ViewHolder holder, int position) {
        String ltime = list.get(position).getLtime();
        String lotime = list.get(position).getLotime();
        String date = list.get(position).getDate();
        String  status = list.get(position).getStatus();
        String latein = list.get(position).getLatein();
        String earlyout = list.get(position).getEarlyout();
        String worktime = list.get(position).getWorktime();
        String ot = list.get(position).getOt();
        holder.setData(ltime,lotime,date,status,latein,earlyout,worktime,ot);
        //              0     1      2     3      4       5       6       7
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ltime,lotime,date,woot,lineout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ltime = itemView.findViewById(R.id.login_time);
            lotime = itemView.findViewById(R.id.logout_time);
            date = itemView.findViewById(R.id.date_attandance);
            woot = itemView.findViewById(R.id.woot);
            lineout = itemView.findViewById(R.id.lineout);

        }
        public void setData(String... data)
        {
            String lf = "",lfo="";

            if (data[3].equals("P"))
            {
                ltime.setText(data[0]);
                lotime.setText(data[1]);
                date.setText(data[2]);
                if (data[1].equals("null"))
                {
                    lotime.setText("--");
                }
                if (data[4].charAt(0)=='-')
                {
                    lf = "00:00/";
                }
                else
                {
                    lf = data[4]+"/";
                }
                if (data[5].charAt(0)=='-')
                {
                    lf += "00:00";
                }
                else
                {
                    lf += data[5];
                }
                lineout.setText(lf);




                lfo = data[6];
                woot.setText(lfo);

            }
            else if(data[3].equals("A"))
            {

                ltime.setText("-A-");
                lotime.setText("-A-");
                date.setText(data[2]);
            }

            else if(data[3].equals("L"))
            {

                ltime.setText("-L-");
                lotime.setText("-L-");
                date.setText(data[2]);
            }
        }
    }
}
