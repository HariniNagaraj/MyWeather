package com.example.kiran.gps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final WeatherService weather = new WeatherService(this);
    private SwipeRefreshLayout pullToRefresh;
    protected static TextView place, humid, air, date,temp;
    private double latitude, longitude;
    private Intent intent;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Pull To Refresh");
        bindUIElements();
        requestReadLocationPermission();
        showLocationSettingsDialog();
        showCurrentDate();
        locationServices();
        weather.findWeather(latitude,longitude);
        initScreenRefresh();
    }

    private void locationServices() {
        LocationManager locationManager;
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

        }
    }

    private void initScreenRefresh() {
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                locationServices();
                weather.findWeather(latitude,longitude);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    protected void bindUIElements() {
        pullToRefresh = findViewById(R.id.pullToRefresh);
        date = findViewById(R.id.day);
        place = findViewById(R.id.city);
        humid = findViewById(R.id.humidity);
        air = findViewById(R.id.wind);
        temp = findViewById(R.id.celcius);
    }

    private void showCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        String formattedDate = df.format(c);
        date.setText(formattedDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void requestReadLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showLocationSettingsDialog() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Location Settings");
            alertDialog.setMessage("Enable GPS press cancel if already set");
            alertDialog.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

    }
}




