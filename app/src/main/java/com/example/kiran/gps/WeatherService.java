package com.example.kiran.gps;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
    private final Context context;

    public WeatherService(Context context) {
        this.context = context;
    }

    void findWeather(String city, final MyCallBack callBack) {
         String url = "https://www.vikramrao.in/api/weather.php?q=" + city.toLowerCase();
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
                    callBack.updateMyText(city,temperature,wind,humidity);
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
                    Toast.makeText(context, jsonError,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jor);
    }

    void findWeather(double latitude, double longitude, final MyCallBack callBack) {
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
                    callBack.updateMyText(city,temperature,wind,humidity);
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
                    Toast.makeText(context, jsonError,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jor);
    }

    public interface MyCallBack {
        void updateMyText(String city, String temperature, String wind, String humidity);
    }
}