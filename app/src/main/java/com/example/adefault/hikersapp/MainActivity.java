package com.example.adefault.hikersapp;

import android.Manifest;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager lm;
    LocationListener l;
    double lat,lon,alti,acc;
    String address1 = "";

    public void maps(View view)
    {
        Intent i = new Intent(this,MapsActivity.class);
        String coordinates = String.valueOf(lat)+","+String.valueOf(lon);
        i.putExtra("msg",coordinates);
        startActivity(i);
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    public void update(Location location)
    {
        lat=location.getLatitude();
        lat = Double.parseDouble(String.format("%.2f",lat));
        lon=location.getLongitude();
        alti=location.getAltitude();
        acc=location.getAccuracy();

        TextView latitude = (TextView) findViewById(R.id.textView6);
        TextView longitude = (TextView) findViewById(R.id.textView3);
        TextView altitude = (TextView) findViewById(R.id.textView4);
        TextView accuracy = (TextView) findViewById(R.id.textView5);
        TextView address = (TextView) findViewById(R.id.textView2);

        latitude.setText("Latitude : "+lat);
        longitude.setText("Longitude : "+lon);
        altitude.setText("Altitude : "+alti);
        accuracy.setText("Accuracy : "+acc);
        address.setText("Address : \n"+address1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lm = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        l = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                address1="";
                Geocoder g = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> list = g.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(list!=null && list.size()>0)
                    {
                        if(list.get(0).getThoroughfare()!=null)
                        {
                            address1+=list.get(0).getThoroughfare()+"\n";
                        }
                        if(list.get(0).getLocality()!=null)
                        {
                            address1+=list.get(0).getLocality()+"\n";
                        }
                        if(list.get(0).getPostalCode()!=null)
                        {
                            address1+=list.get(0).getPostalCode()+"\n";
                        }
                        if(list.get(0).getAdminArea()!=null)
                        {
                            address1+=list.get(0).getAdminArea();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                update(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,l);
            Location location1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location1!=null) {
                update(location1);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,l);
            }
        }
    }

}
