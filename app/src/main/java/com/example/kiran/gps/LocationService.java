package com.example.kiran.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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

    private static final int MIN_TIME = 1000;
    public Location location;
    private final Activity activity;
    private final LocationServiceDelegate delegate;
    private final LocationManager locationManager;

    LocationService(Activity activity, LocationServiceDelegate delegate) {
        this.activity = activity;
        this.delegate = delegate;
        locationManager = (LocationManager) activity.getSystemService(MainActivity.LOCATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == 1) {
            String status = "denied";
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                status = "granted";
                if (showGpsSettingDialogIfRequired()) fetchLocation();
            }
            Toast.makeText(activity, "Permission " + status, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        this.location = location;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                delegate.locationUpdated(location);
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

    @SuppressLint("MissingPermission")
    void fetchLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
    }

    boolean requestReadLocationPermissionIfRequired() {
        boolean locationPermissionGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!locationPermissionGranted) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.CONTEXT_INCLUDE_CODE);
            return false;
        }
        return true;
    }

    boolean showGpsSettingDialogIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isGpsEnabled()) {
                showGpsSettingsDialog();
                return false;
            }
        }
        return true;
    }

    private void showGpsSettingsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Location Settings");
        alertDialog.setMessage("Permission Granted, Please Enable GPS in Settings.");
        initGpsSettingsDialogActions(alertDialog);
        alertDialog.show();
    }

    private void initGpsSettingsDialogActions(AlertDialog.Builder alertDialog) {
        initGpsSettingsDialogSettingsClicked(alertDialog);
        initGpsSettingsDialogCancelClicked(alertDialog);
    }

    private void initGpsSettingsDialogCancelClicked(AlertDialog.Builder alertDialog) {
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void initGpsSettingsDialogSettingsClicked(AlertDialog.Builder alertDialog) {
        alertDialog.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isGpsEnabled() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    interface LocationServiceDelegate {
        void locationUpdated(Location location);
    }
}
