package com.example.sirangwoolheater.parkme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FavoriteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    final String MY_PREFS_NAME = "My_FAV_PARKING_LOTS";
    private List<HashMap<String, String>> mParkingMapList = new ArrayList<>();

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_VIC = "vicinity";
    private static final String KEY_STATUS = "status";
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.favlist);
        mListView.setOnItemClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            onLoaded();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onLoaded() throws ExecutionException, InterruptedException, JSONException {

        SharedPreferences favPref = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        Map<String,?> favlist = favPref.getAll();

        for (Map.Entry<String,?> android : favlist.entrySet()) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_ID, android.getKey());

            JSONObject jsonObj = new PleaceDetailsForFev().execute(android.getKey()).get();
            map.put(KEY_NAME, jsonObj.getJSONObject("result").getString(KEY_NAME));
            map.put(KEY_VIC, jsonObj.getJSONObject("result").getString(KEY_VIC));
            String st = "No Data";
            try {
                st = new ParkingStatus().execute(android.getKey()).get();
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

    private void loadListView() {
    ListAdapter adapter = new SimpleAdapter(FavoriteActivity.this, mParkingMapList, R.layout.row,
            new String[] { KEY_NAME, KEY_VIC, KEY_STATUS},
            new int[] { R.id.title, R.id.description, R.id.button }

    );

    mListView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String id = mParkingMapList.get(i).get(KEY_ID);
        String name = mParkingMapList.get(i).get(KEY_NAME);
        String address = mParkingMapList.get(i).get(KEY_VIC);

        System.out.println(id);

        Intent intent = new Intent(this, ParkingLotItemFav.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("address", address);

        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
