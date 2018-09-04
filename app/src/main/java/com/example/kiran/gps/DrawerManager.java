package com.example.kiran.gps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class DrawerManager {

    private static final String FILENAME = "cities";
    @BindView(R.id.navList)
    ListView mDrawerList;
    private List<String> cities = new ArrayList<>();
    private final Context context;

    public DrawerManager(Activity activity, Context context) {
        this.context = context;
        ButterKnife.bind(this, activity);
    }

    void showDrawerItems(final MainActivity mainActivity,String user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String citiesJson = sharedPreferences.getString(FILENAME, "[]");
        Gson gson = new Gson();
        List<String> cities = gson.fromJson(citiesJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        this.cities = cities;
        loginChecker(user);
        final ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_list_item_1, cities);
        drawerListViewListener(mDrawerList, mainActivity, drawerAdapter);
    }

    private void loginChecker(String user) {
        if(cities.contains("Login")){
            cities.add(0,"Logout");
        }
    }

    private void drawerListViewListener(ListView drawerListView, final MainActivity mainActivity, final ArrayAdapter<String> drawerAdapter) {
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                String city = drawerAdapter.getItem(position);
                if(city.equals("Login")){
                    mainActivity.createSignInIntent();
                    showDrawerItems(mainActivity,"Logout");
                }else if(city.equals("Logout")){
                    mainActivity.signOut();
                    showDrawerItems(mainActivity,"Login");
                }
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String citiesJson = new Gson().toJson(cities);
        editor.putString(FILENAME, citiesJson);
        editor.apply();
    }
}
