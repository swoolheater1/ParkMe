package com.example.sirangwoolheater.parkme;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements LoadJSONTask.Listener, AdapterView.OnItemClickListener {

//    ListView listView;


    private ListView mListView;

    public static String lat = "33.752599";
    public static String log = "84.384780";
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
            lat +
            ",%20-" +
            log +
            "&radius=500&types=parking&sensor=false&key=AIzaSyDMU2zYH3GWeKwQRaDJ0kYFtuDdXGbAMl4";
            //"https://api.learn2crack.com/android/jsonandroid/";

    private List<HashMap<String, String>> mParkingMapList = new ArrayList<>();

    private static final String KEY_VER = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_API = "vicinity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        new LoadJSONTask(this).execute(URL);
     }

    @Override
    public void onLoaded(List<ParkingLot> androidList) {

        System.out.println(androidList);
        for (ParkingLot android : androidList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_VER, android.getid());
            map.put(KEY_NAME, android.getName());
            map.put(KEY_API, android.getVicinity());

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

        Toast.makeText(this, mParkingMapList.get(i).get(KEY_NAME),Toast.LENGTH_LONG).show();
    }

    private void loadListView() {

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, mParkingMapList, R.layout.row,
                new String[] { KEY_NAME, KEY_API },
                new int[] { R.id.title, R.id.description });

        mListView.setAdapter(adapter);



    }

    public void updateButton(View v) {
        RelativeLayout rl = (RelativeLayout) v.getParent();

        Button b = (Button) v.findViewById(R.id.button);
        b.setText("Avail ?");
        b.setBackgroundColor(Color.GREEN);
        rl.setBackgroundColor(Color.RED);
    }
}
