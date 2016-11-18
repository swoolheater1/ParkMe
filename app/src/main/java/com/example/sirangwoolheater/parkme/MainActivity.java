package com.example.sirangwoolheater.parkme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements LocationListener, LoadJSONTask.Listener, AdapterView.OnItemClickListener {

//    ListView listView;

    private LocationManager locationManager;
    double latitude;
    double longitude;

    private ListView mListView;

//    public static String lat = "33.752599";
//    public static String log = "84.384780";
//    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
//            lat +
//            ",%20-" +
//            log +
//            "&radius=500&types=parking&sensor=false&key=AIzaSyDMU2zYH3GWeKwQRaDJ0kYFtuDdXGbAMl4";
//            //"https://api.learn2crack.com/android/jsonandroid/";

    private List<HashMap<String, String>> mParkingMapList = new ArrayList<>();

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VIC = "vicinity";
    private static final String KEY_STATUS = "status";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,   // 3 sec
                1, this);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        System.out.println("Network is: " + network_enabled);
        Location location;

        if (network_enabled) {

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            //  setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.list);
            mListView.setOnItemClickListener(this);


            String str = "latttti " + latitude + "longi " + longitude;
            //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
            final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    latitude+//
                    //"33.752711" +
                    ",%20" +
                    longitude+
                    //"-84.385370" +
                    "&radius=250&types=parking&sensor=false&key=AIzaSyDMU2zYH3GWeKwQRaDJ0kYFtuDdXGbAMl4";
            new LoadJSONTask(this).execute(URL);

        } else if (gps_enabled){

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//NETWORK_PROVIDER);

            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            //  setContentView(R.layout.activity_main);

            mListView = (ListView) findViewById(R.id.list);
            mListView.setOnItemClickListener(this);


            String str = "latttti " + latitude + "longi " + longitude;
            //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
            final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    latitude+
                    //"33.752711" +
                    ",%20" +
                    longitude+
                    //"-84.385370" +
                    "&radius=250&types=parking&sensor=false&key=AIzaSyDMU2zYH3GWeKwQRaDJ0kYFtuDdXGbAMl4";
            new LoadJSONTask(this).execute(URL);
        }
//        else {
//            final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
//                    "33.752599" +
//                    ",%20-" +
//                    "84.384780&radius=500&types=parking&sensor=false&key=AIzaSyDMU2zYH3GWeKwQRaDJ0kYFtuDdXGbAMl4";
//            new LoadJSONTask(this).execute(URL);
//        }

     }

    @Override
    public void onLoaded(List<ParkingLot> androidList) {
        //PHP Password: MobileFinalApp

        ///MyParkingInfo/parking/update/dc44b6407ce52f1d0bae318ce1967f69ff305379/
        ///MyParkingInfo/parking/list/dc44b6407ce52f1d0bae318ce1967f69ff305379/

        //System.out.println(androidList);
        for (ParkingLot android : androidList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_ID, android.getid());
            map.put(KEY_NAME, android.getName());
            map.put(KEY_VIC, android.getVicinity());
            //map.put(KEY_GEM, android.getGeometry());

            String st = "No Data";
            try {
                st = new ParkingStatus().execute(android.getid()).get();
                if (st.equals("Available")){
                    st = "avail";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            map.put(KEY_STATUS, st);

            mParkingMapList.add(map);
        }

        loadListView();
    }

    @Override
    public void onError() {

        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        //Toast.makeText(this, mParkingMapList.get(i).get(KEY_NAME),Toast.LENGTH_LONG).show();
        String id = mParkingMapList.get(i).get(KEY_ID);
        String name = mParkingMapList.get(i).get(KEY_NAME);
        String address = mParkingMapList.get(i).get(KEY_VIC);

        System.out.println(id);

        Intent intent = new Intent(this, ParkingLotItem.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("address", address);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, mParkingMapList, R.layout.row,
                new String[] { KEY_NAME, KEY_VIC, KEY_STATUS},
                new int[] { R.id.title, R.id.description, R.id.button }

        );

        mListView.setAdapter(adapter);

    }

    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();


        String str = "Latitude: "+location.getLatitude()+"Longitude: "+location.getLongitude();
        //System.out.println(str);
        //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();

    }

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public void onProviderDisabled(String provider) {

        /******** Called when User off Gps *********/

        //Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/

        //Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }





}
