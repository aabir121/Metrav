package com.example.aabir.metravv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aabir.metravv2.Utility.VolleySingleton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppLogin extends AppCompatActivity {

    AppCompatEditText emailText,passText;
    TextInputLayout emailTextLayout,passTextLayout;

    Button logBtn, signupBtn;
    ProgressDialog progressDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    String result="";


    private static final int permsRequestCodeGroup = 500;
    private static final int storageRequestCode=100;
    private static final int locationRequestCode=200;
    private static final int cameraRequestCode=300;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("language_select","English").equals("বাংলা"))
        {
            String languageToLoad  = "bn"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

        }

        setContentView(R.layout.activity_app_login);


//            checkAndRequestPermissions();




        emailTextLayout=(TextInputLayout)findViewById(R.id.useridTextLayout);
                passTextLayout=(TextInputLayout)findViewById(R.id.passTextLayout);
                emailText = (AppCompatEditText) findViewById(R.id.useridText);
                passText = (AppCompatEditText) findViewById(R.id.passText);
                logBtn = (Button) findViewById(R.id.loginBtn);
                signupBtn = (Button) findViewById(R.id.signupBtn);

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Signing In. Please Wait....");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


                SharedPreferences prefs2 = getSharedPreferences("Metrav", MODE_PRIVATE);
                if (prefs2.getInt("LoggedIn", 0) != 0) {

                    Intent intent = new Intent(AppLogin.this, NavDrawerMainMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }






        signupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AppLogin.this, AppRegister.class);
                        startActivity(intent);
                    }
                });
                logBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.show();
                        if(emailText.getText().toString().trim().isEmpty())
                        {
                            emailText.setError("This field cannot be empty.");
                            emailText.requestFocus();
                        }
                        else if(passText.getText().toString().trim().isEmpty())
                        {
                            passText.setError("This field cannot be empty.");
                            passText.requestFocus();
                        }
                        else
                        {
                            String login_url = "http://techlayerbd.com/metrav/login.php";

                            StringRequest stringRequest=new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    Log.d("imagecheck",response);
                                    result=response;
                                    if (result.equals("Invalid Password"))
                                    {
                                        passText.setError("Forgot Password?");
                                        passText.requestFocus();
                                    }
                                    else if (result.equals("No such User Found."))
                                    {

                                        emailText.setError("No Such User Found");
                                        emailText.requestFocus();
                                    }
                                    else {
                                        Log.d("JSONRESPONSE", result+"asdas");


                                        String userID = "", userMail = "", userName = "", userPass = "",imgstr="";
                                        JSONObject object = null;
                                        try {
                                            object = new JSONObject(result);
                                            userID = object.getString("uid");
                                            userMail = object.getString("email");
                                            userName = object.getString("name");
                                            userPass = object.getString("pass");
                                            imgstr=object.getString("imgstr");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        SharedPreferences settings = getSharedPreferences("Metrav", 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putInt("LoggedIn", 1);
                                        editor.putString("UserID", userID);
                                        editor.putString("UserEmail", userMail);
                                        editor.putString("Username", userName);
                                        editor.putString("UserPass", userPass);
                                        editor.putString("Image",imgstr);

                                        editor.commit();
                                        AppLogin.this.finish();

                                        Intent intent = new Intent(AppLogin.this, NavDrawerMainMenuActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);



                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Something went wrong. Please retry.",Toast.LENGTH_LONG).show();
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params=new HashMap<String, String>();
                                    params.put("username",emailText.getText().toString());
                                    params.put("password",passText.getText().toString());
                                    return params;
                                }
                            };
                             VolleySingleton.getmInstace(AppLogin.this).addToRequest(stringRequest);

                        }
                    }
                     });


                emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        emailText.setError(null);
                        return false;
                    }
                });
                passText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        passText.setError(null);
                        return false;
                    }
                });



    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    private boolean canMakeSmores(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generatePermissionList()
    {
        String[] perms = {"android.permission-group.STORAGE", "android.permission.CAMERA","android.permission-group.LOCATION"};
        requestPermissions(perms,permsRequestCodeGroup);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermission(String permission){
        if(canMakeSmores()){
            return(checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean storage,camera,location;

        switch (requestCode)
        {
            case 200:
                storage=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                camera=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                location=grantResults[2]==PackageManager.PERMISSION_GRANTED;
                break;
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean haveToCheck()
    {
        if(canMakeSmores())
        {
            String[] perms = {"android.permission-group.STORAGE", "android.permission.CAMERA","android.permission-group.LOCATION"};

            String perm1="android.permission-group.STORAGE";
            String perm2="android.permission-group.LOCATION";
            String perm3="android.permission.CAMERA";
            if(checkSelfPermission(perm1)!=PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(perms,permsRequestCodeGroup);
            }
        }

        return true;
    }





}
