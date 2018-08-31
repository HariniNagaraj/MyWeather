package com.example.kiran.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Objects;

class LocationService implements LocationListener {

    private final MainActivity mainActivity;
    private final LocationServiceDelegate delegate;
    double latitude;
    double longitude;
    private final LocationManager locationManager;

    public LocationService(MainActivity mainActivity, LocationServiceDelegate delegate) {
        this.mainActivity = mainActivity;
        this.delegate = delegate;
        locationManager = (LocationManager) mainActivity.getSystemService(MainActivity.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    void fetchLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
    }

    boolean requestReadLocationPermissionIfRequired() {
        boolean locationPermissionGranted = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!locationPermissionGranted) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.CONTEXT_INCLUDE_CODE);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRequestPermissionsResult(int MY_PERMISSIONS_ACCESS_FINE_LOCATION, int[] grantResults) {
        switch (MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            case 1: {
                if ((grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //If user presses allow
                    Toast.makeText(mainActivity, "Permission granted!", Toast.LENGTH_SHORT).show();
                    if (showGPSSettingDialogIfRequired()) {
                        fetchLocation();
                    }
                } else {
                    Toast.makeText(mainActivity, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    boolean showGPSSettingDialogIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isGpsEnabled()) {
                showGPSSettingsDialog();
                return false;
            }
        }
        return true;
    }

    private void showGPSSettingsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
        alertDialog.setTitle("Location Settings");
        alertDialog.setMessage("Permission Granted, Please Enable GPS in Settings.");
        alertDialog.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mainActivity.setIntent(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                mainActivity.startActivity(mainActivity.getIntent());
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isGpsEnabled() {
        final LocationManager manager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delegate.locationUpdated();

            }
        });
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}