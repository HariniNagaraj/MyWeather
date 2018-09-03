package com.example.kiran.gps;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LocationService.LocationServiceDelegate, SearchManager.SearchManagerDelegate {

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.day)
    TextView date;

    private DrawerManager drawerManager;
    private LocationService locationService;
    private SearchManager searchManager;
    private WeatherService weatherService;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupCurrentDate();
        initScreenRefresh();
        objectReferences();
    }

    private void objectReferences() {
        weatherService = new WeatherService(this, this);
        locationService = new LocationService(this, this);
        searchManager = new SearchManager(this, this);
        drawerManager = new DrawerManager(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLocationServices();
        drawerManager.showDrawerItems(this);
    }

    private void setupCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        date.setText(formattedDate);
    }

    private void setupLocationServices() {
        if (locationService.requestReadLocationPermissionIfRequired()) {
            if (locationService.showGpsSettingDialogIfRequired()) {
                locationService.fetchLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (searchManager.isExpanded()) {
            searchManager.minimize();
            return;
        }
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initScreenRefresh() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onUserRefresh();
            }
        });
    }

    private void onUserRefresh() {
        locationService.showGpsSettingDialogIfRequired();
        locationService.fetchLocation();
        updateWeather(locationService.location);
        pullToRefresh.setRefreshing(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationService.onRequestPermissionsResult(requestCode, grantResults);
    }

    void updateWeather(String city) {
        weatherService.findWeather(city);
    }

    private void updateWeather(Location location) {
        weatherService.findWeather(location);
    }

    @Override
    public void locationUpdated(Location location) {
        updateWeather(location);
    }

    @Override
    public void cityChangedFromSearchBar(String city) {
        updateWeather(city);
    }

    @Override
    public void addCityToDrawer(String city) {
        drawerManager.addCity(city);
    }
}




