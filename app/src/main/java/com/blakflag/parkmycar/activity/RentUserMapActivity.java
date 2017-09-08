package com.blakflag.parkmycar.activity;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.blakflag.parkmycar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

public class RentUserMapActivity extends FragmentActivity implements OnMapReadyCallback {

    public static boolean SET_ZOOM_CONTROL_ENABLED = true;
    public static boolean SET_ZOOM_GESTURES_ENABLED = true;
    public static boolean SET_ZOOM_ALL_GESTURES_ENABLED = true;
    public static float ZOOM = 17.0f;

    public static int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 100;





    //location search variable here
    List<String> searchResult;
    EditText etSearchLocation;
    ListView lvSearchList;
    ArrayAdapter<String> lvAdapter;
    List<Address> addressList = null;

    LocationTracker tracker;
    TrackerSettings settings;
    MarkerOptions markeroption;
    Marker marker;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_user_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markeroption=new MarkerOptions()
                .position(new LatLng(21,90))
                .flat(true)
                .draggable(true)
                .title("You");

        etSearchLocation = (EditText) findViewById(R.id.etLocationSearchbar);
        lvSearchList = (ListView) findViewById(R.id.listView);
        searchResult = new ArrayList<String>();
        lvAdapter = new ArrayAdapter<String>(this,
                R.layout.item_search, R.id.tv_search_text, searchResult);
        lvSearchList.setAdapter(lvAdapter);
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

                mMap.clear();
                LatLng latLng = new LatLng(addressList.get(position).getLatitude(), addressList.get(position).getLongitude());
                markeroption=new MarkerOptions()
                        .position(latLng)
                        .flat(true)
                        .draggable(true)
                        .title(addressList.get(position).getFeatureName());

                marker=mMap.addMarker(markeroption);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM));
                lvSearchList.setVisibility(View.INVISIBLE);
            }
        });
       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                markeroption=new MarkerOptions()
                        .position(latLng)
                        .flat(true)
                        .draggable(true)
                        .title("Seleted Location");

                marker=mMap.addMarker(markeroption);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM));
                lvSearchList.setVisibility(View.INVISIBLE);
            }
        });*/




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(SET_ZOOM_CONTROL_ENABLED);
        mMap.getUiSettings().setZoomGesturesEnabled(SET_ZOOM_GESTURES_ENABLED);
        mMap.getUiSettings().setAllGesturesEnabled(SET_ZOOM_ALL_GESTURES_ENABLED);


    }
}
