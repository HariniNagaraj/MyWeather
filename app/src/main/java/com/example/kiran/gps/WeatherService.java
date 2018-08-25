package com.example.kiran.gps;

import static com.example.kiran.gps.MainActivity.air;
import static com.example.kiran.gps.MainActivity.humid;
import static com.example.kiran.gps.MainActivity.latitude;
import static com.example.kiran.gps.MainActivity.longitude;
import static com.example.kiran.gps.MainActivity.place;
import static com.example.kiran.gps.MainActivity.temp;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

public class WeatherService {
    private final MainActivity mainActivity;
    public WeatherService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    void findWeather() {
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
                    Toast.makeText(mainActivity, jsonError,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        queue.add(jor);

    }
}