package com.example.aabir.metravv2;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.model.Line;
import com.example.aabir.metravv2.Utility.Emergency;
import com.example.aabir.metravv2.Utility.EmergencyGroup;
import com.example.aabir.metravv2.Utility.EmergencyListAdapter;
import com.example.aabir.metravv2.Utility.ShareLocation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.BitmapDescriptor;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abir on 4/6/2017.
 */

public class NavDrawerEmergencyContacts extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private SearchView search;
    private EmergencyListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<EmergencyGroup> emergencyGroupArrayList = new ArrayList<EmergencyGroup>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String languageToLoad;
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select", "English").equals("বাংলা")) {
            languageToLoad = "bn"; // your language
            Log.e("Nav", "bangla");
        } else {
            languageToLoad = "en";
            Log.e("Nav", "English");
        }

        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_nav_drawer_emergency_police_contacts);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FloatingActionButton sideBarToggle = (FloatingActionButton) findViewById(R.id.sideBarToggleBtn);
        sideBarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        View hView = navigationView.getHeaderView(0);
        CircleImageView nav_user_image = (CircleImageView) hView.findViewById(R.id.imageViewHeader);


        SharedPreferences settings = getSharedPreferences("Metrav", 0);
        String imgstr = settings.getString("Image", "");
        byte[] base = Base64.decode(imgstr, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bit = BitmapFactory.decodeByteArray(base, 0, base.length, options); //Convert bytearray to bitmap
        if (bit != null)
            nav_user_image.setImageBitmap(bit);
        else
            Toast.makeText(getApplicationContext(), "bitmap is null", Toast.LENGTH_LONG).show();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) findViewById(R.id.filterSearchEmergencyText);
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);

        //display the list
        displayList();

        myList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String msg =
                        emergencyGroupArrayList.get(groupPosition).getEmergencyArrayList().get(childPosition).getNumbers();
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                Log.d("EMERGENCYROW", "Child Click");


                AlertDialog.Builder alertDialog=new AlertDialog.Builder(new ContextThemeWrapper(NavDrawerEmergencyContacts.this,R.style.myDialog));

                LayoutInflater layoutInflater=NavDrawerEmergencyContacts.this.getLayoutInflater();
                View popupView=layoutInflater.inflate(R.layout.emergency_popup,null);
                alertDialog.setView(popupView);

                TextView title=(TextView)popupView.findViewById(R.id.popup_emergency_title);
                title.setText(emergencyGroupArrayList.get(groupPosition).getName());

                LinearLayout popupLinearLayout=(LinearLayout) popupView.findViewById(R.id.popup_emergency_info_layout);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(30,20,30,0);

                LinearLayout.LayoutParams paramsChild=new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsChild.setMargins(30,0,30,0);

                final String[] parse=msg.split(",");
                for(int i=0;i<parse.length;i++)
                {

                    LinearLayout layout=new LinearLayout(NavDrawerEmergencyContacts.this);
                    layout.setLayoutParams(params);
                    layout.setOrientation(LinearLayout.HORIZONTAL);

                    Button tv=new Button(NavDrawerEmergencyContacts.this);
                    tv.setLayoutParams(paramsChild);
                    tv.setText(parse[i]);
                    tv.setBackground(getResources().getDrawable(R.drawable.custom_button_border_white));
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    tv.setPadding(20,0,20,0);

                    if(parse[i].length()<11)
                    {
                        parse[i]="02"+parse[i];
                    }
                    Drawable top = getResources().getDrawable(R.drawable.ic_phone);
                    tv.setCompoundDrawablesWithIntrinsicBounds(null, null , top, null);
                    final String num=parse[i];
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri number = Uri.parse("tel:" + num);
                            Intent callIntent = new Intent(Intent.ACTION_DIAL,number);
                            startActivity(callIntent);
                        }
                    });


                    layout.addView(tv);

//                    ImageButton call=new ImageButton(NavDrawerEmergencyContacts.this);
//                    call.setLayoutParams(paramsChild);
//                    call.setImageResource(R.drawable.ic_phone);
//
//                    layout.addView(call);
                    popupLinearLayout.addView(layout);

//                    popupLinearLayout.addView(tv);
                }

                AlertDialog alert=alertDialog.create();
                alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();

//                alertDialog.show();

//                Uri number = Uri.parse("tel:" + msg);
//                Intent callIntent = new Intent(Intent.ACTION_VOICE_COMMAND);
////                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
//                startActivity(callIntent);
                return false;
            }
        });
    }


    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            myList.expandGroup(i);
        }
    }

    //method to expand all groups
    private void displayList() {

        //display the list
        loadSomeData();

        //get reference to the ExpandableListView
        myList = (ExpandableListView) findViewById(R.id.expandableListView);
        //create the adapter by passing your ArrayList data
        listAdapter = new EmergencyListAdapter(NavDrawerEmergencyContacts.this, emergencyGroupArrayList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.get_direction_screen) {
            // Handle the camera action
            Intent intent = new Intent(getApplicationContext(), NavDrawerMainMenuActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_share_location) {
            String title = "Location Shared from Metrav!";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, title + "\n" + new ShareLocation(getApplicationContext(), getParent()).getPosition());
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


        else if (id == R.id.nav_bus_offline) {

            Intent intent = new Intent(getApplicationContext(), NavDrawerBusOffline.class);
            startActivity(intent);
        } else if (id == R.id.find_place_screen) {
            Log.d("ERRORR", "CLICKED");
            try {
                Intent intent = new Intent(getApplicationContext(), NavDrawerPlaceSearchActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.d("ERRORR", e.toString());
            }
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_traffic_window) {
            Intent intent = new Intent(getApplicationContext(), NavDrawerTrafficWindow.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            SharedPreferences settings = getSharedPreferences("Metrav", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("LoggedIn", 0);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), AppLogin.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadSomeData() {
        ArrayList<Emergency> emergencyArrayList = new ArrayList<>();
        Emergency emergency = new Emergency(" Police Commissioner ", " 9331555 , 01711538313");
        emergencyArrayList.add(emergency);
        emergency = new Emergency(" Additional Police Commissioner ", " 9343455 , 01713373101");
        emergencyArrayList.add(emergency);
        emergency = new Emergency(" Joint Commissioner (Traffic) ", " 9350620 , 01713373106");
        emergencyArrayList.add(emergency);
        emergency = new Emergency(" Joint Commissioner (Detective Branch) ", " 8332406, 8315026 , 07173373193");
        emergencyArrayList.add(emergency);
        emergency = new Emergency(" Joint Commissioner (Head Quarters) ", " 9339459 , 07173373103");
        emergencyArrayList.add(emergency);
        emergency = new Emergency(" Join Commissioner Crime & Operations ", " 9360806 , 01713373104");
        emergencyArrayList.add(emergency);

        EmergencyGroup emergencyGroup = new EmergencyGroup("Police Commissionars", emergencyArrayList);
        emergencyGroupArrayList.add(emergencyGroup);
        emergencyArrayList = new ArrayList<>();

        emergency = new Emergency("Adabor", "9133265, 01713-373183, 01711-887505");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Airport", " 8951281 , 01713-373162, 01711-533881");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Badda", " 9882652 , 01713-373173, 01712-953752");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Cantonment", " 8829267,  01713-373172");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Demra", " 7501155 , 01713-373144, 01711-323758");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Dhanmondi", " 8631941 , 01713-373126");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Gulshan", " 9880234 , 01713-373171, 01552-393158");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Hajaribag", " 9669900 , 01713-373136, 01817-050945");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Jatrabari", "7419505");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Kafrul", "9871771 , 01713-373191, 01711-939404");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Kadamtali", "7419505");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Kamrangirchar", "7320323 , 01713-373137, 01716-428888");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Khilgaon", " 7219090 , 01713-373154, 01711-650139");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Khilkhet", "8919364, 01713-373174, 01819-430671");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Kotwali", " 7116255- 01713-373135, 01711-827163");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Lalbagh", " 7316300 , 01713-373134, 01711-337048");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Mirpur", " 9001001 , 01713-373089, 01717-533396");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Mohammadpur", " 9119943 , 01713-373182, 01552-559929");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Motijheel", " 9571000 , 01713-373152, 01711-936365");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("New Market", " 8631942 -  01713-373128, 01711-029276");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Pallabi", " 8015122 , 01713-373190, 01711-809780");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Paltan", " 9360802 , 01713-373155, 01711-307493");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Ramna", " 9350468 , 01713-373125, 01714-012206");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Shabujbag", " 7219988 , 01713-373153, 01715-119527");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Shahbag", " 9676699 -  01713-373127, 01711-592076");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Shah Ali", "8060555,01713-373192 01712-517635");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Shyampur", " 7410691 -  01713-373145 01711-859660");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Sutrapur", "7116233 -  01713-373143 01717-258926");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Tejgaon", " 9119444 -  01713-373180, 01711-738495");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Turag", " 8914664 , 01713-373163, 01711-549852");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Uttara", " 8914126 , 01713-373161, 01711-301646");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Dakkhinkhan", " 8931777 , 01713-373165, 01711-018619");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Uttarkhan", "8931888 , 01713-373164, 01711-175545");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Darus Salam", " 8032333");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Tejgaon", "8836472,  01713-373181, 01711-825232");
        emergencyArrayList.add(emergency);

        emergencyGroup = new EmergencyGroup("Police Stations", emergencyArrayList);
        emergencyGroupArrayList.add(emergencyGroup);
        emergencyArrayList = new ArrayList<>();


        emergency = new Emergency("Sadar Ghat", " 7121206");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Mohakhali", " 9898537");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Farmgate", " 9119925");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("New Airport", " 8914442");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Sergeant Ahad", " 9558304");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("Darus Salam", " 9003820");
        emergencyArrayList.add(emergency);

        emergencyGroup = new EmergencyGroup("Police Boxes", emergencyArrayList);
        emergencyGroupArrayList.add(emergencyGroup);
        emergencyArrayList = new ArrayList<>();


        emergency = new Emergency("Police Control Room", " 9665407 ,8614300, 8616557");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("D.B Control Room", "9341511, 8352380, 9341493, 9361804");
        emergencyArrayList.add(emergency);
        emergency = new Emergency("CID Control Room", " 9331043");
        emergencyArrayList.add(emergency);

        emergencyGroup = new EmergencyGroup("Control Rooms", emergencyArrayList);
        emergencyGroupArrayList.add(emergencyGroup);
        emergencyArrayList = new ArrayList<>();

        emergency = new Emergency("Emergency Contacts", "Tel : 8614300, 9665407\nFax : 8616552\nE-mail : emergency@dmp.gov.bd");
        emergencyArrayList.add(emergency);
        emergencyGroup = new EmergencyGroup("Emergency", emergencyArrayList);
        emergencyGroupArrayList.add(emergencyGroup);


    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
