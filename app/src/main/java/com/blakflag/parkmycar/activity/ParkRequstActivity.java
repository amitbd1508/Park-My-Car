package com.blakflag.parkmycar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.adapter.ParkRequestAdapter;
import com.blakflag.parkmycar.adapter.ParkinfoAdapter;
import com.blakflag.parkmycar.callback.IRequestCallBack;
import com.blakflag.parkmycar.model.History;
import com.blakflag.parkmycar.model.ParkingInfo;
import com.blakflag.parkmycar.model.ParkingRequst;
import com.blakflag.parkmycar.util.App;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParkRequstActivity extends AppCompatActivity implements IRequestCallBack{


    FirebaseDatabase postdb,historydb;
    DatabaseReference postref,historyref;
    private RecyclerView recyclerView;
    List<ParkingRequst> parkingRequsts;
    ParkRequestAdapter parkRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_requst);
        postdb = FirebaseDatabase.getInstance();
        historydb = FirebaseDatabase.getInstance();
        postref = postdb.getReference(App.PARKING_REQUSET_DB);
        historyref = historydb.getReference(App.PARKING_HISTORY_DB);
        recyclerView= (RecyclerView) findViewById(R.id.list);
        parkingRequsts=new ArrayList<ParkingRequst>();
        parkRequestAdapter=new ParkRequestAdapter(this,parkingRequsts,this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(parkRequestAdapter);

        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkingRequsts.clear();

                recyclerView.setVisibility(View.VISIBLE);
                parkRequestAdapter.notifyDataSetChanged();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ParkingRequst parkingRequst = child.getValue(ParkingRequst.class);
                    Log.d("Keys",child.getKey()+"\n"+child.getRef().toString()+"\n"+child.getValue());
                    if(parkingRequst.isBookingConfirm==0 && parkingRequst.getParkingEmail().equals(App.user.email)){
                        parkingRequst.key=child.getKey();
                        parkingRequsts.add(parkingRequst);
                        parkRequestAdapter.notifyDataSetChanged();


                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ParkingRequst parkingRequst = child.getValue(ParkingRequst.class);

                    if(parkingRequst.isBookingConfirm==1){
                        child.getRef().removeValue();
                        Log.d("Removeing value","Removed ");


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void requestStatus(final ParkingRequst parkingRequst, boolean isRequstAccepted) {
        Log.d("ParkRequest","Request accepted ");
        if (isRequstAccepted)
        {
            Log.d("ParkRequest","Request true ");
            postref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child:dataSnapshot.getChildren())
                    {
                        ParkingRequst parkingRequst1=child.getValue(ParkingRequst.class);
                        Log.d("key",parkingRequst.key+"=="+parkingRequst1.key);
                        if((parkingRequst1.isBookingConfirm==0 && parkingRequst.key.equals(child.getKey())) )
                        {
                            Log.d("ParkRequest","Request Confirm ");
                            parkingRequst1.isBookingConfirm=1;
                            child.getRef().setValue(parkingRequst1);

                            History history=new History();
                            history.carOwnerName=parkingRequst1.requestBy;
                            history.parkOwnerName=parkingRequst1.parkingAddress;
                            history.totaltime=parkingRequst1.hour+"";
                            history.price=parkingRequst1.totalPrice+"";
                            history.parkowneremail=parkingRequst1.getParkingEmail();
                            history.carowneremail=parkingRequst1.requesterEmail;
                            historyref.push().setValue(history);

                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {

        }
    }
}
