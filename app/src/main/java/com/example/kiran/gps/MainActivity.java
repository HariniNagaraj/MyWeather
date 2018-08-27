package com.example.kiran.gps;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final WeatherService weather = new WeatherService(this);
    private final LocationServices locationServices = new LocationServices(this);

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
        setContentView(R.layout.activity_main);
        setTitle("Pull To Refresh");
        ButterKnife.bind(this);
        locationServices.requestReadLocationPermission();
        showCurrentDate();
        locationServices.fetchLocation();
        initScreenRefresh();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initScreenRefresh() {
        locationServices.isGPSEnabled();
        updateWeather();
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                locationServices.isGPSEnabled();
                locationServices.fetchLocation();
                updateWeather();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void updateWeather() {
        weather.findWeather(locationServices.latitude, locationServices.longitude, new WeatherService.MyCallBack() {
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




