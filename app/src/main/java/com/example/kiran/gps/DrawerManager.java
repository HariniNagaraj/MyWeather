package com.example.kiran.gps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DrawerManager {

    ArrayAdapter<String> drawerAdapter;

    void showDrawerItems(ListView listView,MainActivity mainActivity) {
        final String[] dummyCityList = {"Bangalore", "Kolkata", "Mumbai", "Delhi", "Hyderabad"};
        drawerAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, dummyCityList);
        listView.setAdapter(drawerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String city = drawerAdapter.getItem(i);
            }
        });
    }
}