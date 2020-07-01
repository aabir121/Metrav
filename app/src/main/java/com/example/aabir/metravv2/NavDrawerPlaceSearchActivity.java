package com.example.aabir.metravv2;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.akexorcist.googledirection.model.Info;
import com.example.aabir.metravv2.Utility.Place;
import com.example.aabir.metravv2.Utility.PlaceAutocompleteAdapter;
import com.example.aabir.metravv2.Utility.ShareLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerPlaceSearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemSelectedListener,GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,GoogleMap.OnMapLongClickListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener, View.OnClickListener {

    //location

    private GoogleMap mMap;
    MarkerOptions myPositionMarker;
    MarkerOptions markerHere;


    //PlaceListView
    LinearLayout listLayout;
    ListView placeListView;
    String placeListString=null;



    //PlaceSearchLayout
    LinearLayout placeTypeLayout;
    static float windowHeight;
    static float windowWidth;
    FloatingActionButton placeTypeBtn;
    Button[] placeTypeSelectBtn=new Button[10];
    ImageButton popupListLayoutBtn;
    Animation animation;
    boolean placeClicked=false;

    private AutoCompleteTextView mAutocompleteViewSource;
    FloatingActionButton getCurrentLocationButton;
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(
            new LatLng(23.549686, 90.056174 ), new LatLng(24.018418, 90.514853));

    HoloCircleSeekBar picker;
//    SeekBar seekBar;
//    EditText distanceText;
    //    NumberPicker numberPicker;
    List<Map<String, String>> items;
    Button testButtonPlace;
    String placeType=null;

    //for async task
    private static final String APP_ID = "AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI";
    int pos=0;
    ProgressDialog progressDialog;
    Double lat,lon;
    LatLng positionLatlng;
    ArrayList<Place> arrayList= new ArrayList<Place>();
    StringBuilder placeList=new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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


        setContentView(R.layout.activity_nav_drawer_place_search);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

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


        Display display = getWindowManager().getDefaultDisplay();
        windowWidth = display.getWidth();
        windowHeight = display.getHeight();


        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select","English").equals("বাংলা"))
        {
            try {
                 //  navigationView.getMenu().getItem(5).setTitle("বাহির");
            }catch(Exception e)
            {
                Log.d("bangla",e.toString());
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mAutocompleteViewSource = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_source_place_search_activity);
        getCurrentLocationButton = (FloatingActionButton) findViewById(R.id.current_location_search_button_place_search_activity);

        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_BANGLADESH,
                null);
        mAutocompleteViewSource.setAdapter(mAdapter);


        listLayout=(LinearLayout)findViewById(R.id.listViewLayout);
        placeListView=(ListView)findViewById(R.id.placeListView);
        placeTypeLayout=(LinearLayout)findViewById(R.id.placeTypeLayout);
        picker=(HoloCircleSeekBar)findViewById(R.id.picker);
        placeTypeBtn=(FloatingActionButton)findViewById(R.id.placeTypeSelectBtn);
        popupListLayoutBtn=(ImageButton)findViewById(R.id.popupListLayoutBtn);



        placeTypeSelectBtn[0]=(Button)findViewById(R.id.placeTypeBtn1);
        placeTypeSelectBtn[1]=(Button)findViewById(R.id.placeTypeBtn2);
        placeTypeSelectBtn[2]=(Button)findViewById(R.id.placeTypeBtn3);
        placeTypeSelectBtn[3]=(Button)findViewById(R.id.placeTypeBtn4);
        placeTypeSelectBtn[4]=(Button)findViewById(R.id.placeTypeBtn5);
        placeTypeSelectBtn[5]=(Button)findViewById(R.id.placeTypeBtn6);
        placeTypeSelectBtn[6]=(Button)findViewById(R.id.placeTypeBtn7);
        placeTypeSelectBtn[7]=(Button)findViewById(R.id.placeTypeBtn8);
        placeTypeSelectBtn[8]=(Button)findViewById(R.id.placeTypeBtn9);
        placeTypeSelectBtn[9]=(Button)findViewById(R.id.placeTypeBtn10);



        placeTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(placeTypeLayout.getVisibility()==View.INVISIBLE)
                expand(placeTypeLayout,1);
                else
                    collapse(placeTypeLayout);
            }
        });


        for(int i=0;i<10;i++){
            placeTypeSelectBtn[i].setOnClickListener(this);
        }

//            txt=(TextView)findViewById(R.id.demoTextView);
//        seekBar=(SeekBar)findViewById(R.id.seekBarDemo);
//        distanceText=(EditText)findViewById(R.id.distanceTextDemo);
//            numberPicker=(NumberPicker)findViewById(R.id.numberPicker);
        testButtonPlace=(Button)findViewById(R.id.testButtonPlace);

//            numberPicker.setMaxValue(2000);
//            numberPicker.setMinValue(100);

        progressDialog = new ProgressDialog(NavDrawerPlaceSearchActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);


//            numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//                @Override
//                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
////                    txt.setText(""+newVal);
//                    distanceText.setText(""+newVal);
//                }
//            });

//        distanceText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(Double.parseDouble(distanceText.getText().toString())>2000)
//                    Toast.makeText(getApplicationContext(),"Radius Cannot be more than 2000 meters.",Toast.LENGTH_LONG);
//
//                return false;
//            }
//        });
//        seekBar.setMax(2000);




        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // get the last know location from your location manager.
                boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

                try {
                    if (permissionGranted) {
                        // {Some Code}
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        // now get the lat/lon from the location and do something with it.
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        positionLatlng=latLng;
                        String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
                        Toast.makeText(getApplicationContext(),"Current place selected",Toast.LENGTH_LONG);
                        lat=positionLatlng.latitude;
                        lon=positionLatlng.longitude;

                        myPositionMarker = new MarkerOptions().position(
                                latLng).title(addresses.get(0).getAddressLine(0));

                        myPositionMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place));
//
                        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                                .target(latLng).zoom(16).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition2));
//
                        mMap.addMarker(myPositionMarker);

                        mAutocompleteViewSource.setText(ad);
//                        mAutocompleteViewSource.setEnabled(false);

                    } else {
                        ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                }catch(Exception e)
                {
                    mAutocompleteViewSource.setText(e.toString());
                }

            }
        });




        testButtonPlace.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(picker.getValue()!=0 && placeType!=null)
                {
                    placeClicked=true;
                    popupListLayoutBtn.setVisibility(View.VISIBLE);
                    popupListLayoutBtn.setAnimation(animation);
                    Log.d("PLACETYPE",placeType);
                    SelectedPlaceFindBackgroundInner task=new SelectedPlaceFindBackgroundInner(progressDialog);
                    task.execute(""+lat,""+lon,placeType, String.valueOf(picker.getValue()));


//                    try {
//                        placeListString=task.get().toString();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }

                    Toast.makeText(getApplicationContext(),placeListString,Toast.LENGTH_LONG);


                }
                else if(picker.getValue()==0)
                {
                    makeAlertBox(getString(R.string.error_distance_zero_msg));
                }
                else if(placeType==null)
                {
                    makeAlertBox(getString(R.string.error_no_place_selected_msg));
                }





//                Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMapActivity.class);
//                intent.putExtra("placetype",placeType);
//                intent.putExtra("myplace",""+lat+","+lon);
//                intent.putExtra("placelist",placeListString);
//                startActivity(intent);
            }
        });

        animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        popupListLayoutBtn.setVisibility(View.INVISIBLE);

        popupListLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listLayout.getVisibility()==View.GONE)
                {
                    expand(listLayout,2);
                    popupListLayoutBtn.setAnimation(null);
                }
                else
                {
                    collapse(listLayout);
                    popupListLayoutBtn.setAnimation(animation);
                    popupListLayoutBtn.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(listLayout.getVisibility()==View.VISIBLE)
        {
            collapse(listLayout);
            listLayout.setVisibility(View.INVISIBLE);
        }
        else if(placeTypeLayout.getVisibility()==View.VISIBLE)
        {
            collapse(placeTypeLayout);
        }
        else if(placeClicked){
            mMap.clear();
            getCurrentLocationButton.performClick();
            picker.setValue(0);
            placeClicked=false;
            popupListLayoutBtn.setAnimation(null);
            popupListLayoutBtn.setVisibility(View.INVISIBLE);
            testButtonPlace.setVisibility(View.VISIBLE);
        }

        else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMainMenuActivity.class);
            startActivity(intent);
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_place_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.get_direction_screen) {
            NavDrawerPlaceSearchActivity.this.finish();
            Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMainMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            // Handle the camera action
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

        else if(id==R.id.nav_share_location)
        {
            String title="Location Shared from Metrav!";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, title+"\n"+new ShareLocation(getApplicationContext(),getParent()).getPosition());
            startActivity(Intent.createChooser(share, "Share using"));

        }

        else if (id == R.id.nav_emergency_police) {
            Intent intent=new Intent(getApplicationContext(),NavDrawerEmergencyContacts.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_traffic_window) {
            Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerTrafficWindow.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            SharedPreferences settings = getSharedPreferences("Metrav", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("LoggedIn",0);
            editor.commit();
            Intent intent=new Intent(NavDrawerPlaceSearchActivity.this,AppLogin.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        txt.setText(items.get(position).get("subText").toString());
        placeType=items.get(position).get("subText").toString();
//        placeTypeLayout.getLayoutParams().height=700;
//        placeTypeLayout.setVisibility(View.VISIBLE);

//        txt.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        String mapType=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("maptype","1");

        try {
            mMap.setMapType(Integer.parseInt(mapType));
        }catch(Exception e)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0,0,20,240);



        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        try {
            if (permissionGranted) {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                positionLatlng=latLng;
                lat=location.getLatitude();
                lon=location.getLongitude();
                String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
                Toast.makeText(getApplicationContext(),"Current place selected",Toast.LENGTH_LONG);

                myPositionMarker = new MarkerOptions().position(
                        latLng).title(addresses.get(0).getAddressLine(0));

                myPositionMarker.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_place));
//
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(latLng).zoom(16).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));
//
                mMap.addMarker(myPositionMarker);
                mAutocompleteViewSource.setText(ad);

            } else {
                ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT);
        }


        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        mMap.addMarker(myPositionMarker);
        markerHere = new MarkerOptions().position(
                latLng).title("Source");

        myPositionMarker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerHere.draggable(true);
        mMap.addMarker(markerHere);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(markerHere.getPosition().latitude,markerHere.getPosition().longitude, 1);
            String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
            mAutocompleteViewSource.setText(ad);
            lat=markerHere.getPosition().latitude;
            lon=markerHere.getPosition().longitude;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        final LatLng markerLatLng=marker.getPosition();


        List<Address> addressesDest = null;
        List<Address> addressesSource = null;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addressesDest = geocoder.getFromLocation(markerLatLng.latitude, markerLatLng.longitude, 1);
            addressesSource = geocoder.getFromLocation(lat, lon, 1);

        }catch (IOException e) {
            e.printStackTrace();
        }


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        alertDialog.setTitle(marker.getTitle());

        String message=getString(R.string.direction_prompt_title);
        String yesString=getString(R.string.yes_btn);
        String noString=getString(R.string.cancel_btn);
            alertDialog.setMessage(message);

        final List<Address> finalAddressesSource = addressesSource;
        final List<Address> finalAddressesDest = addressesDest;

        alertDialog.setPositiveButton(yesString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NavDrawerMapWithDirectionActivity.source = finalAddressesSource.get(0).getAddressLine(0);
                        NavDrawerMapWithDirectionActivity.source2 = finalAddressesDest.get(0).getAddressLine(0);
                        NavDrawerMapWithDirectionActivity.s1 = new LatLng(lat,lon);
                        NavDrawerMapWithDirectionActivity.s2 = markerLatLng;

                        NavDrawerDirectionFinal.origin = new LatLng(lat,lon);
                        NavDrawerDirectionFinal.destination = markerLatLng;
                        NavDrawerDirectionFinal.originName = finalAddressesSource.get(0).getAddressLine(0);
                        NavDrawerDirectionFinal.destinationName = finalAddressesDest.get(0).getAddressLine(0);

                        mAutocompleteViewSource.setText("");
//                        distanceText.setText("0");
//                        seekBar.setProgress(0);
                        picker.setInitPosition(0);
                        Intent intent = new Intent(NavDrawerPlaceSearchActivity.this,NavDrawerMapWithDirectionActivity.class);
                        startActivity(intent);


                        // continue with delete
                    }
                });
                alertDialog.setNegativeButton(noString, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // do nothing
                            }
                        });

        alertDialog.show();
//        new AlertDialog.Builder(getApplicationContext())
//                .setTitle("Marker Clicked")
//                .setMessage("Do you want directions to this place?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();



//        Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_LONG);

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


        LatLng latLng=marker.getPosition();
        String markerTitleString=getString(R.string.marker_source_string);

        markerHere = new MarkerOptions().position(
                latLng).title(markerTitleString);

        myPositionMarker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerHere.draggable(true);

        mMap.addMarker(markerHere);

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(markerHere.getPosition().latitude,markerHere.getPosition().longitude, 1);
            String ad=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getAddressLine(2);
            mAutocompleteViewSource.setText(ad);
            lat=markerHere.getPosition().latitude;
            lon=markerHere.getPosition().longitude;
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onClick(View view) {

        Button b=(Button)view;
        Toast.makeText(getApplicationContext(),b.getText()+" selected.",Toast.LENGTH_LONG).show();

        if(view.getId()==R.id.placeTypeBtn1)
        {
            placeType="bus_station";

            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_directions_bus));
            collapse(placeTypeLayout);
        }
        else if(view.getId()==R.id.placeTypeBtn2)
        {
            placeType="atm";
//            Toast.makeText(getApplicationContext(),"ATM",Toast.LENGTH_LONG).show();
            collapse(placeTypeLayout);
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_local_atm));
        }
        else if(view.getId()==R.id.placeTypeBtn3)
        {
            placeType="restaurant";
            collapse(placeTypeLayout);
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_local_dining));
        }
        else if(view.getId()==R.id.placeTypeBtn4)
        {
            collapse(placeTypeLayout);
            placeType="hospital";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_local_hospital));
        }
        else if(view.getId()==R.id.placeTypeBtn5)
        {
            collapse(placeTypeLayout);
            placeType="airport";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_local_airport));
        }
        else if(view.getId()==R.id.placeTypeBtn6)
        {
            collapse(placeTypeLayout);
            placeType="bank";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_store));
        }
        else if(view.getId()==R.id.placeTypeBtn7)
        {
            collapse(placeTypeLayout);
            placeType="school";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_school));
        }
        else if(view.getId()==R.id.placeTypeBtn8)
        {
            collapse(placeTypeLayout);
            placeType="police";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.pin_police_station));
        }
        else if(view.getId()==R.id.placeTypeBtn9)
        {
            collapse(placeTypeLayout);
            placeType="university";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_school));
        }
        else if(view.getId()==R.id.placeTypeBtn10)
        {
            collapse(placeTypeLayout);
            placeType="train_station";
            placeTypeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_train));
        }


    }


    class SelectedPlaceFindBackgroundInner extends AsyncTask<String, Void, String> {
        //        private TextView textView;
        private LatLng latLng;
        int pos=0;
        ProgressDialog progressDialog;
        Geocoder geocoder;
        Context context;
        private static final String APP_ID = "AIzaSyCJIPYN7Zb1AOk3Kar9OAiSQKvNoJIXXpI";
        Info durationinfo,distanceinfo;

        String jsonData=null;
        public SelectedPlaceFindBackgroundInner(ProgressDialog progressDialog) {
//            this.textView = textView;
//        this.latLng=latLng;
            this.progressDialog=progressDialog;
        }

        @Override
        protected String doInBackground(String... strings) {
//        jsonData=getUrlContents(strings[0]);
            Double lat=Double.parseDouble(strings[0]);
            Double lon=Double.parseDouble(strings[1]);
            Double distance=Double.parseDouble(strings[3]);
            return findPlaces(lat,lon,strings[2],distance);

        }
        protected String getJSON(String url) {
            return getUrlContents(url);
        }

        public String findPlaces(double latitude, double longitude, String placeSpacification,Double distance2)
        {
            StringBuilder content = new StringBuilder();
            String urlString = makeUrl(latitude, longitude,placeSpacification,distance2);

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
                LatLng origin=new LatLng(latitude,longitude);

                arrayList.clear();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i));
                        String placeName=place.getName();
                        Log.v("Places Services ", ""+place);

                        content.append(""+placeName+"\n");
                        arrayList.add(place);
                    } catch (Exception e) {
                    }
                }

                double min=1000;

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
                    if(distance<min)
                    {min=distance;
                        pos=i;}
                }

                placeList.delete(0,placeList.length());
//            textView.setText("");
                for(int i=0;i<arrayList.size();i++)
                {
                    placeList.append(arrayList.get(i).getName()+"_");
                    placeList.append(arrayList.get(i).getLatitude()+"_");
                    placeList.append(arrayList.get(i).getLongitude()+";");
//                textView.setText(txt.getText().toString().concat(arrayList.get(i).getName()+","+arrayList.get(i).getLatitude()+","+arrayList.get(i).getLongitude()+"\n"));
                }


                placeListString=placeList.toString();
//        content.append("Distance: "+min+"\n\n");
//            String resultCoordinates=arrayList.get(pos).getLatitude()+","+arrayList.get(pos).getLongitude();
                return placeList.toString();

            } catch (JSONException e) {
            return null;
            }

            }





        private String getUrlContents(String theUrl)
        {
            String data = theUrl;
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
                data=content.toString();

                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String temp) {

            placeListString=temp;
            if(placeListString.equals(""))
            {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(NavDrawerPlaceSearchActivity.this, R.style.myDialog));

                String titleString="No Place Found!",messageString="There are no such place in the given region.\n" +
                        "Please increse the search parameter and try again.\n";
                String okString=getString(R.string.cancel_btn),cancelString=getString(R.string.cancel_btn);
                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select","English").equals("বাংলা")) {
                    titleString="কিছু খুজে পাওয়া যায় নি।";
                    messageString="আপনার নির্ধারিত পরিধির মধ্যে কোন কিছু খুজে পাওয়া যায় নি।\nঅনুগ্রহ করে পরিধি বাড়িয়ে আবার চেষ্টা করুন।";
                    okString="হ্যাঁ";
                }
                alertDialog.setTitle(titleString);
                alertDialog.setMessage(messageString);
                alertDialog.setPositiveButton(okString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with delete
                    }
                });
                alertDialog.setNegativeButton(cancelString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

//                Toast.makeText(getApplicationContext(),"Sorry! No such place found in the given region.",Toast.LENGTH_LONG);
            }

            else{

                final ArrayList<ArrayList<String>> finallist=new ArrayList<>();

                Log.d("placesearcherror",placeListString);
                String[] places=placeListString.split(";");
//        txt.setText(places[4]);

                String[] separatePlaces=null;
                for(int i=0;i<places.length;i++)
                {
                    ArrayList<String> l=new ArrayList<String>();

                    Log.d("placesearcherror","inside loop"+places[i]);
                    separatePlaces=places[i].split("_");


                    l.add(separatePlaces[0]);
                    l.add(separatePlaces[1]);
                    l.add(separatePlaces[2]);
//            txt.setText(separatePlaces[2]);
                    finallist.add(l);
                }


                String typeIcon="pin_bus";
                if(placeType.equals("bus_station"))
                    typeIcon="pin_bus";
                else if(placeType.equals("atm"))
                    typeIcon="pin_atm";
                else if(placeType.equals("restaurant"))
                    typeIcon="pin_restaurant";
                else if(placeType.equals("hospital"))
                    typeIcon="pin_hospital";
                else if(placeType.equals("airport"))
                    typeIcon="pin_airport";
                else if(placeType.equals("bank"))
                    typeIcon="pin_bank";
                else if(placeType.equals("school"))
                    typeIcon="pin_school";
                else if(placeType.equals("police"))
                    typeIcon="pin_police_station";
                else if(placeType.equals("university"))
                    typeIcon="pin_school";
                else if(placeType.equals("train_station"))
                    typeIcon="pin_train";


                Double disVal=Double.parseDouble(String.valueOf(picker.getValue()));

                mMap.clear();
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lon))
                        .radius(disVal)
                        .strokeColor(0x80D2D6FF)
                        .fillColor(0x60D2D6FF));
//                mMap.addMarker(markerHere);
                mMap.addMarker(myPositionMarker);


                Resources res = getResources();
                int resID = res.getIdentifier(typeIcon , "drawable", getPackageName());
                Drawable resDraw = res.getDrawable(resID );
                for(int i=0;i<arrayList.size();i++)
                {
                    Double lat=Double.parseDouble(finallist.get(i).get(1));
                    Double lon=Double.parseDouble(finallist.get(i).get(2));
                    LatLng latLng=new LatLng(lat,lon);
                    MarkerOptions markerBus2 = new MarkerOptions().position(
                            latLng).title(finallist.get(i).get(0));

                    markerBus2.icon(BitmapDescriptorFactory.fromResource(resID)
                    );
                    CameraPosition cameraPosition2 = new CameraPosition.Builder()
                            .target(latLng).zoom(16).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition2));

                    mMap.addMarker(markerBus2);
                }

                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(new LatLng(lat,lon)).zoom(14).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));



                final ArrayList<String> listarr = new ArrayList<String>();
                for (int i = 0; i < finallist.size(); ++i) {
                    listarr.add(finallist.get(i).get(0));
                }
                final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                        R.layout.list_view_style_text, listarr);
                placeListView.setAdapter(adapter);
                listLayout.setGravity(Gravity.RIGHT|Gravity.CENTER);
                listLayout.getLayoutParams().height=700;

                testButtonPlace.setVisibility(View.INVISIBLE);

                placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Double latd = null,lngd=null;
                        Log.d("placelistitem","clicked");
                        for (int i=0;i<finallist.size();i++)
                        {
                            if(parent.getItemAtPosition(position).toString().equals(finallist.get(i).get(0)))
                            {
                                Log.d("placelistitem","clicked1");
                                latd=Double.parseDouble(finallist.get(i).get(1));
                                lngd=Double.parseDouble(finallist.get(i).get(2));
                            }

                        }
                        List<Address> addressesDest = null;
                        List<Address> addressesSource = null;
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        try {
                            addressesDest = geocoder.getFromLocation(latd,lngd, 1);
                            addressesSource = geocoder.getFromLocation(lat, lon, 1);
                            Log.d("placelistitem","clicked2");
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("placelistitem: ",addressesDest.toString()+"\n"+addressesSource.toString());

                        try {
                            final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(NavDrawerPlaceSearchActivity.this, R.style.myDialog)).create();

                            String messageString="Do you want directions to this place?";
                            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select","English").equals("বাংলা")) {
                                messageString="আপনি কি এখানে যাওয়ার রাস্তা দেখতে ইচ্ছুক?";
                            }

                            alertDialog.setTitle(messageString);
                            alertDialog.setMessage(parent.getItemAtPosition(position).toString());

                                    final List<Address> finalAddressesSource = addressesSource;
                            final List<Address> finalAddressesDest = addressesDest;
                            final Double finalLatd = latd;
                            final Double finalLngd = lngd;
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            NavDrawerMapWithDirectionActivity.source = finalAddressesSource.get(0).getAddressLine(0);
//                                            NavDrawerMapWithDirectionActivity.source2 = finalAddressesDest.get(0).getAddressLine(0);
//                                            NavDrawerMapWithDirectionActivity.s1 = new LatLng(lat, lon);
//                                            NavDrawerMapWithDirectionActivity.s2 = new LatLng(finalLatd, finalLngd);
                                            mAutocompleteViewSource.setText("");
//                                        distanceText.setText("0");
//                                        seekBar.setProgress(0);
                                            picker.setInitPosition(0);
                                            NavDrawerDirectionFinal.origin = new LatLng(lat,lon);
                                            NavDrawerDirectionFinal.destination = new LatLng(finalLatd,finalLngd);
                                            NavDrawerDirectionFinal.originName = finalAddressesSource.get(0).getAddressLine(0);
                                            NavDrawerDirectionFinal.destinationName = finalAddressesDest.get(0).getAddressLine(0);

                                            Intent intent = new Intent(NavDrawerPlaceSearchActivity.this, NavDrawerDirectionFinal.class);
                                            startActivity(intent);

                                        }
                                    }
                            );
                            alertDialog.show();
                        }catch(Exception e)
                        {
                            Log.d("placelistitem ",e.toString());
                        }

                    }
                });


            }

            Log.d("PLACELIST","some"+placeListString);
            progressDialog.dismiss();
        }



        private String makeUrl(double latitude, double longitude,String place,Double distance) {
            StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        StringBuilder urlString = new StringBuilder("https://www.google.com");

            if (place.equals("")) {
                urlString.append("location=");
                urlString.append(Double.toString(latitude));
                urlString.append(",");
                urlString.append(Double.toString(longitude));
                urlString.append("&radius="+distance);
                //   urlString.append("&types="+place);
                urlString.append("&key=" + APP_ID+"&sensor=true");
            } else {
                urlString.append("location=");
                urlString.append(Double.toString(latitude));
                urlString.append(",");
                urlString.append(Double.toString(longitude));
                urlString.append("&radius="+distance);
                urlString.append("&types="+place);
                urlString.append("&key=" + APP_ID+"&sensor=true");
            }

            return urlString.toString();
        }
    }




    //Expand Layout







    //PlaceSearchSuggestion
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
//            mAutocompleteViewSource.setEnabled(true);
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
//            mAutocompleteViewDest.setText(parent.toString());
//            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


//                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
//                        Toast.LENGTH_SHORT).show();


        }

    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final com.google.android.gms.location.places.Place place = places.get(0);

//          android.location.Address address= (Address) place.getAddress();
            String fullName = place.getName() + "";
            LatLng latLng = place.getLatLng();
            positionLatlng=place.getLatLng();
            lat=positionLatlng.latitude;
            lon=positionLatlng.longitude;

//            sample2.setText(fullName);
            places.release();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        String errorText="Could not connect to Google API Client: Error";
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select","English").equals("বাংলা")) {
            errorText="গুগলের সাথে সঠিকভাবে সংযোগ স্থাপন করা সম্ভব হচ্ছে না।\nকারণঃ ";
        }

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                errorText + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }




    public static void expand(final View v,int state) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int targetHeight=(int)windowHeight/2;
        if(state==1)
        targetHeight = (int)windowHeight/4;
        else if(state==2)
        {
            targetHeight = (int)windowHeight/3;
        }

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        final int finalTargetHeight = targetHeight;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? finalTargetHeight
                        : (int)(finalTargetHeight * interpolatedTime);
//                v.getLayoutParams().width=(int)(windowWidth-windowWidth/4);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.INVISIBLE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
//                    v.getLayoutParams().width=(int)(windowWidth-windowWidth/4);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }



    public void makeAlertBox(String fullInfo)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(NavDrawerPlaceSearchActivity.this, R.style.myDialog))
                .create();
            String titleString=getString(R.string.errro_title);
        String okString=getString(R.string.ok_btn);

        alertDialog.setTitle(R.string.direction_prompt_title);
        alertDialog.setIcon(R.drawable.ic_error);
        StringBuilder message=new StringBuilder("");
        message.append(fullInfo);
//        message.append("\nContact Number: "+fullPlaceDetails.get(i).get(6));
//        message.append("\nDistance from my position: "+fullPlaceDetails.get(i).get(5)+"meters");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, okString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        alertDialog.setMessage(message);
        alertDialog.show();

    }















}
