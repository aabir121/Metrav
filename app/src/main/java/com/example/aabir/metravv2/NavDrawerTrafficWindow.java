package com.example.aabir.metravv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aabir.metravv2.Utility.CustomAdapter;
import com.example.aabir.metravv2.Utility.ShareLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerTrafficWindow extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText trafficFilterText;
    boolean showProgress=false;
    ListView trafficListView;
    ArrayAdapter filterAdapter;
    ArrayList<ArrayList<String>> finallist=new ArrayList<>();
    CustomAdapter adapter;
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


        setContentView(R.layout.activity_nav_drawer_traffic_window);





        trafficListView=(ListView)findViewById(R.id.trafficListView);
        trafficFilterText=(EditText)findViewById(R.id.filterTrafficText);
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


        new BackgroundWorker(NavDrawerTrafficWindow.this).execute();

        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
//                adapter.notifyDataSetChanged();
                finallist.clear();
//                trafficListView.setAdapter(null);
                new BackgroundWorker(NavDrawerTrafficWindow.this).execute();

                handler.postDelayed( this, 30000 );
            }
        }, 30000 );

        trafficFilterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                NavDrawerTrafficWindow.this.adapter.
                NavDrawerTrafficWindow.this.filterAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer_traffic_window, menu);
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
            // Handle the camera action
            finish();
            Intent intent=new Intent(NavDrawerTrafficWindow.this,NavDrawerMainMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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



        else if (id == R.id.find_place_screen) {
            Intent intent=new Intent(NavDrawerTrafficWindow.this,NavDrawerPlaceSearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent=new Intent(NavDrawerTrafficWindow.this,SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_traffic_window) {

        } else if (id == R.id.nav_logout) {
            SharedPreferences settings = getSharedPreferences("Metrav", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("LoggedIn",0);
            editor.commit();
            Intent intent=new Intent(NavDrawerTrafficWindow.this,AppLogin.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class BackgroundWorker extends AsyncTask<String,Void,String> {
        Context context;
        AlertDialog alertDialog;
        ProgressDialog progressDialog;


        BackgroundWorker (Context ctx) {
            context = ctx;
        }
        @Override
        protected String doInBackground(String... params) {
            String traffic_url = "http://techlayerbd.com/metrav/trafficupdate.php";
            try {
                URL url = new URL(traffic_url);
                Long tsLong = System.currentTimeMillis()/1000;
                String timestamp = tsLong.toString();
                Log.d("TIMESTAMP",tsLong.toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("timestamp","UTF-8")+"="+URLEncoder.encode(timestamp,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                StringBuilder data=new StringBuilder("");
                JSONArray array = null;

                try {
                    array = new JSONArray(result);


                    for (int i = 0; i < array.length(); i++) {
                        try {
                            ArrayList<String> traffList=new ArrayList<>();
                            JSONObject main = (JSONObject) array.get(i);
                            Double trafficLat=Double.parseDouble(main.getString("lat"));
                            Double trafficLon=Double.parseDouble(main.getString("lon"));
                            int state=main.getInt("state");
                            long timestampfromdb=Long.parseLong(main.getString("time"));
                            long diffTimeStamp=(tsLong-timestampfromdb);
                            long diffSeconds= compareTwoTimeStamps(tsLong,timestampfromdb);
                            String formattedTimeStamp = new java.text.SimpleDateFormat("HH:mm").format(timestampfromdb*1000);
                            Log.d("TIMESTAMP","Time"+formattedTimeStamp);
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(trafficLat, trafficLon, 1);
                            traffList.add(addresses.get(0).getAddressLine(0)+","+addresses.get(0).getAddressLine(1));
                            traffList.add(trafficLat+"");
                            traffList.add(trafficLon+"");
                            traffList.add(state+"");
//                            if(diffSeconds/60>=60)
//                            {
//                                long hours,mins;
//                                hours=diffSeconds/(60*60);
//                                mins=diffSeconds %(60*60);
//                                traffList.add(hours+"hour and "+mins+" minutes ago");
//                            }
                             if(diffSeconds>=60)
                            {
                                long mins;
                                mins=diffSeconds/60;
                                traffList.add(mins+" minutes ago");
                            }

                            else
                            traffList.add(diffSeconds+" seconds ago");
                            finallist.add(traffList);


                        }
                        catch (Exception e)
                        {
                            Toast.makeText(NavDrawerTrafficWindow.this,e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(NavDrawerTrafficWindow.this,e.toString(),Toast.LENGTH_LONG);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(NavDrawerTrafficWindow.this,e.toString(),Toast.LENGTH_LONG);
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            progressDialog=new ProgressDialog(context);
            progressDialog.setMessage("Getting Traffic Updates....");
            progressDialog.setCancelable(false);
//            if(!showProgress)
//            if(!progressDialog.isShowing())
//            progressDialog.show();
            alertDialog.setTitle("Traffic Status");
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.setMessage(result);



            try{
                final ArrayList<String> listarr = new ArrayList<String>();
                for (int i = 0; i < finallist.size(); ++i) {

                    StringBuilder trafficListBuilder=new StringBuilder("");
                    trafficListBuilder.append(finallist.get(i).get(0)+"\n");
                    int state=Integer.parseInt(finallist.get(i).get(3));
                    if(state==1)
                    {
                        trafficListBuilder.append("Low Traffic"+"\n");
                    }
                    else if(state==2)
                    {
                        trafficListBuilder.append("Medium Traffic"+"\n");
                    }
                    else if(state==3)
                    {
                        trafficListBuilder.append("Heavy Traffic"+"\n");
                    }
                    trafficListBuilder.append(""+finallist.get(i).get(4));
                    listarr.add(trafficListBuilder.toString());
                }
                filterAdapter = new ArrayAdapter(getApplicationContext(),
                        R.layout.list_view_style_text, listarr);
                int[] drawableIds = {R.drawable.ic_traffic_green,R.drawable.ic_traffic_yellow,R.drawable.ic_traffic_red};
                adapter = new CustomAdapter(NavDrawerTrafficWindow.this,  listarr, drawableIds,finallist);

//            final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
//                    R.layout.list_view_style_text, listarr);
                trafficListView.setAdapter(adapter);

            }catch(Exception e)
            {
                Toast.makeText(NavDrawerTrafficWindow.this,e.toString(),Toast.LENGTH_LONG);
            }



Log.d("UPDATED","update");

//            trafficListView.invalidate();
//            alertDialog.setMessage(data.toString());
            progressDialog.dismiss();
            showProgress=true;
//            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public static long compareTwoTimeStamps(long currentTime, long oldTime)
    {
//        long milliseconds1 = oldTime.getTime();
//        long milliseconds2 = currentTime.getTime();

        long diff = currentTime-oldTime;
        long diffSeconds = diff;
        long diffMinutes = diff / (60);
        long diffHours = diff / (60 * 60 );
        long diffDays = diff / (24 * 60 * 60 );

        return diffSeconds;
    }

}
