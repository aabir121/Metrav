package com.example.aabir.metravv2.Utility;

/**
 * Created by aabir on 8/10/2016.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.akexorcist.googledirection.model.Info;
import com.example.aabir.metravv2.Utility.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkTest extends AsyncTask<String, Void, String> {
    private TextView textView;
    private LatLng latLng;
    int pos =0,pos2=1;
    Geocoder geocoder;
    Context context;
    ProgressDialog progressDialog;
    ArrayList<Place> arrayList;
    private static final String APP_ID = "AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI";
    Info durationinfo,distanceinfo;

    String jsonData=null;
    public NetworkTest(TextView textView,ProgressDialog progressDialog) {
        this.textView = textView;
//        this.latLng=latLng;
        this.progressDialog=progressDialog;
    }

    @Override
    protected String doInBackground(String... strings) {
//        jsonData=getUrlContents(strings[0]);
        Double lat=Double.parseDouble(strings[0]);
        Double lon=Double.parseDouble(strings[1]);

        return findPlaces(lat,lon,strings[2]);

    }
    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    public String findPlaces(double latitude, double longitude, String placeSpacification)
    {
        StringBuilder content = new StringBuilder();
        String urlString = makeUrl(latitude, longitude,placeSpacification);

        String json = getJSON(urlString);


        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        try {
            array = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng origin=new LatLng(latitude,longitude);

        arrayList = new ArrayList<Place>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i));
                    String placeName=place.getName();
                    Log.v("Places Services ", ""+place);

                    arrayList.add(place);
                } catch (Exception e) {
                }
            }

        double min=1000;

        List<Double> distancesList=new ArrayList<Double>();
//        double first=1000;
//        double scnd=1000;

        for(int i=0;i<arrayList.size();i++)
        {
            LatLng destination=new LatLng(arrayList.get(i).getLatitude(),arrayList.get(i).getLongitude());

            Location frst=new Location("");
            frst.setLatitude(latitude);
            frst.setLongitude(longitude);

            Location last=new Location("");
            last.setLatitude(arrayList.get(i).getLatitude());
            last.setLongitude(arrayList.get(i).getLongitude());



            double distance=frst.distanceTo(last);
            distancesList.add(distance);
            if(distance<min)
            {min=distance;
                pos =i;}



        }


        content.append(""+arrayList.get(pos).getName()+"\n");
        content.append("Distance: "+min+"\n\n");
        String resultCoordinates=arrayList.get(pos).getLatitude()+","+arrayList.get(pos).getLongitude();

        return resultCoordinates;
    }





    private String getUrlContents(String theUrl)
    {
      String weather = theUrl;
        try {
            URL url = new URL(theUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            StringBuilder content = new StringBuilder();

            String inputString;
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }


//            while ((inputString = bufferedReader.readLine()) != null) {
//                builder.append(inputString);
//            }
            weather=content.toString();

            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weather;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String temp) {

//        latLng=new LatLng(arrayList.get(pos).getLatitude(),arrayList.get(pos).getLongitude());
//        textView.setText(arrayList.get(pos).getLatitude()+","+arrayList.get(pos).getLongitude());
        progressDialog.dismiss();
    }



    private String makeUrl(double latitude, double longitude,String place) {
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        StringBuilder urlString = new StringBuilder("https://www.google.com");

        if (place.equals("")) {
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            //   urlString.append("&types="+place);
            urlString.append("&key=" + APP_ID+"&sensor=true");
        } else {
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius=1000");
            urlString.append("&types="+place);
            urlString.append("&key=" + APP_ID+"&sensor=true");
        }

        return urlString.toString();
    }
}









