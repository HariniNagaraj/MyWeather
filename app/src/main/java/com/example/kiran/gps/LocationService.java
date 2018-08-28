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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Objects;

public class LocationService {
    protected double latitude, longitude;
    private final MainActivity mainActivity;
    public LocationService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    void fetchLocation() {
        LocationManager locationManager;
        locationManager = (LocationManager) mainActivity.getSystemService(MainActivity.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
               latitude=location.getLatitude();
                longitude=location.getLongitude();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void isGPSEnabled() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED & ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            showLocationSettingsDialog();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void requestReadLocationPermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.CONTEXT_INCLUDE_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRequestPermissionsResult(int MY_PERMISSIONS_ACCESS_FINE_LOCATION, String[] PERMISSIONS_GPS, int[] grantResults) {
        switch (MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            case 1: {
                if ((grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //If user presses allow
                    Toast.makeText(mainActivity, "Permission granted!", Toast.LENGTH_SHORT).show();
                    showLocationSettingsDialog();
                } else {
                    Toast.makeText(mainActivity, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void showLocationSettingsDialog() {
        final LocationManager manager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!Objects.requireNonNull(manager).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
    }
}