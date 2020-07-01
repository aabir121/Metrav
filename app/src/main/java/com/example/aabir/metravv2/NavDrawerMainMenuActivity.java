package com.example.aabir.metravv2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aabir.metravv2.Utility.DownloadImageTask;
import com.example.aabir.metravv2.Utility.PlaceAutocompleteAdapter;
import com.example.aabir.metravv2.Utility.ShareLocation;
import com.example.aabir.metravv2.Utility.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerMainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener,GoogleMap.OnMapLongClickListener
{

    private int trafficState = 1;

    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    static ProgressDialog progressDialog;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteViewSource;
    private AutoCompleteTextView mAutocompleteViewDest;
    private Button getDirecttions;
    public LatLng s1, s2;
    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(
            new LatLng(23.549686, 90.056174), new LatLng(24.018418, 90.514853));
    public String source, dest;

    FloatingActionButton getCurrentLocationButton;
    //    Button findPlacesButton;
    private static final int PERMISSION_REQUEST_CODE = 1;

    Double myLat, myLon;
    MarkerOptions markerMe;
    MarkerOptions markerPlaceSave;

    Button greenTraffic, yellowTraffic, redTraffic, submitTrafficBtn;

    //to check if we really want to exit
    private Boolean exit = false;


    Bitmap bitProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        //getSavedPlaces();

        setContentView(R.layout.activity_nav_drawer_main_menu);


        checkLocationService(getApplicationContext());


        progressDialog = new ProgressDialog(NavDrawerMainMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting your directions....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            createPermissions();
        }


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FloatingActionButton sideBarToggle = (FloatingActionButton) findViewById(R.id.sideBarToggleBtn);
        sideBarToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        FloatingActionButton trafficDialogBtn = (FloatingActionButton) findViewById(R.id.traffic_update_button);
        trafficDialogBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showTrafficDialog();
            }
        });


        View hView = navigationView.getHeaderView(0);
        CircleImageView nav_user_image = (CircleImageView) hView.findViewById(R.id.imageViewHeader);


        String value="";
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            value = extras.getString("profile_id");
            String image_url="https://graph.facebook.com/" + value + "/picture?type=large";
            new DownloadImageTask(nav_user_image).execute(image_url);
        }
        else if(value.equals(""))
        {
            SharedPreferences settings = getSharedPreferences("Metrav", 0);
            String imgstr = settings.getString("Image", "");
            byte[] base = Base64.decode(imgstr, Base64.DEFAULT);
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitProfile = BitmapFactory.decodeByteArray(base, 0, base.length, options); //Convert bytearray to bitmap
            if (bitProfile != null)
                nav_user_image.setImageBitmap(bitProfile);
            else
                Toast.makeText(getApplicationContext(), "bitmap is null", Toast.LENGTH_LONG).show();
        }

        nav_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });


        new BackgroundWorker(NavDrawerMainMenuActivity.this).execute();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteViewSource = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places_source);
        mAutocompleteViewDest = (AutoCompleteTextView) findViewById(R.id.autocomplete_places_dest);
        getDirecttions = (Button) findViewById(R.id.getDirectionButton1);
        getCurrentLocationButton = (FloatingActionButton) findViewById(R.id.current_location_search_button);
        mAutocompleteViewSource.setText("");
        mAutocompleteViewDest.setText("");

        progressDialog.dismiss();



        // Register a listener that receives callbacks when a suggestion has been selected

        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewDest.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
//        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
//        mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_BANGLADESH,
                null);
        mAutocompleteViewSource.setAdapter(mAdapter);
        mAutocompleteViewDest.setAdapter(mAdapter);

        getCurrentLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            // instantiate the location manager, note you will need to request permissions in your manifest
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // get the last know location from your location manager.
            boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            try {
                if (permissionGranted)
                {
                    // {Some Code}
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    // now get the lat/lon from the location and do something with it.
                    if (location != null)
                    {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        s1 = latLng;
                        String ad = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getAddressLine(2);

                        MarkerOptions markerMe = new MarkerOptions().position(
                                latLng).title("You are here").snippet(addresses.get(0).getAddressLine(0));
                         markerMe.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place)
                                                                        );
                         CameraPosition cameraPosition2 = new CameraPosition.Builder()
                                                                                .target(latLng).zoom(16).build();

                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition2));
                        mMap.addMarker(markerMe);
                        mAutocompleteViewSource.setText(ad);
                    }
                }
                else
                {
                    ActivityCompat.requestPermissions(NavDrawerMainMenuActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                }
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_LONG).show();
            }
            }
        });


        getDirecttions = (Button) findViewById(R.id.getDirectionButton1);
        getDirecttions.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mAutocompleteViewSource.getText().toString().isEmpty() && !mAutocompleteViewDest.getText().toString().isEmpty())
                {
                    if(haveNetworkConnection())
                    {
                        mAutocompleteViewSource.setError(null);
                        mAutocompleteViewDest.setError(null);
                        new BackgroundTask().execute();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please check your internet connection and try again.",Toast.LENGTH_LONG).show();
                    }

                }
                else if (mAutocompleteViewSource.getText().toString().isEmpty())
                {
                    mAutocompleteViewSource.setError("Please enter a source address.");
                }
                else if (mAutocompleteViewDest.getText().toString().isEmpty())
                {
                    mAutocompleteViewDest.setError("Please enter a destination address.");
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (exit)
            {
                finishAffinity(); // finish activity

            }
            else
            {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



         if(id==R.id.nav_emergency_police)
        {
            Intent intent=new Intent(getApplicationContext(),NavDrawerEmergencyContacts.class);
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

        else if (id == R.id.find_place_screen)
        {
            Log.d("ERRORR", "CLICKED");
            try
            {
                Intent intent = new Intent(NavDrawerMainMenuActivity.this, NavDrawerPlaceSearchActivity.class);
                intent.putExtra("activity", "main");
                startActivity(intent);
            }
            catch (Exception e)
            {
                Log.d("ERRORR", e.toString());
            }
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
        else if (id == R.id.nav_settings)
        {
            Intent i = new Intent(NavDrawerMainMenuActivity.this, SettingsActivity.class);
            i.putExtra("activity", "main");
            startActivity(i);
        }
        else if (id == R.id.nav_traffic_window)
        {
            Intent intent = new Intent(NavDrawerMainMenuActivity.this, NavDrawerTrafficWindow.class);
            intent.putExtra("activity", "main");
            startActivity(intent);

        }
        else if (id == R.id.nav_logout)
        {
            SharedPreferences settings = getSharedPreferences("Metrav", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("LoggedIn", 0);
            editor.apply();
            Intent intent = new Intent(NavDrawerMainMenuActivity.this, AppLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
//            mAutocompleteViewSource.setEnabled(true);
            final AutocompletePrediction item = mAdapter.getItem(position);
            try {
                final String placeId = item.getPlaceId();
            //            mAutocompleteViewDest.setText(parent.toString());
//            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }catch(Exception e)
            {
                e.printStackTrace();
            }


//                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
//                        Toast.LENGTH_SHORT).show();


        }

    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>()
    {
        @Override
        public void onResult(PlaceBuffer places)
        {
            if (!places.getStatus().isSuccess())
            {
                // Request did not complete successfully
//                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final com.google.android.gms.location.places.Place place = places.get(0);

//          android.location.Address address= (Address) place.getAddress();
            String fullName = place.getName() + "";
            if (mAutocompleteViewSource.getText().toString().matches("(.*)" + fullName + "(.*)"))
//            Toast.makeText(getApplicationContext(),latLng.toString(),Toast.LENGTH_LONG);
            {
                s1 = place.getLatLng();
                source = "" + fullName;
                placeMarkers(s1, source, "Source");
            }
            else if (mAutocompleteViewDest.getText().toString().matches("(.*)" + fullName + "(.*)"))
            {
                s2 = place.getLatLng();
                dest = "" + fullName;
                placeMarkers(s2, dest, "Destination");
            }

//            sample2.setText(fullName);
            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

//        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
//                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mMap = googleMap;
        String mapType=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("maptype","1");

        try {
            mMap.setMapType(Integer.parseInt(mapType));
        }catch(Exception e)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

        mMap.setOnMapLongClickListener(this);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0, 0, 20, 50);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        try
        {
            if (permissionGranted)
            {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.

                if (location != null)
                {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    myLat = location.getLatitude();
                    myLon = location.getLongitude();

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    s1=latLng;
                    String ad = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getAddressLine(2);


                    markerMe = new MarkerOptions().position(
                            latLng).title("You are here").snippet(addresses.get(0).getAddressLine(0));

                    markerMe.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place)
                    );
//
                    CameraPosition cameraPosition2 = new CameraPosition.Builder()
                            .target(latLng).zoom(14).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition2));
//
                    mMap.addMarker(markerMe);
                    mAutocompleteViewSource.setText(ad);
                }
                else
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, new android.location.LocationListener()
                    {
                        @Override
                        public void onLocationChanged(Location location)
                        {
                            myLat = location.getLatitude();
                            myLon = location.getLongitude();
                            Log.e("TAG", myLat + "," + myLon);
                            if (progressDialog.isShowing())
                            {
                                mapUpdateNew();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras)
                        {

                        }

                        @Override
                        public void onProviderEnabled(String provider)
                        {

                        }

                        @Override
                        public void onProviderDisabled(String provider)
                        {

                        }
                    });

                }

            }
            else
            {
                ActivityCompat.requestPermissions(NavDrawerMainMenuActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onLocationChanged(Location location)
    {
        myLat = location.getLatitude();
        myLon = location.getLongitude();
        if (progressDialog.isShowing())
        {
            mapUpdateNew();
            progressDialog.dismiss();
        }
    }

    public void mapUpdateNew()
    {
        LatLng latLng = new LatLng(myLat, myLon);
        markerMe = new MarkerOptions().position(
                latLng).title("You are here");

        markerMe.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                .target(latLng).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition2));
        mMap.addMarker(markerMe);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            markerPlaceSave = new MarkerOptions().position(
                    latLng).snippet(addresses.get(0).getAddressLine(0));

            markerPlaceSave.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place));
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_place_save, null);
            alertDialogBuilder.setView(dialogView);

            final EditText ed=(EditText)dialogView.findViewById(R.id.place_save_et);
            final LatLng lt=latLng;

            SharedPreferences sharedPreferences=getSharedPreferences("Metrav", 0);
            final String uid=sharedPreferences.getString("UserID","N/A");

            alertDialogBuilder.setMessage("Do you want to save this place?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String name=ed.getText().toString();
                    Log.d("placename","asd\t"+name);

                    String login_url = "http://techlayerbd.com/metrav/placeinsert.php";

                    StringRequest stringRequest=new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("Place Inserted!")) {
//                    alert.dismiss();
                                Toast.makeText(getApplicationContext(),"Inserted!",Toast.LENGTH_LONG).show();

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),"Something went wrong. Please retry.",Toast.LENGTH_LONG).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params=new HashMap<String, String>();
                            params.put("latitude",lt.latitude+"");
                            params.put("longitude",lt.longitude+"");
                            params.put("name",name);
                            params.put("uid",uid);
                            return params;
                        }
                    };
                    VolleySingleton.getmInstace(NavDrawerMainMenuActivity.this).addToRequest(stringRequest);

                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alert=alertDialogBuilder.create();
            alert.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    class BackgroundTask extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected Void doInBackground(Void... voids)
        {
            /* NavDrawerMapWithDirectionActivity.source = source;
             NavDrawerMapWithDirectionActivity.source2 = dest;
             NavDrawerMapWithDirectionActivity.s1 = s1;
             NavDrawerMapWithDirectionActivity.s2 = s2;
            */
            NavDrawerDirectionFinal.origin = s1;
            NavDrawerDirectionFinal.destination = s2;
            NavDrawerDirectionFinal.originName = source;
            NavDrawerDirectionFinal.destinationName = dest;


            return null;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            progressDialog.dismiss();
            Intent intent = new Intent(NavDrawerMainMenuActivity.this, NavDrawerDirectionFinal.class);
            startActivity(intent);
            super.onPostExecute(aVoid);
        }
    }

    public class BackgroundWorker extends AsyncTask<String, Void, String>
    {
        Context context;
        AlertDialog alertDialog;
        ProgressDialog progressDialog;


        BackgroundWorker(Context ctx)
        {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params)
        {
            String traffic_url = "http://techlayerbd.com/metrav/trafficupdate.php";
            try
            {
                URL url = new URL(traffic_url);
                Long tsLong = System.currentTimeMillis() / 1000;
                String timestamp = tsLong.toString();
                Log.d("TIMESTAMP", tsLong.toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("timestamp", "UTF-8") + "=" + URLEncoder.encode(timestamp, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line ="";
                while ((line = bufferedReader.readLine()) != null)
                {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }


            return null;
        }

        @Override
        protected void onPreExecute()
        {
            alertDialog = new AlertDialog.Builder(context).create();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Getting Traffic Updates....");
            progressDialog.setCancelable(true);
            progressDialog.show();
            alertDialog.setTitle(R.string.traffic_status_title);
        }

        @Override
        protected void onPostExecute(String result)
        {
            alertDialog.setMessage(result);
            progressDialog.dismiss();
            mMap.clear();


            StringBuilder data = new StringBuilder("");
            JSONArray array = null;

            try
            {
                array = new JSONArray(result);
                for (int i = 0; i < array.length(); i++)
                {
                    try
                    {

                        JSONObject main = (JSONObject) array.get(i);
                        Double trafficLat = Double.parseDouble(main.getString("lat"));
                        Double trafficLon = Double.parseDouble(main.getString("lon"));
                        int state = main.getInt("state");
                        data.append("Latitude: " + main.getString("lat") + "\n");
                        data.append("Longitude: " + main.getString("lon") + "\n");
                        data.append("State: " + main.getString("state") + "\n\n");

                        LatLng latLng = new LatLng(trafficLat, trafficLon);
                        MarkerOptions trafficMarker = new MarkerOptions().position(
                                latLng);

                        if (state == 1)
                            trafficMarker.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_traffic_green));
                        else if (state == 2)
                            trafficMarker.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_traffic_yellow));
                        else if (state == 3)
                            trafficMarker.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_traffic_red));

                        mMap.addMarker(markerMe);

                        mMap.addMarker(trafficMarker);


                    }
                    catch (Exception e)
                    {
                        Toast.makeText(NavDrawerMainMenuActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(NavDrawerMainMenuActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }


            alertDialog.setMessage(data.toString());

//            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }

    public void showTrafficDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popupinfo, null);
        dialogBuilder.setView(dialogView);

        greenTraffic = (Button) dialogView.findViewById(R.id.greenTrafficBtn);
        yellowTraffic = (Button) dialogView.findViewById(R.id.yellowTrafficBtn);
        redTraffic = (Button) dialogView.findViewById(R.id.redTrafficBtn);
        submitTrafficBtn = (Button) dialogView.findViewById(R.id.submitTrafficBtn);

        greenTraffic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                redTraffic.setBackgroundResource(R.drawable.custom_traffic_button_back);
                yellowTraffic.setBackgroundResource(R.drawable.custom_traffic_button_back);
//                yellowTraffic.setBackgroundColor(Color.WHITE);
//                redTraffic.setBackgroundColor(Color.WHITE);
                greenTraffic.setBackgroundResource(R.drawable.custom_traffic_button_selected_back);
                trafficState = 1;
            }
        });
        yellowTraffic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                greenTraffic.setBackgroundColor(Color.WHITE);
//                redTraffic.setBackgroundColor(Color.WHITE);
                greenTraffic.setBackgroundResource(R.drawable.custom_traffic_button_back);
                redTraffic.setBackgroundResource(R.drawable.custom_traffic_button_back);
                yellowTraffic.setBackgroundResource(R.drawable.custom_traffic_button_selected_back);
                trafficState = 2;
            }
        });
        redTraffic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                yellowTraffic.setBackgroundColor(Color.WHITE);
                yellowTraffic.setBackgroundResource(R.drawable.custom_traffic_button_back);
//                greenTraffic.setBackgroundColor(Color.WHITE);
                greenTraffic.setBackgroundResource(R.drawable.custom_traffic_button_back);
                redTraffic.setBackgroundResource(R.drawable.custom_traffic_button_selected_back);
                trafficState = 3;

            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();

        submitTrafficBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new TrafficUpdateBackground(NavDrawerMainMenuActivity.this).execute("" + trafficState);
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    public class TrafficUpdateBackground extends AsyncTask<String, Void, String>
    {
        Context context;
        AlertDialog alertDialog;
        ProgressDialog progressDialog;


        TrafficUpdateBackground(Context ctx)
        {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params)
        {
            String traffic_url = "http://techlayerbd.com/metrav/trafficinsert.php";
            try
            {
                URL url = new URL(traffic_url);
                String state = params[0];
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(myLat.toString(), "UTF-8") + "&"
                        + URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(myLon.toString(), "UTF-8") + "&"
                        + URLEncoder.encode("state", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute()
        {
            alertDialog = new AlertDialog.Builder(context).create();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Updating Traffic....");
            progressDialog.setCancelable(false);
//            progressDialog.show();
            alertDialog.setTitle("Traffic Status");
        }

        @Override
        protected void onPostExecute(String result)
        {
            alertDialog.setMessage(result);
            progressDialog.dismiss();
            if (result.equals(R.string.traffic_status_msg))
            {
                alertDialog.setMessage(result);
                Toast.makeText(getApplicationContext(), "Traffic Successfully updated.", Toast.LENGTH_LONG).show();
            }
            else
            {
                alertDialog.setMessage(result);
                alertDialog.setIcon(R.drawable.ic_error);
                Toast.makeText(getApplicationContext(), "Error Contacting to server", Toast.LENGTH_LONG).show();
            }

//            StringBuilder data=new StringBuilder(result+"\n\n");

//            alertDialog.setMessage(data.toString());
            new BackgroundWorker(NavDrawerMainMenuActivity.this).execute();
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }


    public void placeMarkers(LatLng latLng, String adrs, String type)
    {


        MarkerOptions randomMarker = new MarkerOptions().position(
                latLng).title(adrs).snippet(type);

        randomMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place)
        );

        mMap.addMarker(randomMarker);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createPermissions()
    {
        String permission = Manifest.permission.ACCESS_COARSE_LOCATION;
        if (ContextCompat.checkSelfPermission(NavDrawerMainMenuActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(NavDrawerMainMenuActivity.this, permission))
            {
                ActivityCompat.requestPermissions(NavDrawerMainMenuActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);

//                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAutocompleteViewSource.setText("");
        mAutocompleteViewDest.setText("");
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void checkLocationService(final Context context)
    {
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            Log.d("locationstatus","false");
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    public void showUpdateDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_password_dialog, null);
        dialogBuilder.setView(dialogView);

        final SharedPreferences settings = getSharedPreferences("Metrav", 0);

        CircleImageView iv=(CircleImageView)dialogView.findViewById(R.id.update_profile_pic);
        iv.setImageBitmap(bitProfile);

        TextView tv=(TextView)dialogView.findViewById(R.id.update_title_text);
        tv.setText(settings.getString("Username","Title")+"\n"+settings.getString("UserEmail","Email"));

        final AlertDialog alert=dialogBuilder.create();
        Button submit=(Button)dialogView.findViewById(R.id.update_submit_btn);
        final EditText pass=(EditText)dialogView.findViewById(R.id.update_pass_text);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(settings.getString("Username","Title"),pass.getText().toString());
                alert.dismiss();

            }
        });




//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

    }

    public void updateProfile(final String name,final String pass)
    {
        String login_url = "http://techlayerbd.com/metrav/update.php";

        StringRequest stringRequest=new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Successfully Updated!")) {
//                    alert.dismiss();
                    Toast.makeText(getApplicationContext(),"Updated!",Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Something went wrong. Please retry.",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("username",name);
                params.put("password",pass);
                return params;
            }
        };
        VolleySingleton.getmInstace(NavDrawerMainMenuActivity.this).addToRequest(stringRequest);

    }

    public List getSavedPlaces(){
        String login_url = "http://techlayerbd.com/metrav/placeget.php";
        SharedPreferences sharedPreferences=getSharedPreferences("Metrav", 0);
        final String uid=sharedPreferences.getString("UserID","N/A");

        StringRequest stringRequest=new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("placesave","response: "+response);
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject object=jsonArray.getJSONObject(i);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Something went wrong. Please retry.",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("uid",uid);
                return params;
            }
        };
        VolleySingleton.getmInstace(NavDrawerMainMenuActivity.this).addToRequest(stringRequest);


        return null;
    }

}
