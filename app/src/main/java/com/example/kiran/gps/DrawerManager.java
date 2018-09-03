package com.example.kiran.gps;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

class DrawerManager {

    private static final String filename = "cities";
    private List<String> cities = new ArrayList<>();
    private final Context context;

    public DrawerManager(Context context) {
        this.context = context;
    }

    void showDrawerItems(ListView drawerListView, final MainActivity mainActivity) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String citiesJson = sharedPreferences.getString(filename, "[]");
        Gson gson = new Gson();
        List<String> cities = gson.fromJson(citiesJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        this.cities = cities;
        final ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, cities);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String city = drawerAdapter.getItem(position);
                mainActivity.updateWeather(city);
            }
        });
    }

    private boolean isCityAdded(String city) {
        return cities.contains(city);
    }

    public void addCity(String city) {
        if (isCityAdded(city)) return;
        cities.add(city);
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String citiesJson = new Gson().toJson(cities);
        editor.putString(filename, citiesJson);
        editor.apply();
    }
}
