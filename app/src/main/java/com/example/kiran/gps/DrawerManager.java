package com.example.kiran.gps;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

class DrawerManager {

    private SharedPreferences saveCityListFromDrawer;
    private final ArrayList<String> addCityToDrawer = new ArrayList<>();
    private final Context context;
    public DrawerManager(Context context) {
        this.context=context;
    }

    void showDrawerItems(ListView drawerListView, final MainActivity mainActivity, String city) {
        saveCityListFromDrawer =context.getSharedPreferences("savedCities", Context.MODE_PRIVATE);
        if(!isCityAdded(city)){
            addCityToDrawer.add(city);
        }
        final ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, addCityToDrawer);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String city = drawerAdapter.getItem(position);
                mainActivity.updateWeather(city);
            }
        });
    }

    private boolean isCityAdded(String city){
        return addCityToDrawer.contains(city);
    }
}