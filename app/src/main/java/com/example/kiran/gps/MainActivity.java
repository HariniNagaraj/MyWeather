package com.example.kiran.gps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout pullToRefresh;
    LocationManager locationManager;
    TextView place, humid, air;
    TextView date;
    protected static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    TextView temp;
Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
         pullToRefresh = findViewById(R.id.pullToRefresh);
pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        find_weather();
        pullToRefresh.setRefreshing(false);
    }
});
        date = findViewById(R.id.day);
        temp = findViewById(R.id.celcius);
        place = findViewById(R.id.city);
        humid = findViewById(R.id.humidity);
        air = findViewById(R.id.wind);
        showLocationSettingsDialog();

        requestReadLocationPermission();

        find_weather();


    }


    private void find_weather() {
        double latti = 0.0,longi = 0.0;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();
            }

            String urlparse = "https://api.openweathermap.org/data/2.5/weather?lat=" + latti + "&lon=" + longi + "&appid=b3e236d068148443f565e441eacf0a84&units=metric";
            String url = urlparse;
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject main_object = response.getJSONObject("main");
                        JSONObject main_object2 = response.getJSONObject("wind");
                        String temperature = String.valueOf(main_object.getInt("temp"));
                        String city = response.getString("name");
                        String wind = String.valueOf(main_object2.getInt("speed") * 3.6 + " kph");
                        String humidity = String.valueOf(main_object.getInt("humidity") + "%");


                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd");
                        String formatted_date = sdf.format(calendar.getTime());

                        date.setText(formatted_date);


                        temp.setText(temperature);
                        place.setText(city);
                        humid.setText(humidity);
                        air.setText(wind);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            );
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jor);

        }
    }

    private void requestReadLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explanation not needed, since user requests this themself

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MainActivity.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
        } else {
            privacyGuardWorkaround();
        }


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                return;
            }
        }
    }

    private void privacyGuardWorkaround() {
        // Workaround for CM privacy guard. Register for location updates in order for it to ask us for permission
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            DummyLocationListener dummyLocationListener = new DummyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, dummyLocationListener);
            locationManager.removeUpdates(dummyLocationListener);
        } catch (SecurityException e) {
            // This will most probably not happen, as we just got granted the permission
        }
    }

    private void showLocationSettingsDialog() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Location Settings");
            alertDialog.setMessage("Enable GPS press cancel if already set");
            alertDialog.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
    }



    public class DummyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}




