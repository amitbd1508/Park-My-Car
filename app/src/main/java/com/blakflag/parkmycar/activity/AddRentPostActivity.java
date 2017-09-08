package com.blakflag.parkmycar.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.model.ParkingInfo;
import com.blakflag.parkmycar.util.App;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class AddRentPostActivity extends AppCompatActivity {

    private static final String TAG ="AddRentPostActivity" ;
    FirebaseDatabase postdb;
    DatabaseReference postref;
    Button post,location;
    TextView contactPerson,address,currentLocation,phone,startTime,endTime,price,note;

    public static int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 100;

    LocationTracker tracker;
    TrackerSettings settings;
    boolean findlocation=false;
    ParkingInfo parkingInfo;
    ProgressDialog progressDialog;
    boolean flag=true;
    public static double currentLatitude = 0.0, currentLongitude = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rent_post);


        progressDialog = new ProgressDialog(AddRentPostActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Location...");
        progressDialog.show();
        
        
        setTrakerSettings();
        initalizeTracker();
        startTracking();
        
        postdb = FirebaseDatabase.getInstance();
        postref = postdb.getReference(App.RENT_PARKINFO_DB);
        initializeView();
        parkingInfo=new ParkingInfo();

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),RentUserMapActivity.class));
                flag=true;

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allvalidation())
                {
                    parkingInfo.address=address.getText().toString();
                    parkingInfo.contactPerson=contactPerson.getText().toString();
                    parkingInfo.phone=phone.getText().toString();
                    parkingInfo.startTime=startTime.getText().toString();
                    parkingInfo.endTime=endTime.getText().toString();
                    parkingInfo.note=note.getText().toString();
                    parkingInfo.price=Float.parseFloat(price.getText().toString());

                    if(findlocation)
                    {
                        postref.push().setValue(parkingInfo);
                        finish();
                    }
                    else
                        Toast.makeText(AddRentPostActivity.this, "Please Wait ...\n Cannot find the location", Toast.LENGTH_SHORT).show();

                }
                else
                    Toast.makeText(AddRentPostActivity.this, "Please provide all data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    Address getAddressFromLatLog(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses.get(0);
    }
    private boolean allvalidation() {
        if(validateView(contactPerson) &&
        validateView(address)&&
        validateView(currentLocation)&&
        validateView(phone)&&
        validateView(startTime)&&
        validateView(endTime)&&
        validateView(price)&&
        validateView(note))
            return true;
        return false;
    }

    private void initializeView() {
        location= (Button) findViewById(R.id.findlocation);
        post= (Button) findViewById(R.id.post);

        contactPerson= (TextView) findViewById(R.id.contact_person);
        address= (TextView) findViewById(R.id.address);
        currentLocation= (TextView) findViewById(R.id.curretlocation);
        phone= (TextView) findViewById(R.id.phone);
        startTime= (TextView) findViewById(R.id.starttime);
        endTime= (TextView) findViewById(R.id.endtime);
        price= (TextView) findViewById(R.id.price);
        note= (TextView) findViewById(R.id.note);




    }
    boolean validateView(TextView textView)
    {
        if(textView==null||textView.getText().toString().length()<1)
        {
            textView.setError("Please provide the info");
            return false;
        }
        return true;
    }

    private void initalizeTracker() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
        tracker = new LocationTracker(this, settings) {

            @Override
            public void onLocationFound(Location location) {
                // Do some stuff when a new location has been found.


               try{
                   //Log.d("Add Rental", "Location :" + location.describeContents() + location.getSpeed() + "\n" + location.getAltitude() + "\n" + location.getLatitude() + "\n" + location.getLongitude() + "\n" + location.getProvider() + "\n" + location.getAccuracy());

                   if(flag)
                   {
                       currentLatitude = location.getLatitude();
                       currentLongitude = location.getLongitude();
                       if(progressDialog!=null)
                           progressDialog.dismiss();
                       findlocation=true;
                       Address add=getAddressFromLatLog(new LatLng(currentLatitude,currentLongitude));
                       String addressstr="";
                       if(!(add==null||add.equals(null)))
                           addressstr="\n"+add.getLocality()+","+add.getFeatureName()+","+add.getPostalCode();
                       currentLocation.setText("Lat: "+currentLatitude+"\n"+"Lon:"+currentLongitude+addressstr);
                       parkingInfo.latitude=currentLatitude;
                       parkingInfo.longitude=currentLongitude;
                       parkingInfo.submittedBy=App.user.email;
                       flag=false;
                   }
               }catch (Exception ex)
               {
                   ex.printStackTrace();
               }
                
            }

            @Override
            public void onTimeout() {

                Toast.makeText(getApplicationContext(), "Time out", Toast.LENGTH_SHORT).show();
            }
        };
    }





    private void setTrakerSettings() {
        settings = new TrackerSettings()
                .setUseGPS(true)
                .setUseNetwork(true)
                .setUsePassive(true)
                .setTimeBetweenUpdates(4000)
                .setMetersBetweenUpdates(1);
    }

    private void startTracking() {
        if (tracker != null) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            tracker.startListening();
            Log.d(TAG, "Tracker started");
        }

    }

    private void stopTracking() {
        if (tracker != null)
            tracker.stopListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        startTracking();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTracking();
    }
}
