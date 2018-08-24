package com.example.kiran.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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

public class Weather {
    private final MainActivity mainActivity;

    public Weather(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    void findWeather() {
        LocationManager locationManager;
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        double latitude = 0.0, longitude = 0.0;
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
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
                        mainActivity.getTemp().setText(temperature);
                        mainActivity.getPlace().setText(city);
                        mainActivity.getHumid().setText(humidity);
                        mainActivity.getAir().setText(wind);
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
                        Toast.makeText(mainActivity, jsonError,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
            RequestQueue queue = Volley.newRequestQueue(mainActivity);
            queue.add(jor);
        }
    }
}