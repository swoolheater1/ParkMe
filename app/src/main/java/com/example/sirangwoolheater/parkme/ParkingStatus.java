package com.example.sirangwoolheater.parkme;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sirangwoolheater on 11/15/16.
 */
public class ParkingStatus extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... params) {

        //http connectino starts

        String resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder("http://rachitic-tumble.000webhostapp.com/RestController.php?view=single&id="+params[0]);
            URL url = new URL(sb.toString());
            //Log.e("url", url.toString());
            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
                //System.out.println(jsonResults);

            }
            resultList = jsonResults.toString();
            System.out.println("le json result "+jsonResults.toString());
        } catch (MalformedURLException e) {
            //Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

        }
        //http connectino ends


        return resultList;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }


}
