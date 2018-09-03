package com.example.kiran.gps;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

class DrawerManager {

    SharedPreferenceCallBack getCity;
    private final ArrayList<String> addCityToDrawer = new ArrayList<>();
    private final Context context;
    public DrawerManager(Context context,SharedPreferenceCallBack getCity) {
        this.getCity=getCity;
        this.context=context;
    }

    void showDrawerItems(ListView drawerListView, final MainActivity mainActivity, String city) {
             if(!isCityAdded(city)){
            addCityToDrawer.add(city);
        }
        getCity.getCityForSharedPre(city);
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

interface SharedPreferenceCallBack{
    void getCityForSharedPre(String city);
}