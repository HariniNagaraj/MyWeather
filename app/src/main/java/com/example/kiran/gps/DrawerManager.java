package com.example.kiran.gps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class DrawerManager {

    private static final String LOGIN = "Login";
    private static final String FILENAME = "cities";
    private static final String LOGOUT = "Logout";
    @BindView(R.id.navList)
    ListView mDrawerList;
    private final Context activity;
    private List<String> cities = new ArrayList<>();
    private DrawerManagerDelegate delegate;

    public DrawerManager(Activity activity, DrawerManagerDelegate delegate) {
        this.activity = activity;
        this.delegate = delegate;
        ButterKnife.bind(this, activity);
    }

    public void addCity(String city) {
        if (isCityAdded(city)) return;
        cities.add(city);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String citiesJson = new Gson().toJson(cities);
        editor.putString(FILENAME, citiesJson);
        editor.apply();
    }

    public void showDrawerItems(String loginMenuText) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String citiesJson = sharedPreferences.getString(FILENAME, "[]");
        this.cities = new Gson().fromJson(citiesJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        checkLoginStatus(loginMenuText);
        final ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, cities);
        initDrawerListViewListener(mDrawerList, drawerAdapter);
    }

    private boolean isCityAdded(String city) {
        return cities.contains(city);
    }

    private void checkLoginStatus(String loginStatusText) {
        cities.remove(0);
        cities.add(0, loginStatusText);
    }

    private void initDrawerListViewListener(ListView drawerListView, final ArrayAdapter<String> drawerAdapter) {
        drawerListView.setAdapter(drawerAdapter);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                onMenuItemClicked(drawerAdapter.getItem(position));
            }
        });
    }

    private void onMenuItemClicked(String clickedOption) {
        if (clickedOption.equals(LOGIN)) {
            delegate.createSignInIntent();
        } else if (clickedOption.equals(LOGOUT)) {
            delegate.signOut();
            showDrawerItems(LOGIN);
        } else delegate.updateWeather(clickedOption);
    }

    interface DrawerManagerDelegate {
        void createSignInIntent();

        void signOut();

        void updateWeather(String city);
    }
}
