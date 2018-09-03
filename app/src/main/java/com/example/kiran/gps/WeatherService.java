package com.example.kiran.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
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

    public static final double WIND_SPEED = 3.6;
    @BindView(R.id.city)
    TextView place;
    @BindView(R.id.celcius)
    TextView temp;
    @BindView(R.id.wind)
    TextView air;
    @BindView(R.id.humidity)
    TextView humid;

    private final Context context;

    public WeatherService(Context context, Activity activity) {
        this.context = context;
        ButterKnife.bind(this, activity);
    }

    private void parseJsonAndUpdateWeather(String url) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseJsonData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorResponse(error);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jor);
    }

    private void errorResponse(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null && networkResponse.data != null) {
            String jsonError = networkResponse.data.toString();
            Toast.makeText(context, jsonError,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseJsonData(JSONObject response) {
        try {
            parseJsonFromResponse(response);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private void parseJsonFromResponse(JSONObject response) throws JSONException {
        JSONObject mainobject = response.getJSONObject("main");
        JSONObject mainobjectWind = response.getJSONObject("wind");
        String temperature = String.valueOf(mainobject.getInt("temp"));
        String city = response.getString("name");
        String wind = String.valueOf(mainobjectWind.getInt("speed") * WIND_SPEED + " kph");
        String humidity = String.valueOf(mainobject.getInt("humidity") + "%");
        updateWeather(temperature, city, wind, humidity);
    }

    private void updateWeather(String temperature, String city, String wind, String humidity) {
        temp.setText(temperature);
        place.setText(city);
        air.setText(wind);
        humid.setText(humidity);
    }

    void findWeather(Location location) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=b3e236d068148443f565e441eacf0a84&units=metric";
        parseJsonAndUpdateWeather(url);
    }

    void findWeather(String city) {
        String url = "https://www.vikramrao.in/api/weather.php?q=" + city.toLowerCase();
        parseJsonAndUpdateWeather(url);
    }
}
