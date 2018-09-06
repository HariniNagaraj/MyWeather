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

    public static final String LOGOUT = "Logout";
    private static final String LOGIN = "Login";
    private static final String FILENAME = "cities";
    @BindView(R.id.navList)
    ListView mDrawerList;
    private final Context activity;
    private List<String> cities = new ArrayList<>();
    private DrawerManagerDelegate delegate;
    private ArrayAdapter<String> drawerAdapter;

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

    public void initDrawerMenu() {
        loadMenus();
        initMenuList();
    }

    private void initMenuList() {
        drawerAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, cities);
        mDrawerList.setAdapter(drawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                onMenuItemClicked(drawerAdapter.getItem(position));
            }
        });
    }

    private void loadMenus() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String citiesJson = sharedPreferences.getString(FILENAME, "[]");
        this.cities = new Gson().fromJson(citiesJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        updateLoginStatusMenu(LOGIN);
    }

    public void updateMenus(String loginStatus) {
        updateLoginStatusMenu(loginStatus);
        drawerAdapter.notifyDataSetChanged();
    }

    private boolean isCityAdded(String city) {
        return cities.contains(city);
    }

    private void updateLoginStatusMenu(String loginStatusText) {
        cities.set(0, loginStatusText);
    }

    private void onMenuItemClicked(String clickedOption) {
        if (clickedOption.equals(LOGIN)) {
            delegate.createSignInIntent();
        } else if (clickedOption.equals(LOGOUT)) {
            delegate.signOut();
            updateMenus(LOGIN);
        } else delegate.updateWeather(clickedOption);
    }

    interface DrawerManagerDelegate {
        void createSignInIntent();

        void signOut();

        void updateWeather(String city);
    }
}
