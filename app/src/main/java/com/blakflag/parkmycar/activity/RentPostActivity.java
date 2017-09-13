package com.blakflag.parkmycar.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.adapter.ParkinfoAdapter;
import com.blakflag.parkmycar.model.ParkingInfo;
import com.blakflag.parkmycar.model.User;
import com.blakflag.parkmycar.util.App;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nipunbirla.boxloader.BoxLoaderView;

import java.util.ArrayList;
import java.util.List;

public class RentPostActivity extends AppCompatActivity {

    FirebaseDatabase postdb;
    DatabaseReference postref;


    private RecyclerView recyclerView;
    List<ParkingInfo> parkingInfos;
    ParkinfoAdapter parkinfoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_post);

        postdb = FirebaseDatabase.getInstance();
        postref = postdb.getReference(App.RENT_PARKINFO_DB);


        recyclerView= (RecyclerView) findViewById(R.id.list);
       // boxLoader= (BoxLoaderView) findViewById(R.id.progress);

        parkingInfos=new ArrayList<ParkingInfo>();
        parkinfoAdapter=new ParkinfoAdapter(this,parkingInfos);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(parkinfoAdapter);
      //  boxLoader.setVisibility(View.GONE);
        findViewById(R.id.newpost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddRentPostActivity.class));
            }
        });
        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkingInfos.clear();
             //   boxLoader.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                parkinfoAdapter.notifyDataSetChanged();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ParkingInfo parkingInfo = child.getValue(ParkingInfo.class);
                    Log.d("Rent post",parkingInfo.submittedBy+" == "+App.user.email);
                    if(parkingInfo.submittedBy==App.user.email || parkingInfo.submittedBy.equals(App.user.email)){
                        parkingInfos.add(parkingInfo);
                        parkinfoAdapter.notifyDataSetChanged();
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Drawer();
    }
    public void Drawer() {

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .addProfiles(
                        new ProfileDrawerItem().withName(App.user.name).withEmail(App.user.email).
                                withIcon(getResources().getDrawable(R.drawable.parking))
                )
                .build();

        final Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withDrawerWidthDp(250)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("History").withIdentifier(1),
                        new PrimaryDrawerItem().withName("Park Requset").withIdentifier(2),
                        new PrimaryDrawerItem().withName("Account").withIdentifier(0)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.equals(2)) {
                            startActivity(new Intent(getApplicationContext(), ParkRequstActivity.class));
                        }
                        else if (drawerItem.equals(1)) {
                            startActivity(new Intent(getApplicationContext(), RentHistoryActivity.class));
                        }

                        return true;
                    }
                }).withDrawerGravity(Gravity.LEFT)
                .build();
        result.openDrawer();
        result.closeDrawer();
        result.isDrawerOpen();

        /*ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.isDrawerOpen()) {
                    result.closeDrawer();
                } else {
                    result.openDrawer();
                }
            }
        });*/
    }
}
