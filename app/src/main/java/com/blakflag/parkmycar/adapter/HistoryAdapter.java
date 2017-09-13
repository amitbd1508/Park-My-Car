package com.blakflag.parkmycar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.activity.ParkRequstActivity;
import com.blakflag.parkmycar.model.History;
import com.blakflag.parkmycar.model.ParkingRequst;
import com.blakflag.parkmycar.util.App;

import java.util.List;

/**
 * Created by Amit on 9/9/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    List<History> histories;
    Context context;

    public HistoryAdapter(List<History> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rent_history_item, parent, false);

        return new HistoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //bind your history
        holder.price.setText("Price : "+histories.get(position).price);
        if(App.user.type.equals(App.CN_USER))
            holder.name.setText(histories.get(position).parkOwnerName);
        else
            holder.name.setText(histories.get(position).carOwnerName);
        holder.description.setText("Payment Method : Cash"+"\n"+"Total Time : "+histories.get(position).totaltime);


    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        TextView description,name,price;

        public ViewHolder(View itemView) {
            super(itemView);

            description= (TextView) itemView.findViewById(R.id.description);
            price= (TextView) itemView.findViewById(R.id.price);
            name= (TextView) itemView.findViewById(R.id.name);
        }
    }
}
