package com.example.aabir.metravv2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.aabir.metravv2.Utility.ShareLocation;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by abir on 4/6/2017.
 */

public class NavDrawerBusOffline extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

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


        setContentView(R.layout.activity_nav_drawer_bus_offline);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageButton sideBarToggle = (ImageButton) findViewById(R.id.sideBarToggleBtn);
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

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        ImageButton srch=(ImageButton)findViewById(R.id.search_btn);
        srch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lin=(LinearLayout)OneFragment.root.findViewById(R.id.intra_search_layout);
                LinearLayout lin2=(LinearLayout)TwoFragment.root.findViewById(R.id.inter_search_layout);

                if(tabLayout.getSelectedTabPosition()==0)
                {
                    if(lin.getVisibility()==View.GONE)
                        lin.setVisibility(View.VISIBLE);
                    else
                    {
                        lin.setVisibility(View.GONE);
                    }
                }
                else
                {
                    if(lin2.getVisibility()==View.GONE)
                        lin2.setVisibility(View.VISIBLE);
                    else
                    {
                        lin2.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        OneFragment one=new OneFragment();
        one.setContext(getApplicationContext());
        adapter.addFragment(one, "Inter City");
        adapter.addFragment(new TwoFragment(), "Intra City");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
        else if(id==R.id.nav_emergency_police)
        {
            Intent intent=new Intent(getApplicationContext(),NavDrawerEmergencyContacts.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
