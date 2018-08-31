package com.example.kiran.gps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

class DrawerManager {

    private final ArrayList<String> addCityToDrawer = new ArrayList<>();

    void showDrawerItems(ListView drawerListView, final MainActivity mainActivity, String city) {
        if (addCityToDrawer.contains(city)) {
            return;
        } else {
            addCityToDrawer.add(city);
        }
        final ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, addCityToDrawer);
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String city = drawerAdapter.getItem(i);
                mainActivity.updateWeather(city);
            }
        });
    }
}