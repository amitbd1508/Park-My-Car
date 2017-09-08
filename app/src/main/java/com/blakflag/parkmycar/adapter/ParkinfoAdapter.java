package com.blakflag.parkmycar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.model.ParkingInfo;

import java.util.List;


/**
 * Created by Amit on 8/15/2017.
 */

public class ParkinfoAdapter extends RecyclerView.Adapter<ParkinfoAdapter.ViewHolder> {
    Context context;
    List<ParkingInfo>parkingInfos;

    public ParkinfoAdapter(Context context, List<ParkingInfo> parkingInfos) {
        this.context = context;
        this.parkingInfos = parkingInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rent_post_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.adddress.setText(parkingInfos.get(position).address);
        holder.price.setText("Price : "+parkingInfos.get(position).price +" per hour");
        holder.description.setText(
                "Contact Person: "+parkingInfos.get(position).contactPerson+"\n"
                        +"Phone : "+parkingInfos.get(position).phone+"\n"

                        +parkingInfos.get(position).startTime +"  to  "+parkingInfos.get(position).endTime+"\n"
                +"Note: "+parkingInfos.get(position).note);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        TextView description,adddress,price;
        Button delete;
        public ViewHolder(View itemView) {
            super(itemView);
            delete= (Button) itemView.findViewById(R.id.delete);
            description= (TextView) itemView.findViewById(R.id.description);
            price= (TextView) itemView.findViewById(R.id.tvprice);
            adddress= (TextView) itemView.findViewById(R.id.address);
        }
    }
}
