package com.blakflag.parkmycar.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.blakflag.parkmycar.R;
import com.blakflag.parkmycar.model.ParkingInfo;
import com.blakflag.parkmycar.model.ParkingRequst;
import com.blakflag.parkmycar.util.App;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    FirebaseDatabase postdb;
    DatabaseReference postref;


    FirebaseDatabase parkingReqdb;
    DatabaseReference parkingReqref;

    private static final String TAG = "UserMapsActivity";
    //map settings variable
    public static boolean SET_ZOOM_CONTROL_ENABLED = true;
    public static boolean SET_ZOOM_GESTURES_ENABLED = true;
    public static boolean SET_ZOOM_ALL_GESTURES_ENABLED = true;
    public static float ZOOM = 17.0f;

    public static int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 100;



    public static double currentLatitude = 21.0, currentLongitude = 90.0;
    static double destinationLatitude = 21.0, destinationLongitude = 90.0;

    //location search variable here
    List<String> searchResult;
    EditText etSearchLocation;
    ListView lvSearchList;
    ArrayAdapter<String> lvAdapter;
    List<Address> addressList = null;

    TextView parking_price,parking_address;
    Button request;
    View requstCard;
    RangeBar rangebar;
    boolean reqFlag=false;
    float price;
    String currentEmail="";
    LatLng req,res;
    ProgressDialog requestProgress;

    ImageView ivMenu;

    View response;

    private GoogleMap mMap;
    LocationTracker tracker;
    TrackerSettings settings;
    List<ParkingInfo>parkingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        parkingList=new ArrayList<ParkingInfo>();

        Drawer();

        postdb = FirebaseDatabase.getInstance();
        postref = postdb.getReference(App.RENT_PARKINFO_DB);

        parkingReqdb = FirebaseDatabase.getInstance();
        parkingReqref = postdb.getReference(App.PARKING_REQUSET_DB);

        ivMenu= (ImageView) findViewById(R.id.iv_map_drawer);

        requstCard=findViewById(R.id.requstcard);
        requstCard.setVisibility(View.GONE);
        parking_address= (TextView) findViewById(R.id.tv_parking_address);
        parking_price= (TextView) findViewById(R.id.tv_price);
        request= (Button) findViewById(R.id.btnRequest);
        rangebar= (RangeBar) findViewById(R.id.rangebar);
        rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                if(reqFlag)
                {
                    parking_price.setText("Price : "+Float.parseFloat(rangeBar.getRightPinValue())*price);

                }
            }
        });
        findViewById(R.id.btnnavigate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(res);
            }
        });


        response =findViewById(R.id.response);
        etSearchLocation = (EditText) findViewById(R.id.etLocationSearchbar);
        lvSearchList = (ListView) findViewById(R.id.listView);
        searchResult = new ArrayList<String>();
        lvAdapter = new ArrayAdapter<String>(this,
                R.layout.item_search, R.id.tv_search_text, searchResult);
        lvSearchList.setAdapter(lvAdapter);

        response.setVisibility(View.GONE);
        etSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                lvSearchList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String location = etSearchLocation.getText().toString();

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 10);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    searchResult.clear();
                    for (int i = 0; i < addressList.size(); i++) {
                        searchResult.add(addressList.get(i).getFeatureName()+" , " + addressList.get(i).getLocality()+" , "+addressList.get(i).getPostalCode()+"," + addressList.get(i).getCountryName());
                    }
                    lvAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                lvSearchList.setVisibility(View.VISIBLE);
            }
        });

        lvSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LatLng latLng = new LatLng(addressList.get(position).getLatitude(), addressList.get(position).getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .flat(true)
                        .draggable(true)

                        .title(addressList.get(position).getFeatureName())
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM));
                lvSearchList.setVisibility(View.INVISIBLE);
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestProgress = new ProgressDialog(MapsActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                requestProgress.setIndeterminate(true);
                requestProgress.setMessage("Requesting...");
                requestProgress.show();
                ParkingRequst parkingRequst=new ParkingRequst();
                Format dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                String pattern = ((SimpleDateFormat) dateFormat).toLocalizedPattern();
                parkingRequst.date=pattern;
                parkingRequst.distance=distance(req.latitude,req.longitude,res.latitude,res.longitude);
                parkingRequst.isBookingOver=0;
                parkingRequst.isBookingConfirm=0;
                parkingRequst.parkingAddress=parking_address.getText().toString();
                parkingRequst.parkingEmail=currentEmail;
                parkingRequst.hour=Integer.parseInt(rangebar.getRightPinValue());
                parkingRequst.totalPrice=parkingRequst.hour*price;
                parkingRequst.requsterLat=currentLatitude;
                parkingRequst.requesterLon=currentLongitude;
                parkingRequst.paymentMethod="Cash";
                parkingRequst.requesterEmail=App.user.email;
                parkingRequst.requesterContactNo=App.user.phone;
                parkingRequst.requestBy=App.user.name;

                parkingReqref.push().setValue(parkingRequst);



            }
        });

        parkingReqref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren())
                {
                    ParkingRequst parkingRequst=child.getValue(ParkingRequst.class);
                    if(parkingRequst.requesterEmail.equals(App.user.email) && parkingRequst.isBookingConfirm==1)
                    {
                        requstCard.setVisibility(View.GONE);
                        if(requestProgress!=null)
                            requestProgress.dismiss();
                        response.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }


    public void Drawer() {



        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.add)
                .addProfiles(
                        new ProfileDrawerItem().withName("Full Name").withEmail("Email").
                                withIcon(getResources().getDrawable(R.drawable.add))
                )
                .build();

        final Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withDrawerWidthDp(250)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("History").withIdentifier(1),
                        new PrimaryDrawerItem().withName("Pending Requset").withIdentifier(2),
                        new PrimaryDrawerItem().withName("Trip History").withIdentifier(0)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.equals(0)) {
                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
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

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
//        mMap.addCircle(new CircleOptions().radius(200).visible(true));
        // Add a marker in Sydney and move the camera

        setTrakerSettings();
        initalizeTracker();
        startTracking();


        mMap.getUiSettings().setZoomControlsEnabled(SET_ZOOM_CONTROL_ENABLED);
        mMap.getUiSettings().setZoomGesturesEnabled(SET_ZOOM_GESTURES_ENABLED);
        mMap.getUiSettings().setAllGesturesEnabled(SET_ZOOM_ALL_GESTURES_ENABLED);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this, ""+marker.getSnippet()+" Tag "+marker.getTag(), Toast.LENGTH_SHORT).show();
                ParkingInfo park=new ParkingInfo();
                for(ParkingInfo parking:parkingList)
                {
                    if((parking.latitude+parking.longitude+"").equals(marker.getTag())||parking.latitude+parking.longitude+""==marker.getTag())
                    {
                        Log.d("Clicked","Marker clicked");
                        park=parking;
                        price=parking.price;
                        parking_address.setText(parking.address);
                        reqFlag=true;
                        requstCard.setVisibility(View.VISIBLE);
                        currentEmail=parking.submittedBy;

                        res=new LatLng(parking.latitude,parking.longitude);
                        req=new LatLng(currentLatitude,currentLongitude);



                        break;
                    }
                }




                return true;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                reqFlag=false;
                requstCard.setVisibility(View.GONE);
                response.setVisibility(View.GONE);
            }
        });

        mMap.setMaxZoomPreference(100);

        postref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                parkingList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ParkingInfo parkingInfo = child.getValue(ParkingInfo.class);
                    parkingList.add(parkingInfo);
                    IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                    iconFactory.setRotation(0);
                    iconFactory.setContentRotation(0);
                    iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
                    addIcon(parkingInfo.latitude+parkingInfo.longitude+"",iconFactory, makeCharSequence(parkingInfo.price+"TK\n",parkingInfo.startTime+"-"+parkingInfo.endTime), new LatLng(parkingInfo.latitude,parkingInfo.longitude));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    void  navigate(LatLng latLng)
    {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latLng.latitude+","+latLng.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
    private void addIcon(String latlang,IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text.toString()))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        Marker ma =mMap.addMarker(markerOptions);
        ma.setTag(latlang); //here add a tag
    }
    private CharSequence makeCharSequence(String prefix,String suffix ) {

        String sequence = prefix + suffix;
        SpannableStringBuilder ssb = new SpannableStringBuilder(sequence);
        ssb.setSpan(new StyleSpan(ITALIC), 0, prefix.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(BOLD), prefix.length(), sequence.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    //===========================location tracker ==========================

    Marker marker;
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
            }
            return;
        }
        tracker = new LocationTracker(this, settings) {

            @Override
            public void onLocationFound(Location location) {
                // Do some stuff when a new location has been found.
                Log.d(TAG, "Location :" + location.describeContents() + location.getSpeed() + "\n" + location.getAltitude() + "\n" + location.getLatitude() + "\n" + location.getLongitude() + "\n" + location.getProvider() + "\n" + location.getAccuracy());

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                if(marker!=null)
                    marker.remove();
                LatLng latLng = new LatLng(currentLatitude,currentLongitude);
                marker=mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet("You")
                        .title(App.user.name)

                );
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM));

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

    Address getAddressFromLatLog(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses.get(0);
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
    //==========================end location tracker=========================
}
