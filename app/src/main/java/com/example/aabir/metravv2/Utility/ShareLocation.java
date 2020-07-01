package com.example.aabir.metravv2.Utility;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by abir on 4/3/2017.
 */

public class ShareLocation {

    Context context;
    Activity activity;

    public ShareLocation(Context context, Activity activity)
    {
        this.context=context;
        this.activity=activity;
    }

    public String getPosition() {
        StringBuilder positionMessage=new StringBuilder("");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        try {
            if (permissionGranted) {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.
                positionMessage.append("Download it from the PlayStore.\n\n");
                positionMessage.append("https://maps.google.com/?q=");
                positionMessage.append(location.getLatitude());
                positionMessage.append(",");
                positionMessage.append(location.getLongitude());

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
        return positionMessage.toString();
    }

}
