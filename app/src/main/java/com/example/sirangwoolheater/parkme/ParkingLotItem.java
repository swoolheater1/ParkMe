package com.example.sirangwoolheater.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ParkingLotItem extends AppCompatActivity implements OnMapReadyCallback, PlacesDetails.Listener {

    private SupportMapFragment gmap;
    private String id;
    private TextView ad;
    private TextView na;
    private String Name;
    private String Address;
    private List<Double> loc;
    private Button availBut;
    private Button fullBut;
    private Button openMap;
    private CheckBox fav;
    final String MY_PREFS_NAME = "My_FAV_PARKING_LOTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        Name = intent.getExtras().getString("name");
        Address = intent.getExtras().getString("address");

        new PlacesDetails(this).execute(id);

        fav = (CheckBox) findViewById(R.id.checkBox);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(id, null);
        if (restoredText != null) {
            fav.setChecked(true);
        }


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredText = prefs.getString(id, null);
                if (restoredText != null) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.remove(id);
                    editor.commit();
                    fav.setChecked(false);
                    }
                else
                {
                    // MY_PREFS_NAME - a static String variable like: //public static final String MY_PREFS_NAME = "MyPrefsFile";
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(id, "true");
                    editor.commit();
                    fav.setChecked(true);
                }
            }
        });

        ad = (TextView) findViewById(R.id.address);
        na = (TextView) findViewById(R.id.name);
        openMap = (Button) findViewById(R.id.openMaps);

        String st = "no data";
        try {
            st = new ParkingStatus().execute(id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ad.setText(Address);


        availBut = (Button) findViewById(R.id.availButton);
        fullBut = (Button) findViewById(R.id.fullButton);

        if(st.equals("Available")){
            availBut.setEnabled(false);
            fullBut.setEnabled(true);
            na.setTextColor(Color.GREEN);
        } else if (st.equals("Full")){
            availBut.setEnabled(true);
            fullBut.setEnabled(false);
            na.setTextColor(Color.RED);
        } else if (st.equals("none")){
            st = "Available";
            availBut.setEnabled(false);
            fullBut.setEnabled(true);
            na.setTextColor(Color.GREEN);
        }

        na.setText(Name + " " + st);

        availBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ParkingUpdateStatus().execute(id);
                String st = "no data";
                try {
                    st = new ParkingStatus().execute(id).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                na.setText(Name + " " + st);
                if(st.equals("Available")){
                    availBut.setEnabled(false);
                    fullBut.setEnabled(true);
                    na.setTextColor(Color.GREEN);
                } else if (st.equals("Full")){

                    availBut.setEnabled(true);
                    fullBut.setEnabled(false);
                    na.setTextColor(Color.RED);
                }
            }
        });
        fullBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ParkingUpdateStatus().execute(id);
                String st = "no data";
                try {
                    st = new ParkingStatus().execute(id).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                na.setText(Name + " " + st);
                if(st.equals("Available")){

                    availBut.setEnabled(false);
                    fullBut.setEnabled(true);
                    na.setTextColor(Color.GREEN);
                } else if (st.equals("Full")){
                    availBut.setEnabled(true);
                    fullBut.setEnabled(false);
                    na.setTextColor(Color.RED);
                }
            }
        });

        gmap =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
//            Intent intent = new Intent(this, FavoriteActivity.class);
//            startActivity(intent);
//            return true;
        }
        if (id == R.id.action_favorite) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        LatLng address = new LatLng(loc.get(1),loc.get(0));
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 16));
        gmap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_dark_pressed))
                //.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(address));//47.17, 27.5699))); //Iasi, Romania
    }

    @Override
    public void onLoaded(List<Double> list) {
        System.out.println("Returned list: " + list);
        loc = list;
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "To travel is to live", Toast.LENGTH_LONG).show();
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+loc.get(1)+","+loc.get(0));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        gmap.getMapAsync(this);

    }

    @Override
    public void onError() {

    }
}
