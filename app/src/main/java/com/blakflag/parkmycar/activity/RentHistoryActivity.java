package com.blakflag.parkmycar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.adapter.HistoryAdapter;
import com.blakflag.parkmycar.adapter.ParkRequestAdapter;
import com.blakflag.parkmycar.model.History;
import com.blakflag.parkmycar.model.ParkingRequst;
import com.blakflag.parkmycar.util.App;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RentHistoryActivity extends AppCompatActivity {

    FirebaseDatabase postdb;
    DatabaseReference postref;
    private RecyclerView recyclerView;
    List<History> histories;
    HistoryAdapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_history);
        postdb = FirebaseDatabase.getInstance();
        postref = postdb.getReference(App.PARKING_HISTORY_DB);
        recyclerView= (RecyclerView) findViewById(R.id.list);
        histories=new ArrayList<History>();
        final HistoryAdapter historyAdapter=new HistoryAdapter(histories,this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(historyAdapter);

        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                histories.clear();

                recyclerView.setVisibility(View.VISIBLE);
                historyAdapter.notifyDataSetChanged();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    History history = child.getValue(History.class);
                    Log.d("Keys",child.getKey()+"\n"+child.getRef().toString()+"\n"+child.getValue());
                    if(App.user.type.equals(App.CN_USER)) {
                        if (history.carowneremail.equals(App.user.email)) {
                            histories.add(history);
                            historyAdapter.notifyDataSetChanged();


                        }
                    }
                    else
                    {
                        if (history.parkowneremail.equals(App.user.email)) {
                            histories.add(history);
                            historyAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
