package com.example.kiran.gps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

class DrawerManager {

    void showDrawerItems(ListView listView,MainActivity mainActivity) {
        final String[] dummyCityList = {};
        ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, dummyCityList);
        listView.setAdapter(drawerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
               // String city = drawerAdapter.getItem(i);
            }
        });
    }
}