package com.example.kiran.gps;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LocationService.LocationServiceDelegate, SearchManager.SearchManagerDelegate {

    private final DrawerManager drawerManager = new DrawerManager(this);
    private LocationService locationService;
    private SearchManager searchManager;
    private WeatherService weatherService;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.day)
    TextView date;
    @BindView(R.id.navList)
    ListView mDrawerList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupCurrentDate();
        initScreenRefresh();
        weatherService = new WeatherService(this,this);
        locationService = new LocationService(this, this);
        searchManager = new SearchManager(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLocationServices();
    }

    private void setupCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(currentDate);
        date.setText(formattedDate);
    }

    private void setupLocationServices() {
        if (locationService.requestReadLocationPermissionIfRequired()) {
            if (locationService.showGPSSettingDialogIfRequired()) {
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
                locationService.showGPSSettingDialogIfRequired();
                locationService.fetchLocation();
                updateWeather(locationService.latitude, locationService.longitude);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationService.onRequestPermissionsResult(requestCode, grantResults);
    }

    void updateWeather(String city) {
        weatherService.findWeather(city);
    }

    private void updateWeather(double latitude, double longitude) {
        weatherService.findWeather(latitude, longitude);
    }

    @Override
    public void locationUpdated() {
        updateWeather(locationService.latitude, locationService.longitude);
    }

    @Override
    public void cityChangedFromSearchBar(String city) {
        updateWeather(city);
    }

    @Override
    public void getCityForDrawer(String city) {
        drawerManager.showDrawerItems(mDrawerList, this, city);
    }
}




