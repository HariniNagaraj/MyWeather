package com.example.kiran.gps;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

import butterknife.BindView;
import butterknife.ButterKnife;

class WeatherService {

    private final Context context;

    @BindView(R.id.city)
    TextView place;
    @BindView(R.id.celcius)
    TextView temp;
    @BindView(R.id.wind)
    TextView air;
    @BindView(R.id.humidity)
    TextView humid;

    public WeatherService(Context context, Activity activity) {
        this.context = context;
        ButterKnife.bind(this, activity);
    }

    private void parseJsonAndUpdateWeather(String url) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
                parseJsonData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Toast.makeText(context, jsonError,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jor);
    }

    private void parseJsonData(JSONObject response) {
        try {
            JSONObject mainobject = response.getJSONObject("main");
            JSONObject mainobjectWind = response.getJSONObject("wind");
            String temperature = String.valueOf(mainobject.getInt("temp"));
            String city = response.getString("name");
            String wind = String.valueOf(mainobjectWind.getInt("speed") * 3.6 + " kph");
            String humidity = String.valueOf(mainobject.getInt("humidity") + "%");
            updateWeather(temperature, city, wind, humidity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateWeather(String temperature, String city, String wind, String humidity) {
        temp.setText(temperature);
        place.setText(city);
        air.setText(wind);
        humid.setText(humidity);
    }

    void findWeather(double latitude, double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=b3e236d068148443f565e441eacf0a84&units=metric";
        parseJsonAndUpdateWeather(url);
    }

    void findWeather(String city) {
        String url = "https://www.vikramrao.in/api/weather.php?q=" + city.toLowerCase();
        parseJsonAndUpdateWeather(url);
    }
}