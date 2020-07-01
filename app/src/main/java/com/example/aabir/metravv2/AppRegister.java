package com.example.aabir.metravv2;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatEditText;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aabir.metravv2.Utility.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class AppRegister extends AppCompatActivity {

    final static int RESULT_LOAD_IMAGE=1;
    private static final int RESULT_CROP = 2;
    private static final int REQUEST_CAMERA = 3;
    private static final int PERMISSION_REQUEST_CODE = 4;
    private static final int STORAGE_PERMISSION_CODE = 5;
    int request=0;
    AppCompatEditText usernameText,emailText,passText,cpassText;
    Button regBtn;
    ImageButton profilePicBtn;
    Bitmap bitmapProfile;
    String result="";


    //Progress dialog and alert dialog
    ProgressDialog progressDialog;
    AlertDialog alertDialog;
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

        setContentView(R.layout.activity_app_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering your account. Please Wait....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        alertDialog=new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.myDialog)).create();


        usernameText=(AppCompatEditText) findViewById(R.id.useridRegText);
        emailText=(AppCompatEditText)findViewById(R.id.emailRegText);
        passText=(AppCompatEditText)findViewById(R.id.passRegText);
        cpassText=(AppCompatEditText)findViewById(R.id.confirmPassRegText);
        profilePicBtn=(ImageButton)findViewById(R.id.iv_profile_pic);
        regBtn=(Button)findViewById(R.id.RegisterBtn);

        Log.d("PERMSISSIONTHIS","On create"+" "+true);
        profilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final CharSequence options[] = new CharSequence[] {"Take Photo", "Choose from Gallery"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(AppRegister.this);
                    builder.setTitle("Add Photo!");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on colors[which]
                            if(options[which].equals("Choose from Gallery"))
                            {
                                if(isReadStorageAllowed())
                                {
                                    Intent i = new Intent();
                                    i.setType("image/*");
                                    i.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                                }
                                requestStoragePermission();
                            }
                            else
                            {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }
                        }
                    });
                    builder.show();

            }
        });



        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(passText.getText().toString().equals(cpassText.getText().toString()) )
                {
                    if(!emailText.getText().toString().contains("@"))
                    {
                        emailText.setError("Emails ids should contain an @ character.");
                        emailText.requestFocus();
                    }
                    else if(!passStregthChecker(passText.getText().toString()))
                    {
                        passText.setError("Please use atleast an Uppercase,a Digit and a Lowercase letter and the length should be atleast of 8 characters.");
                        cpassText.setText("");
                    }
                    else{
                        progressDialog.show();
                        String login_url = "http://techlayerbd.com/metrav/register.php";
                        final String user_name = usernameText.getText().toString();
                        final String user_mail=emailText.getText().toString();
                        final String password = passText.getText().toString();
                        StringRequest stringRequest=new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                if(result.equals("There is already an acount with this email id."))
                                {
                                    emailText.setError("This Email id is already taken.");
                                    emailText.requestFocus();
                                }
                                else if(result.equals("Username already taken."))
                                {
                                    usernameText.setError("Username already taken.");
                                    usernameText.requestFocus();
                                }
                                else
                                {
                                    alertDialog.setMessage("Registration Successful");
                                    alertDialog.setCancelable(false);
                                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(AppRegister.this,AppLogin.class);
                                            startActivity(intent);
                                        }
                                    });
                                    alertDialog.show();
                                }
                                result=response;
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"Taking too long to process. Please check your internet connection.",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> map=new HashMap<>();
                                map.put("username",user_name);
                                map.put("user_mail",user_mail);
                                map.put("password",password);
                                map.put("image",imageToString(bitmapProfile));

                                return map;
                            }
                        };
                        VolleySingleton.getmInstace(AppRegister.this).addToRequest(stringRequest);



//                        new BackgroundWorker(AppRegister.this).execute("register",usernameText.getText().toString(),
//                                emailText.getText().toString(),passText.getText().toString());
                    }
                }
                else
                {
                    cpassText.setError("Passwords Dont Match");
                    cpassText.requestFocus();
                    cpassText.setText("");
                }
            }
        });

        usernameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                usernameText.setError(null);
                return false;
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

    public boolean passStregthChecker(String pass)
    {

        return pass.length()>=8;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                bitmapProfile=MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImage);

                performCrop(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            profilePicBtn.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
        if (requestCode == RESULT_CROP ) {
            if(resultCode == RESULT_OK){
                Log.d("croperror","crop result okay");
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                // Set The Bitmap Data To ImageView
                profilePicBtn.setImageBitmap(selectedBitmap);
                profilePicBtn.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else
            {
                Log.d("croperror","result not okay");
            }
        }
        if(requestCode==REQUEST_CAMERA && data!=null)
        {
            bitmapProfile = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapProfile.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            profilePicBtn.setImageBitmap(bitmapProfile);
        }

    }
    private void performCrop(Uri picUri) {
        try {
            //Start Crop Activity
            Log.d("croperror","first");
            Intent cropIntent = new Intent();
            cropIntent.setAction("com.android.camera.action.CROP");
            // indicate image type and Uri

            Log.d("croperror","second");
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);
            Log.d("croperror","third");
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
            Log.d("croperror","final");
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            Log.d("croperror","Error "+anfe.toString());
        }
    }

    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStreamy=new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStreamy);
        byte[] imgBytes=byteArrayOutputStreamy.toByteArray();

        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }


    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }


    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Toast.makeText(getApplicationContext(),"Permission needed to access your Gallery",Toast.LENGTH_LONG).show();
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
             //   Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
                profilePicBtn.performClick();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }
}
