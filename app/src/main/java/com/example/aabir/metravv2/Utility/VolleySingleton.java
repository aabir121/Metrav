package com.example.aabir.metravv2.Utility;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by abir on 3/31/2017.
 */

public class VolleySingleton {
    private static VolleySingleton mInstace;
    private RequestQueue requestQueue;
    private Context mCtx;

    public VolleySingleton(Context context)
    {
        mCtx=context;
        requestQueue=getRequestQueue();
    }


    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getmInstace(Context context)
    {
        if(mInstace==null)
        {
            mInstace=new VolleySingleton(context);
        }
        return mInstace;
    }

    public <T> void addToRequest(Request<T> request)
    {
        requestQueue.add(request);
    }
}
