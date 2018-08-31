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

    private SearchManager searchManager;
    private final DrawerManager drawerManager = new DrawerManager();
    private final WeatherService weatherService = new WeatherService(this);
    private LocationService locationService;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.city)
    TextView place;
    @BindView(R.id.day)
    TextView date;
    @BindView(R.id.celcius)
    TextView temp;
    @BindView(R.id.wind)
    TextView air;
    @BindView(R.id.humidity)
    TextView humid;
    @BindView(R.id.navList)
    ListView mDrawerList;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        drawerManager.showDrawerItems(mDrawerList,this);
        setupInitialUI();
        initScreenRefresh();
        locationService = new LocationService(this, this);
        searchManager = new SearchManager(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLocationServices();
    }

    private void setupInitialUI() {
        showCurrentDate();
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
        weatherService.findWeather(city, new WeatherService.MyCallBack() {
            @Override
            public void updateMyText(String city, String temperature, String wind, String humidity) {
                updateUI(city, temperature, wind, humidity);
            }
        });
    }

    private void updateUI(String city, String temperature, String wind, String humidity) {
        place.setText(city);
        humid.setText(humidity);
        air.setText(wind);
        temp.setText(temperature);
    }

    private void updateWeather(double latitude, double longitude) {
        weatherService.findWeather(latitude, longitude, new WeatherService.MyCallBack() {
            @Override
            public void updateMyText(String city, String temperature, String wind, String humidity) {
                updateUI(city, temperature, wind, humidity);
            }
        });
    }

    private void showCurrentDate() {
        Date day = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(day);
        date.setText(formattedDate);
    }

    @Override
    public void locationUpdated() {
        updateWeather(locationService.latitude, locationService.longitude);
    }

    @Override
    public void cityChanged(String city) {
        updateWeather(city);
    }
}




