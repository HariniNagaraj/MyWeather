package com.example.kiran.gps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout pullToRefresh;
    private TextView place, humid, air, date,temp;
    private Intent intent;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        screenRefresher();
        setUIElements();
        showLocationSettingsDialog();
        requestReadLocationPermission();
        setCurrentDate();
        findWeather();
    }

    private void screenRefresher() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findWeather();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void setUIElements() {
        date = findViewById(R.id.day);
        place = findViewById(R.id.city);
        humid = findViewById(R.id.humidity);
        air = findViewById(R.id.wind);
        temp = findViewById(R.id.celcius);
    }

    private void setCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String formattedDate = df.format(c);
        date.setText(formattedDate);
    }

    private void findWeather() {
        LocationManager locationManager;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        double latitude = 0.0, longitude = 0.0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=b3e236d068148443f565e441eacf0a84&units=metric";
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
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String jsonError = new String(networkResponse.data);
                        Toast.makeText(MainActivity.this, jsonError,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void requestReadLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showLocationSettingsDialog() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
}




