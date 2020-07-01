package com.example.aabir.metravv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.aabir.metravv2.Utility.ShareLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.akexorcist.googledirection.util.DirectionConverter.decodePoly;

public class NavDrawerDirectionFinal extends FragmentActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private String serverKey = "AIzaSyAn4t6lMgzYWOHDOEkSGPy8ARfkByiIybk";
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};

    private String[] routeList={"Azampur Counter","Azampur Local Bus Stop","Rajlakshmi Counter Bus Stop","Airport Bus Stand","Kawlar Dhakhil Madrasha","Khilkhet North Bus Stop"};
    static LatLng origin,destination;
    static String originName,destinationName;
    ArrayList<Location> route1=new ArrayList<>();
    ArrayList<Location> route2=new ArrayList<>();
    ArrayList<ArrayList<Location>> allRoutes=new ArrayList<>();
    ArrayList<LatLng> latLngArrayList=new ArrayList<>();



    LatLng nearRouteBusLatLng;
    Location nearRouteBusLocation;
    int selectRouteNumber=0;



    int routenumber=0;
    Geocoder geocoder;
    List<Step> step;
    ArrayList<LatLng> sectionList;




    FloatingActionButton walkDirectionButton,driveDirectionButton,busDirectionButton;
    StringBuilder directionInfo=new StringBuilder("");
    TextView resultText;

    AlertDialog.Builder dialogBuilder;
    View dialogView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        String languageToLoad;
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select","English").equals("বাংলা"))
        {
            languageToLoad  = "bn"; // your language
            Log.e("Nav","bangla");
        }
        else{
            languageToLoad="en";
            Log.e("Nav","English");
        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_nav_drawer_map_with_direction);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FloatingActionButton sideBarToggle=(FloatingActionButton)findViewById(R.id.sideBarToggleBtn);
        sideBarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        View hView =  navigationView.getHeaderView(0);
        CircleImageView nav_user_image = (CircleImageView) hView.findViewById(R.id.imageViewHeader);


        SharedPreferences settings = getSharedPreferences("Metrav", 0);
        String imgstr=settings.getString("Image","");
        byte[] base= Base64.decode(imgstr,Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bit= BitmapFactory.decodeByteArray(base, 0, base.length, options); //Convert bytearray to bitmap
        if(bit!=null)
            nav_user_image.setImageBitmap(bit);
        else
            Toast.makeText(getApplicationContext(),"bitmap is null",Toast.LENGTH_LONG).show();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.map_with_direction_popup, null);
        dialogBuilder.setView(dialogView);

         FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
        resultText=(TextView)dialogView.findViewById(R.id.resultText);
        walkDirectionButton=(FloatingActionButton) findViewById(R.id.walkDirectionButton);
        driveDirectionButton=(FloatingActionButton)findViewById(R.id.driveDirectionButton);
        busDirectionButton=(FloatingActionButton)findViewById(R.id.busDirectionButton);

        busStandAdd();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickButtonClick(v);

            }
        });


        driveDirectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleDirection.withServerKey(serverKey)
                        .from(origin)
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(final Direction direction, String rawBody) {
                                // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                if (direction.isOK()) {


                                    try {
                                        driveDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(200, 255, 200)));
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            driveDirectionButton.setForegroundTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
                                        }
                                        walkDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
                                        busDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));

                                        mMap.clear();
                                        addMarkers(origin);
                                        addMarkers(destination);

                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                        // resultText.setText("");
                                        Route route = direction.getRouteList().get(routenumber);
                                        String color = colors[routenumber % colors.length];
                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                        mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));

                                        Info distanceinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                        Info durationinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                        sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                        for (LatLng position : sectionList) {

                                            MarkerOptions marker5 = new MarkerOptions().position(
                                                    position);

                                            marker5.icon(BitmapDescriptorFactory
                                                    .fromResource(R.drawable.ic_leglist_place));

                                            mMap.addMarker(marker5);

                                        }
                                        step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();

                                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                        for (PolylineOptions polylineOption : polylineOptionList) {
                                            mMap.addPolyline(polylineOption);
                                        }
                                        directionInfo.delete(0, directionInfo.length());
                                        directionInfo.append("\n"+getString(R.string.direction_info_msg_start)+" \n"+getString(R.string.direciton_info_distance)+" " + distanceinfo.getText() + "\n"+getString(R.string.direction_info_duration)+" " + durationinfo.getText());
                                    }catch(Exception e)
                                    {
                                        Toast.makeText(getApplicationContext(),R.string.direction_error,Toast.LENGTH_LONG).show();

                                    }
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                                Toast.makeText(getApplicationContext(),R.string.direction_error,Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
        walkDirectionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleDirection.withServerKey(serverKey)
                        .from(origin)
                        .to(destination)
                        .transportMode(TransportMode.WALKING)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(final Direction direction, String rawBody) {
                                // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                if (direction.isOK()) {

                                    walkDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(200, 255, 200)));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        walkDirectionButton.setForegroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));
                                    }
                                    busDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));
                                    driveDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));

                                    try {

                                        mMap.clear();
                                        addMarkers(origin);
                                        addMarkers(destination);
                                        Log.d("blablabla", "here");

                                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                        resultText.setText("");
                                        Route route = direction.getRouteList().get(routenumber);
                                        String color = colors[routenumber % colors.length];
                                        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                        mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));

                                        Info distanceinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                        Info durationinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                        sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                        for (LatLng position : sectionList) {

                                            MarkerOptions marker5 = new MarkerOptions().position(
                                                    position);

                                            marker5.icon(BitmapDescriptorFactory
                                                    .fromResource(R.drawable.ic_leglist_place));

                                            mMap.addMarker(marker5);

                                        }
                                        step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();

                                        ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                        for (PolylineOptions polylineOption : polylineOptionList) {
                                            mMap.addPolyline(polylineOption);
                                        }
                                        directionInfo.delete(0, directionInfo.length());
                                        directionInfo.append("\n"+getString(R.string.direction_info_msg_start)+" \n"+getString(R.string.direciton_info_distance)+" " + distanceinfo.getText() + "\n"+getString(R.string.direction_info_duration)+" " + durationinfo.getText());
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(),R.string.direction_error,Toast.LENGTH_LONG).show();

                                        Log.d("blablabla",e.toString());
                                    }

                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                                Toast.makeText(getApplicationContext(),R.string.direction_error,Toast.LENGTH_LONG).show();

                            }
                        });

            }
        });

        busDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    busDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(200, 255, 200)));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        busDirectionButton.setForegroundTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
                    }

                    walkDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));
                    driveDirectionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 255, 255)));

                    Location srcLoc = new Location("");
                    srcLoc.setLatitude(origin.latitude);
                    srcLoc.setLongitude(origin.longitude);

                    Location destLoc = new Location("");
                    destLoc.setLatitude(destination.latitude);
                    destLoc.setLongitude(destination.longitude);

                    if (srcLoc.distanceTo(destLoc) <= 1000) {
                        Toast.makeText(getApplicationContext(),getString(R.string.bus_route_notfound_msg),Toast.LENGTH_LONG).show();
                    } else {

                        Location originBus = new Location(findShortest(origin));
                        Location destBus = new Location(findShortest(destination));


                        boolean originFound = false, destFound = false;

                        for (int i = 0; i < allRoutes.size(); i++) {
                            for (int j = 0; j < allRoutes.get(i).size(); j++) {
                                if (originBus.getProvider().equals(allRoutes.get(i).get(j).getProvider()))
                                    originFound = true;
                                if (destBus.getProvider().equals(allRoutes.get(i).get(j).getProvider()))
                                    destFound = true;
                            }
                            if (originFound && destFound) {
                                Log.d("routenumber", "Same route Route Number" + i);
                                selectRouteNumber = i;
                                break;
                            }
                            originFound = false;
                            destFound = false;
                        }

                        if (!originFound || !destFound) {
                            nearRouteBusLocation = new Location(findShortestAlternateRoute(destination));
                            nearRouteBusLatLng = new LatLng(nearRouteBusLocation.getLatitude(), nearRouteBusLocation.getLongitude());
                        }


//        Log.d("busdistance",originBusPosition+"");


                        GoogleDirection.withServerKey(serverKey)
                                .from(origin)
                                .to(new LatLng(originBus.getLatitude(), originBus.getLongitude()))
                                .transportMode(TransportMode.DRIVING)
                                .alternativeRoute(true)
                                .execute(new DirectionCallback() {
                                    @Override
                                    public void onDirectionSuccess(final Direction direction, String rawBody) {
                                        // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                        if (direction.isOK()) {


                                            mMap.clear();
                                            addMarkers(origin);
                                            addMarkers(destination);

                                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            Route route = direction.getRouteList().get(routenumber);
                                            String color = colors[routenumber % colors.length];
                                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.BLUE));

                                            Info distanceinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                            Info durationinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                            sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                            for (LatLng position : sectionList) {

                                                MarkerOptions marker5 = new MarkerOptions().position(
                                                        position);

                                                marker5.icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.ic_leglist_place));

                                                mMap.addMarker(marker5);

                                            }
                                            step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();
                                            resultText.append("You place to nearest  Bus Station: \n" + ""+getString(R.string.direciton_info_distance)+" " + distanceinfo.toString() + "\n"+getString(R.string.direction_info_duration)+" " + durationinfo.toString() + "\n");
                                        }


//                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
//                            for (PolylineOptions polylineOption : polylineOptionList) {
//                                mMap.addPolyline(polylineOption);
//                            }

                                    }

                                    @Override
                                    public void onDirectionFailure(Throwable t) {
                                        // Do something here
                                        Toast.makeText(getApplicationContext(), R.string.direction_error, Toast.LENGTH_LONG).show();

                                    }
                                });
                        GoogleDirection.withServerKey(serverKey)
                                .from(destination)
                                .to(new LatLng(destBus.getLatitude(), destBus.getLongitude()))
                                .transportMode(TransportMode.DRIVING)
                                .alternativeRoute(true)
                                .execute(new DirectionCallback() {
                                    @Override
                                    public void onDirectionSuccess(final Direction direction, String rawBody) {
                                        // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                        if (direction.isOK()) {


                                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            Route route = direction.getRouteList().get(routenumber);
                                            String color = colors[routenumber % colors.length];
                                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.BLUE));

                                            Info distanceinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDistance();
                                            Info durationinfo = direction.getRouteList().get(routenumber).getLegList().get(0).getDuration();

                                            sectionList = direction.getRouteList().get(routenumber).getLegList().get(0).getSectionPoint();
                                            for (LatLng position : sectionList) {

                                                MarkerOptions marker5 = new MarkerOptions().position(
                                                        position);

                                                marker5.icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.ic_leglist_place));

                                                mMap.addMarker(marker5);

                                            }
                                            step = direction.getRouteList().get(routenumber).getLegList().get(0).getStepList();
                                            resultText.append("You place to nearest  Bus Station: \n" + ""+getString(R.string.direciton_info_distance)+" " + distanceinfo.toString() + "\n"+getString(R.string.direction_info_duration)+" " + durationinfo.toString() + "\n");

//                            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.BLUE, 5, Color.BLUE);
//                            for (PolylineOptions polylineOption : polylineOptionList) {
//                                mMap.addPolyline(polylineOption);
//                            }

                                        }
                                    }

                                    @Override
                                    public void onDirectionFailure(Throwable t) {
                                        // Do something here
                                        Toast.makeText(getApplicationContext(), R.string.direction_error, Toast.LENGTH_LONG).show();

                                    }
                                });


                        if (originFound && destFound) {
                            String busPositions = busStandPositions(selectRouteNumber, originBus, destBus);
                            String[] busPositionArr = busPositions.split(",");
                            int originBusPosition = Integer.parseInt(busPositionArr[0]);
                            int destBusPosition = Integer.parseInt(busPositionArr[1]);
                            Log.d("routenumber", "firstCondition");
                            new DrawBusRouteBackground(originBusPosition, destBusPosition, new LatLng(originBus.getLatitude(), originBus.getLongitude()), new LatLng(destBus.getLatitude(), destBus.getLongitude()), 0, 0).execute();
                        } else {
                            Log.d("routenumber", "secondCondition");
                            String busPositions = busStandPositions(findRouteNumber(originBus, nearRouteBusLocation), originBus, nearRouteBusLocation);
                            String[] busPositionArr = busPositions.split(",");
                            int originBusPosition = Integer.parseInt(busPositionArr[0]);
                            int destBusPosition = Integer.parseInt(busPositionArr[1]);
                            Log.d("routenumber", "" + findRouteNumber(originBus, nearRouteBusLocation));

                            Log.d("routecheck", "secondCondition" + originBusPosition + "," + destBusPosition + "," + nearRouteBusLatLng);

                            new DrawBusRouteBackground(originBusPosition, destBusPosition, new LatLng(originBus.getLatitude(), originBus.getLongitude()), nearRouteBusLatLng, 0, 0).execute();

                            String busPositions2 = busStandPositions(findRouteNumber(nearRouteBusLocation, destBus), nearRouteBusLocation, destBus);
                            String[] busPositionArr2 = busPositions2.split(",");
                            int originBusPosition2 = Integer.parseInt(busPositionArr2[0]);
                            int destBusPosition2 = Integer.parseInt(busPositionArr2[1]);
                            Log.d("routenumber", "" + findRouteNumber(nearRouteBusLocation, destBus));
                            Log.d("routecheck", "secondCondition" + originBusPosition2 + "," + destBusPosition2 + "," + nearRouteBusLatLng);

                            new DrawBusRouteBackground(originBusPosition2, destBusPosition2, nearRouteBusLatLng, new LatLng(destBus.getLatitude(), destBus.getLongitude()), 1, 1).execute();
                        }
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(),R.string.direction_error,Toast.LENGTH_LONG).show();

                }

            }});

//        driveDirectionButton.performClick();
        new BackgroundWorker(NavDrawerDirectionFinal.this).execute();

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent=new Intent(NavDrawerDirectionFinal.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
//            super.onBackPressed();
        }
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);String mapType=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("maptype","1");

        try {
            mMap.setMapType(Integer.parseInt(mapType));
        }catch(Exception e)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);

        mMap.setPadding(0,0,50,0);

        // add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void addMarkers(LatLng latLng)
    {
        Log.d("MARKER","MARKER ADDED");
        String title="";
        if(latLng.equals(origin))
            title="You are here";
        else if(latLng.equals(destination))
            title="Destination";
        MarkerOptions marker = new MarkerOptions().position(
                latLng).title(title);

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place)
        );
        mMap.addMarker(marker);

        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition2));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.get_direction_screen) {
            // Handle the camera action
            Intent intent=new Intent(getApplicationContext(),NavDrawerMainMenuActivity.class);
            startActivity(intent);


        }
        else if(id==R.id.nav_share_location)
        {
            String title="Location Shared from Metrav!";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, title+"\n"+new ShareLocation(getApplicationContext(),getParent()).getPosition());
            startActivity(Intent.createChooser(share, "Share using"));

        }
        else if(id==R.id.nav_bus_offline)
        {

            Intent intent=new Intent(getApplicationContext(),NavDrawerBusOffline.class);
            startActivity(intent);
        }


        else if(id==R.id.nav_emergency_police)
        {
            Intent intent=new Intent(getApplicationContext(),NavDrawerEmergencyContacts.class);
            startActivity(intent);
        }

        else if (id == R.id.find_place_screen) {
            Log.d("ERRORR","CLICKED");
            try{
                Intent intent=new Intent(getApplicationContext(),NavDrawerPlaceSearchActivity.class);
                startActivity(intent);
            }catch(Exception e)
            {
                Log.d("ERRORR",e.toString());
            }
        } else if (id == R.id.nav_settings) {
            Intent i=new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_traffic_window) {
            Intent intent=new Intent(getApplicationContext(),NavDrawerTrafficWindow.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            SharedPreferences settings = getSharedPreferences("Metrav", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("LoggedIn",0);
            editor.commit();
            Intent intent=new Intent(getApplicationContext(),AppLogin.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_call_uber)
        {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.ubercab");
            if (intent != null)
            {
/* we found the activity now start the activity */
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else
            {
/* bring user to the market or let them choose an app? */
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id="+"com.ubercab"));
                startActivity(intent);
            }
            // Handle the camera action
        }


        else if(id== R.id.nav_call_pathao)
        {

            Intent intent = getPackageManager().getLaunchIntentForPackage("com.pathao.user");
            if (intent != null)
            {
/* we found the activity now start the activity */
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else
            {
/* bring user to the market or let them choose an app? */
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("market://details?id="+"com.pathao.user"));
                startActivity(intent);
            }

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class DrawBusRouteBackground extends AsyncTask<String,Void,String> {
String url;
        int originBusPostion,destBusPosition;

        LatLng origin,destination;
        int separateRoute=0,colorState=0;

        DrawBusRouteBackground(int i, int j, LatLng l1, LatLng l2, int a, int b)
        {
            Log.d("routecheck","inside constructor");
            originBusPostion=i;
            destBusPosition=j;
            origin=l1;
            destination=l2;
            separateRoute=a;
            colorState=b;
            Log.d("routecheck","inside constructor end");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("routecheck","do in back");
            String json=getUrlContents(url);
            Log.d("routescheck",url);
            JSONObject object = null;
            try {
                object = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray array = null;
            try {
                array = object.getJSONArray("routes");
                JSONArray legs=array.getJSONObject(0).getJSONArray("legs");
                for(int i=0;i<legs.length();i++)
                {
                    String end=legs.getJSONObject(i).getString("end_address");
                    Double lat=legs.getJSONObject(i).getJSONObject("end_location").getDouble("lat");
                    Double lon=legs.getJSONObject(i).getJSONObject("end_location").getDouble("lng");
                    LatLng endLatLng=new LatLng(lat,lon);
                    latLngArrayList.add(endLatLng);
                    Log.d("routes","end"+end+"latlng: "+lat+","+lon);
                }

                Log.d("routesjson",array.getJSONObject(0).getJSONArray("legs").toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("routes",e.toString());
            }
            return json;

        }

        @Override
        protected void onPreExecute() {

            Log.d("routecheck","on Pre");
            url=makeURL();
        }

        @Override
        protected void onPostExecute(String result) {
            for(int i=0;i<latLngArrayList.size();i++)
            {
                Log.d("routeLatLng",latLngArrayList.get(i).toString());

            }

            drawPath(result);
//            Polyline line = mMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
//            line.setPoints(latLngArrayList);
            CameraPosition cameraPosition2 = new CameraPosition.Builder()
                    .target(origin).zoom(14).build();

            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition2));
//            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), latLngArrayList, 10, Color.BLUE));

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        public String makeURL()
        {
            StringBuilder baseURL=new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=");
            baseURL.append(origin.latitude+","+origin.longitude);
            baseURL.append("&destination="+destination.latitude+","+destination.longitude);
            baseURL.append("&waypoints=");
            Log.d("busdistance","insideURL"+originBusPostion+"");
            for(int i=originBusPostion;i<=destBusPosition;i++)
            {
                if(separateRoute==0)
                {
                    baseURL.append(route1.get(i).getLatitude()+","+ route1.get(i).getLongitude());
                    if(i!= route1.size()-1)
                        baseURL.append("&");
                }
                else
                {
                    baseURL.append(route2.get(i).getLatitude()+","+ route2.get(i).getLongitude());
                    Log.d("routecheck","Traversing "+ route2.get(i).getProvider()+"");

                    if(i!= route2.size())
                        baseURL.append("&");

                }


                Log.d("busdistance","insideURL"+ route1.get(i).getProvider()+"");

            }
            baseURL.append("&key=AIzaSyAn4t6lMgzYWOHDOEkSGPy8ARfkByiIybk");

            Log.d("routes",baseURL.toString());
            Log.d("routecheck",baseURL.toString());

            return baseURL.toString();
        }
        private String getUrlContents(String theUrl)
        {
            String weather = theUrl;
            try {
                Log.d("routes","here\n"+weather);

                URL url = new URL(theUrl);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
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
//                Log.d("routes","JSON"+weather);
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return weather;
        }
        public void drawPath(String  result) {

            try {
                //Tranform the string into a json object
                final JSONObject json = new JSONObject(result);
                JSONArray routeArray = json.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);

                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                List<LatLng> list = decodePoly(encodedString);
                if(separateRoute==0 && colorState==0){
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.RED)//Google maps blue color
                            .geodesic(true)
                    );
                }
                else if(separateRoute==1 && colorState==0)
                {
                    Log.d("routecheck","Draw");
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.RED)//Google maps blue color
                            .geodesic(true)
                    );
                }
                else
                {
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.GREEN)//Google maps blue color
                            .geodesic(true)

                    );

                }
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
            }
            catch (JSONException e) {
                Log.d("routecheck",e.toString());
            }
        }
    }

    public void busStandAdd()
    {
        Location location=new Location("");
        location.setLatitude(23.87985);
        location.setLongitude(90.40132);
        location.setProvider("Abdullahpur");
        route1.add(location);

        Location location1=new Location("Abdullahpur Counter Buse");
        location1.setLatitude(23.87746);
        location1.setLongitude(90.40127);
        route1.add(location1);

        Location location3=new Location("House Building");
        location3.setLatitude(23.87372);
        location3.setLongitude(90.40073);
        route1.add(location3);


        Location location4=new Location("House Building Counter");
        location4.setLatitude(23.87219);
        location4.setLongitude(90.40073);
        route1.add(location4);

        Location location5=new Location("Ajampur Counter");
        location5.setLatitude(23.86936);
        location5.setLongitude(90.40047);
        route1.add(location5);

        Location location6=new Location("Ajampur Local");
        location6.setLatitude(23.86836);
        location6.setLongitude(90.40041);
        route1.add(location6);

        Location location7=new Location("Rajlokkhi");
        location7.setLatitude(23.86451);
        location7.setLongitude(90.40016);
        route1.add(location7);

        Location location8=new Location("Rajlokkhi Counter");
        location8.setLatitude(23.86359);
        location8.setLongitude(90.40012);
        route1.add(location8);

        Location location9=new Location("Jasimuddin");
        location9.setLatitude(23.86089);
        location9.setLongitude(90.40043);
        route1.add(location9);


        Location location10=new Location("Airport");
        location10.setLatitude(23.85151);
        location10.setLongitude(90.40789);
        route1.add(location10);

        Location location11=new Location("Kawla");
        location11.setLatitude(23.84623);
        location11.setLongitude(90.41216);
        route1.add(location11);

        Location location12=new Location("Khilkhet");
        location12.setLatitude(23.82997);
        location12.setLongitude(90.41993);
        route1.add(location12);

        Location location13=new Location("Khilkhet South");
        location13.setLatitude(23.82822);
        location13.setLongitude(90.42020);
        route1.add(location13);


        Location location14=new Location("Bishwa Road");
        location14.setLatitude(23.82118);
        location14.setLongitude(90.41865);
        route1.add(location14);

        Location location15=new Location("Shewra");
        location15.setLatitude(23.81841);
        location15.setLongitude(90.41443);
        route1.add(location15);

        Location location16=new Location("MES");
        location16.setLatitude(23.81615);
        location16.setLongitude(90.40532);
        route1.add(location16);

        Location location17=new Location("Kakoli");
        location17.setLatitude(23.79495);
        location17.setLongitude(90.40120);
        route1.add(location17);

        Location location18=new Location("Banani South");
        location18.setLatitude(23.79333);
        location18.setLongitude(90.40093);
        route1.add(location18);

        Location location19=new Location("Shainik Club");
        location19.setLatitude(23.79043);
        location19.setLongitude(90.40043);
        route1.add(location19);

        Location location20=new Location("Chairman Bari");
        location20.setLatitude(23.78805);
        location20.setLongitude(90.40003);
        route1.add(location20);

        Location location21=new Location("Mohakhali");
        location21.setLatitude(23.78110);
        location21.setLongitude(90.39890);
        route1.add(location21);




        route2.add(location21);
        Location location22=new Location("Wireless More");
        location22.setLatitude(23.78066);
        location22.setLongitude(90.40553);
        route2.add(location22);

        Location location23=new Location("TB Gate");
        location23.setLatitude(23.78029);
        location23.setLongitude(90.40938);
        route2.add(location23);

        Location location24=new Location("Gulshan Bridge");
        location24.setLatitude(23.78040);
        location24.setLongitude(90.41290);
        route2.add(location24);

        Location location25=new Location("Gulshan-1");
        location25.setLatitude(23.78033);
        location25.setLongitude(90.41620);
        route2.add(location25);

        allRoutes.add(route1);
        allRoutes.add(route2);


    }

    public Location findShortest(LatLng src)
    {
        Location srcLoc=new Location("");
        srcLoc.setLatitude(src.latitude);
        srcLoc.setLongitude(src.longitude);

        final int[] distanceToBusR1=new int[route1.size()];
        final int[] distanceToBusR2=new int[route2.size()];

        for(int i = 0; i< route1.size(); i++)
        {
            distanceToBusR1[i]= (int) route1.get(i).distanceTo(srcLoc);
        }
        for(int i = 0; i< route2.size(); i++)
        {
            distanceToBusR2[i]= (int) route2.get(i).distanceTo(srcLoc);
        }
       // Arrays.sort(distanceToBusR1);

        final Integer[] idx = new Integer[distanceToBusR1.length];
        for(int i=0;i<idx.length;i++)
        {
         idx[i]=i;
        }

        final Integer[] idx2 = new Integer[distanceToBusR2.length];
        for(int i=0;i<idx2.length;i++)
        {
            idx2[i]=i;
        }

        Arrays.sort(idx, new Comparator<Integer>() {
            @Override public int compare(final Integer o1, final Integer o2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Integer.compare(distanceToBusR1[o1], distanceToBusR1[o2]);
                }
                return 0;
            }
        });
        Arrays.sort(idx2, new Comparator<Integer>() {
            @Override public int compare(final Integer o1, final Integer o2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Integer.compare(distanceToBusR2[o1], distanceToBusR2[o2]);
                }
                return 0;
            }
        });

/*
        for(int i = 0; i< route1.size(); i++)
        {
            Log.d("busdistance",idx[i]+","+distanceToBusR1[idx[i]]+","+ route1.get(idx[i]).getProvider());
        }
        for(int i = 0; i< route2.size(); i++)
        {
            Log.d("busdistance",idx2[i]+","+distanceToBusR2[idx2[i]]+","+ route2.get(idx2[i]).getProvider());
        }
        */




        if(distanceToBusR2[0]<distanceToBusR1[0])
        {
            Log.d("busdistance","Selecting "+route2.get(idx2[0]).getProvider());
            return route2.get(idx2[0]);
        }
        else
        {
            Log.d("busdistance","Selecting "+route1.get(idx[0]).getProvider());
            return route1.get(idx[0]);
        }
    }
    public Location findShortestAlternateRoute(LatLng src)
    {
        Location srcLoc=new Location("");
        srcLoc.setLatitude(src.latitude);
        srcLoc.setLongitude(src.longitude);

        int srcRouteNumber=0;

        for(int i=0;i<allRoutes.size();i++)
        {
            for(int j=0;j<allRoutes.get(i).size();j++)
            {
                if(allRoutes.get(i).get(j).distanceTo(srcLoc)==0)
                {
                   srcRouteNumber=i;
                    break;
                }
            }
        }

        if(srcRouteNumber==0)
            srcRouteNumber=1;
        else if(srcRouteNumber==1)
            srcRouteNumber=0;


        final int[] distanceToBusR1=new int[route1.size()];
        final int[] distanceToBusR2=new int[route2.size()];

        if(srcRouteNumber==1)
        {
            for(int i = 0; i< route1.size(); i++)
            {
                distanceToBusR1[i]= (int) route1.get(i).distanceTo(srcLoc);
            }

            final Integer[] idx = new Integer[distanceToBusR1.length];
            for(int i=0;i<idx.length;i++)
            {
                idx[i]=i;
            }
            Arrays.sort(idx, new Comparator<Integer>() {
                @Override public int compare(final Integer o1, final Integer o2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        return Integer.compare(distanceToBusR1[o1], distanceToBusR1[o2]);
                    }
                    return 0;
                }
            });

            return route1.get(idx[0]);
        }
        else
        {
            for(int i = 0; i< route2.size(); i++)
            {
                distanceToBusR2[i]= (int) route2.get(i).distanceTo(srcLoc);
            }

            final Integer[] idx2 = new Integer[distanceToBusR2.length];
            for(int i=0;i<idx2.length;i++)
            {
                idx2[i]=i;
            }
            Arrays.sort(idx2, new Comparator<Integer>() {
                @Override public int compare(final Integer o1, final Integer o2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        return Integer.compare(distanceToBusR2[o1], distanceToBusR2[o2]);
                    }
                    return 0;
                }
            });
            return route2.get(idx2[0]);


        }
        // Arrays.sort(distanceToBusR1);

    }

    public String busStandPositions(int routeNumber,Location originBus,Location destBus)
    {
        int originBusPosition=0;
        int destBusPosition=0;
        Log.d("routecheck",allRoutes.get(routeNumber).size()+""+originBus+","+destBus);
        for(int i = 0; i< allRoutes.get(routeNumber).size(); i++)
        {
            Log.d("routecheck",allRoutes.get(routeNumber).get(i)+"");
            if(allRoutes.get(routeNumber).get(i).getProvider().equals(originBus.getProvider()))
            {

                originBusPosition=i;
            }
            else if(allRoutes.get(routeNumber).get(i).getProvider().equals(destBus.getProvider()))
            {
                destBusPosition=i;
            }
        }

        return originBusPosition+","+destBusPosition;
    }

    public int findRouteNumber(Location l1,Location l2)
    {
        boolean frst=false,scnd=false;
        for(int i=0;i<allRoutes.size();i++)
        {
            for(int j=0;j<allRoutes.get(i).size();j++)
            {
                if(allRoutes.get(i).get(j).getProvider().equals(l1.getProvider()))
                {
                    Log.d("routenumber","route "+i+"check "+allRoutes.get(i).get(j).getProvider()+" and "+l1.getProvider());
                    frst=true;
                }
                else if(allRoutes.get(i).get(j).getProvider().equals(l2.getProvider()))
                {
                    Log.d("routenumber","route "+i+"check "+allRoutes.get(i).get(j).getProvider()+" and "+l2.getProvider());
                    scnd=true;
                }
            }
            if(frst && scnd)
            {
                Log.d("routenumber",i+"");
                return i;
            }
            else{
                frst=false;
                scnd=false;
            }
        }
        return 0;
    }

    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker

        dialogView.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_transparent_back));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        }
        resultText.setText(directionInfo);
        Button okBtn=(Button)dialogView.findViewById(R.id.directionPopupOkBtn);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setCancelable(false);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)dialogView.getParent()).removeView(dialogView); // <- fix
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    class BackgroundWorker extends AsyncTask<Void, Void, Void>
    {
        Context context;
        ProgressDialog progressDialog;

        BackgroundWorker(Context context)
        {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Getting the directions");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            performDrive();
            Log.d("CHECKBACK",progressDialog.isShowing()+" state");
            return null;
        }
    }

    public void performDrive()
    {
        driveDirectionButton.performClick();
    }
}
