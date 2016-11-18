package com.example.sirangwoolheater.parkme;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sirangwoolheater on 11/15/16.
 */

public class PlacesDetails extends AsyncTask<String,Void,List<Double>> {
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";



    private static final String API_KEY = "AIzaSyDMU2zYH3GWeKwQRaDJ0kYFtuDdXGbAMl4" ; // cle pour le serveur
//    public PlacesDetails() {
//        // TODO Auto-generated constructor stub
//    }

    public PlacesDetails(Listener listener) {

        mListener = listener;
    }



    public interface Listener {

        void onLoaded(List<Double> list);

        void onError();
    }

    private Listener mListener;

    @Override
    protected List<Double> doInBackground(String... strings) {

        return placeDetail(strings[0]);
    }

    @Override
    protected void onPostExecute(List<Double> list) {

        if (list != null) {

            mListener.onLoaded(list);

        } else {

            mListener.onError();
        }
    }


    public ArrayList<Double> placeDetail(String input) {
        ArrayList<Double> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON);
            sb.append("?placeid=" + URLEncoder.encode(input, "utf8"));
            sb.append("&key=" + API_KEY);
            URL url = new URL(sb.toString());
            //Log.e("url", url.toString());
            System.out.println("URL: "+url);
            System.out.println("******************************* connexion au serveur *****************************************");
            //Log.e("nous sommes entrai de test la connexion au serveur", "test to connect to the api");
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);

            }
            System.out.println("le json result"+jsonResults.toString());
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
            System.out.println("******************************* fin de la connexion*************************************************");
        }

        try {

            // Create a JSON object hierarchy from the results
            //Log.e("creation du fichier Json", "creation du fichier Json");
            System.out.println("fabrication du Json Objet");
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            //JSONArray predsJsonArray = jsonObj.getJSONArray("html_attributions");
            JSONObject result = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            System.out.println("la chaine Json "+result);
            Double longitude  = result.getDouble("lng");
            Double latitude =  result.getDouble("lat");
            System.out.println("longitude et latitude "+ longitude+latitude);
            resultList = new ArrayList<Double>(result.length());
            resultList.add(result.getDouble("lng"));
            resultList.add(result.getDouble("lat"));
            System.out.println("les latitude dans le table"+resultList);

        } catch (JSONException e) {
            ///Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}
