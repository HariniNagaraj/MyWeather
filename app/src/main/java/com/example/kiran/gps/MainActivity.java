package com.example.kiran.gps;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.kiran.gps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    ListAdapter adapter;
    List<String> arrayList= new ArrayList<>();
    private final WeatherService weather = new WeatherService(this);
    private final LocationService locationService = new LocationService(this);

    @BindView(R.id.pullToRefresh) SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.city) TextView place;
    @BindView(R.id.day) TextView date;
    @BindView(R.id.celcius) TextView temp;
    @BindView(R.id.wind) TextView air;
    @BindView(R.id.humidity) TextView humid;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle("Pull To Refresh");
        ButterKnife.bind(this);


        sampleCityList();

        onQueryTextListener();
        locationService.requestReadLocationPermission();
        showCurrentDate();
        locationService.fetchLocation();
        initScreenRefresh();
    }





    private void onQueryTextListener() {
        activityMainBinding.searchBar.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String Query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void sampleCityList() {
        arrayList.add("Delhi");
        arrayList.add("Mumbai");
        arrayList.add("kolkata");
        arrayList.add("Bangalore");
        arrayList.add("Hyderabad");
        adapter= new ListAdapter(arrayList);
activityMainBinding.listView.setAdapter(adapter);
        activityMainBinding.searchBar.setActivated(true);
        activityMainBinding.searchBar.setQueryHint("Type your keyword here");
        activityMainBinding.searchBar.onActionViewExpanded();
        activityMainBinding.searchBar.setIconified(false);
        activityMainBinding.searchBar.clearFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initScreenRefresh() {
        locationService.isGPSEnabled();
        updateWeather();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                locationService.isGPSEnabled();
                locationService.fetchLocation();
                updateWeather();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationService.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    private void updateWeather() {
        weather.findWeather(locationService.latitude, locationService.longitude, new WeatherService.MyCallBack() {
            @Override
            public void updateMyText(String city, String temperature, String wind, String humidity) {
                place.setText(city);
                humid.setText(humidity);
                air.setText(wind);
                temp.setText(temperature);
            }
        });
    }

    private void showCurrentDate() {
        Date day = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String formattedDate = simpleDateFormat.format(day);
        date.setText(formattedDate);
    }

}




