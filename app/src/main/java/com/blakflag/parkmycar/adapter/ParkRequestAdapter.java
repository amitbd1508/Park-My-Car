package com.blakflag.parkmycar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.callback.IRequestCallBack;
import com.blakflag.parkmycar.model.ParkingInfo;
import com.blakflag.parkmycar.model.ParkingRequst;

import java.util.List;


/**
 * Created by Amit on 8/15/2017.
 */

public class ParkRequestAdapter extends RecyclerView.Adapter<ParkRequestAdapter.ViewHolder> {
    Context context;
    List<ParkingRequst> parkingRequsts;
    IRequestCallBack iRequestCallBack;

    public ParkRequestAdapter(Context context, List<ParkingRequst> parkingRequsts,IRequestCallBack iRequestCallBack) {
        this.context = context;
        this.parkingRequsts = parkingRequsts;
        this.iRequestCallBack=iRequestCallBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_request_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.adddress.setText(parkingRequsts.get(position).requestBy);
        holder.price.setText("Price : "+ parkingRequsts.get(position).hour +" per hour");
        holder.description.setText(
                "Contact Person: "+ parkingRequsts.get(position).requesterContactNo+"\n"
                        +"Distance : "+ parkingRequsts.get(position).distance+"\n"

                        + parkingRequsts.get(position).date
                        +"Note: "+ parkingRequsts.get(position).paymentMethod);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iRequestCallBack.requestStatus(parkingRequsts.get(position),true);
                Log.d("Accepted", "request callback");

            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iRequestCallBack.requestStatus(parkingRequsts.get(position),false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkingRequsts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        TextView description,adddress,price;
        Button accept,reject;
        public ViewHolder(View itemView) {
            super(itemView);
            accept= (Button) itemView.findViewById(R.id.accept);
            reject= (Button) itemView.findViewById(R.id.reject);
            description= (TextView) itemView.findViewById(R.id.description);
            price= (TextView) itemView.findViewById(R.id.tvprice);
            adddress= (TextView) itemView.findViewById(R.id.address);
        }
    }
}
