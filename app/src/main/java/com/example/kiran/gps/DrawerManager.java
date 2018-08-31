package com.example.kiran.gps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

class DrawerManager {

    ArrayList<String> drawerCity = new ArrayList<>();

    void showDrawerItems(ListView listView, MainActivity mainActivity, String city) {
        drawerCity.add(city);
        final ArrayAdapter<String> drawerAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, drawerCity);
        listView.setAdapter(drawerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String city = drawerAdapter.getItem(i);

            }
        });
    }


}