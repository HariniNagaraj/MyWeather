package com.example.kiran.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
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

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

class WeatherService {

    private static final double WIND_SPEED = 3.6;
    @BindView(R.id.city)
    TextView place;
    @BindView(R.id.celcius)
    TextView temp;
    @BindView(R.id.wind)
    TextView air;
    @BindView(R.id.humidity)
    TextView humid;

    private final Context context;

    WeatherService(Context context, Activity activity) {
        this.context = context;
        ButterKnife.bind(this, activity);
    }

    void findWeather(Location location) {
        parseJsonAndUpdateWeather("https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=b3e236d068148443f565e441eacf0a84&units=metric");
    }

    void findWeather(String city) {
        parseJsonAndUpdateWeather("https://www.vikramrao.in/api/weather.php?q=" + city.toLowerCase());
    }

    private void parseJsonAndUpdateWeather(String url) {
        RequestQueue volleyQueue = Volley.newRequestQueue(context);
        volleyQueue.add(getJsonObjectRequest(url));
    }

    @NonNull
    private JsonObjectRequest getJsonObjectRequest(String url) {
        return new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
    }

    private void errorResponse(VolleyError error) {
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse != null && networkResponse.data != null) {
            String jsonError = Arrays.toString(networkResponse.data);
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
        JSONObject mainObject = response.getJSONObject("main");
        JSONObject mainObjectWind = response.getJSONObject("wind");
        String temperature = String.valueOf(mainObject.getInt("temp"));
        String city = response.getString("name");
        String wind = String.valueOf(mainObjectWind.getInt("speed") * WIND_SPEED + " kph");
        String humidity = String.valueOf(mainObject.getInt("humidity") + "%");
        updateWeather(new WeatherInfo(temperature, city, wind, humidity));
    }

    private void updateWeather(WeatherInfo weatherInfo) {
        temp.setText(weatherInfo.getTemperature());
        place.setText(weatherInfo.getCity());
        air.setText(weatherInfo.getWind());
        humid.setText(weatherInfo.getHumidity());
    }

    private static final class WeatherInfo {
        private final String temperature;
        private final String city;
        private final String wind;
        private final String humidity;

        private WeatherInfo(String temperature, String city, String wind, String humidity) {
            this.temperature = temperature;
            this.city = city;
            this.wind = wind;
            this.humidity = humidity;
        }

        public String getTemperature() {
            return temperature;
        }

        public String getCity() {
            return city;
        }

        public String getWind() {
            return wind;
        }

        public String getHumidity() {
            return humidity;
        }
    }
}
