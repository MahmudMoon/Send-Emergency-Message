package com.example.moonc.emergencysms;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mGoogleApiClient;
    Button btn;
    String num1 = "", num2 = "", num3 = "";
    String[] ary;
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String postalCode = "";
    String knownName = "";
    ImageView iv;
    File file;
    boolean conection  = false;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SavedContracts";

    public static String[] Load(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl = 0;
        try {
            while ((test = br.readLine()) != null) {
                anzahl++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis.getChannel().position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try {
            while ((line = br.readLine()) != null) {
                array[i] = line;
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dir = new File(path);
        dir.mkdirs();

      file = new File(path+"/Contracts.txt");

        btn = (Button) findViewById(R.id.configure);
        iv = (ImageView) findViewById(R.id.emergency);
        ary = new String[3];
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, addnumber.class);
                        startActivity(intent);
                    }
                }
        );

       conection  =  isConnected();
       if(conection) {
           getLocation();
       }
        else
       {
           Toast.makeText(this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
       }
    }



    public void clicked(View view) {



        if(file.length()>3) {
            try {
                sendSms();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else
        {
            Toast.makeText(this,"Configure First",Toast.LENGTH_SHORT).show();
        }
    }

    public void getData() {

        ary = Load(file);

        Log.v("MainActivity", "num1 = " + num1 + " \nnum2 = " + num2 + " \nnum3 = " + num3);
    }

        public void sendSms()throws IOException {
        getData();
        for (int i = 0; i < 3; i++) {
            if (!ary[i].isEmpty() && ary[i]!="null") {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", ary[i], null));
                intent.putExtra("sms_body", "Country = " + country + "\n" + "City = " + city + "\n" + "Postalcode = " + postalCode + "\n" + "Popular place = " + knownName + "\n" + "State = " + state + "\n");
                startActivity(intent);
            }else
            {
                    //do nothing
            }
        }
    }


    public void getLocation() throws NullPointerException {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        }
    }


    public void onConnected(@Nullable Bundle bundle) throws NullPointerException{
        double lon = 0.0, lat = 0.0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Check Access Permission", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                lon = mLastLocation.getLatitude();
                lat = mLastLocation.getLongitude();
               try {
                   getAddress(lon, lat);
               }catch (NullPointerException e)
               {
                   //do nothing
               }
            }

            Toast.makeText(this, " " + lon + " " + lat, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();

    }


    public void getAddress(double latitude, double longitude) throws NullPointerException {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            Log.v("MainActivity", String.valueOf(addresses.get(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (geocoder.isPresent()) {
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
        }

    }

    public boolean isConnected() {

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivity!=null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if(info!=null)
            {
                if(info.getState()==NetworkInfo.State.CONNECTED)
                {
                    return true;
                }else
                    return false;
            }else
                return false;
        }return  false;
    }
}
