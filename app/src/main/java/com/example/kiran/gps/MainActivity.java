package com.example.kiran.gps;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LocationService.LocationServiceDelegate, SearchManager.SearchManagerDelegate, DrawerManager.DrawerManagerDelegate, CloudService.CloudServiceDelegate {

    private static final int RC_SIGN_IN = 123;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.day)
    TextView date;
    @BindView(R.id.drawerid)
    DrawerLayout drawerLayout;
    private DrawerManager drawerManager;
    private LocationService locationService;
    private SearchManager searchManager;
    private WeatherService weatherService;
    private CloudService cloudService;
    private AnalyticsService analyticsService;

    @Override
    public void createSignInIntent() {
        List<AuthUI.IdpConfig> googleSignIn = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(googleSignIn).setIsSmartLockEnabled(false).build(), RC_SIGN_IN);
    }

    @Override
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        drawerManager.clear();
                        Toast.makeText(MainActivity.this, "You have been signed out!", Toast.LENGTH_SHORT).show();
                        drawerManager.updateMenus(DrawerManager.LOGIN);
                    }
                });
    }

    @Override
    public void updateWeather(String city) {
        weatherService.findWeather(city);
    }

    @Override
    public void clearUserData() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        drawerManager.clear();
                        drawerManager.updateMenus(DrawerManager.LOGOUT);
                        Toast.makeText(MainActivity.this, "Data has been cleared.", Toast.LENGTH_SHORT).show();
                    }
                });
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
        cloudService.uploadCitiesListToCloud(drawerManager.cities);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initServices();
        setupLocationServices();
        setupCurrentDate();
        initScreenRefresh();
        drawerManager.initDrawerMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(MainActivity.this, "welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                drawerManager.updateMenus(DrawerManager.LOGOUT);
                cloudService.sync();
                analyticsService.OnUserSignIn(user);
            } else {
                Toast.makeText(MainActivity.this, "sign-in failed, try again!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        locationService.fetchLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationService.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void initServices() {
        weatherService = new WeatherService(this, this);
        locationService = new LocationService(this, this);
        searchManager = new SearchManager(this, this);
        drawerManager = new DrawerManager(this, this);
        cloudService = new CloudService(this);
        analyticsService = new AnalyticsService(this);
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
        analyticsService.onUserRefresh();
    }

    private void updateWeather(Location location) {
        weatherService.findWeather(location);
    }

    public void showDrawerMenu(View view) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void citiesDownloaded(List<String> citiesList) {
        if (citiesList == null) citiesList = new ArrayList<>();
        for (String city : citiesList)
            drawerManager.addCity(city);
        cloudService.uploadCitiesListToCloud(drawerManager.cities);
    }
}
