package com.example.kashif.lab3;

/**
 * Created by Kashif on 04-Jun-16.
 */

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class MyService extends Service implements LocationListener {

    private double latitudeValue;
    private double longitudeValue;
    double[] latitude = new double[1000];
    double[] longitude = new double[1000];
    int i = 0;
    String Message;
    boolean check = false;
    double dist = 0;
    double finalDist;
    double speed;
    private Location location;

    protected LocationManager mLocationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }

        // Requesting location updates after every 5 secs And/Or after the distance changed by 5 meters
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
    }

    // On service binds with the main activity
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Service: onBind", Toast.LENGTH_LONG).show();
        return myBinder;
    }

    // On service unbinds with the main activity
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(), "Service: Unbind", Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);
    }

    // On destroying the running service
    public void onDestroy(){
        stopService(new Intent(getBaseContext(), MyService.class));
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service: Stopped", Toast.LENGTH_LONG).show();
    }

    // writing the Latitude and Longitude information on the External Storage
    private void addLocationToList() {
        latitude[i] = latitudeValue;
        longitude[i] = longitudeValue;
        i++;
        String state = Environment.getExternalStorageState();

        // Checking if the SD mounted or not
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File Root = Environment.getExternalStorageDirectory();
            File Dir = new File(Root.getAbsolutePath() + "/MyAppFile");
            Dir.mkdir();
            File fileName = new File(Dir, "Text.GPX");
            if (check) {
                Message = Message + (String.valueOf(latitudeValue) + "   " + String.valueOf(longitudeValue) + "\r\n");
            } else {
                Message = (String.valueOf(latitudeValue) + "   " + String.valueOf(longitudeValue) + "\r\n");
                check = true;
            }
            Log.d("Message : ", String.valueOf(Message));
            try {
                fileName.createNewFile();
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(Message.getBytes());
                fos.close();
                Toast.makeText(getBaseContext(), "Done writing on SD card", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "SD card not found", Toast.LENGTH_LONG).show();
        }
    }

    // Getting user's location information
    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitudeValue = location.getLatitude();
        longitudeValue = location.getLongitude();

        Log.d("Lat : ", String.valueOf(latitudeValue));
        Log.d("Long : ", String.valueOf(longitudeValue));
        Log.d("Location : ", String.valueOf(location));

        addLocationToList();
        return location;
    }

    // This block will execute whenever location is changed
    @Override
    public void onLocationChanged(Location location) {
        latitudeValue = location.getLatitude();
        longitudeValue = location.getLongitude();
        addLocationToList();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private final IMyService.Stub myBinder = new IMyService.Stub() {
        public double getLatitude(){
            getLocation();
            double latitude = location.getLatitude();
            return latitude;
        }

        public double getLongitude(){
            getLocation();
            double longitude = location.getLongitude();
            return longitude;
        }

        // Calculating the distance of the user from the starting point to the current location
        public double getDistance(){
            dist = 0;

            double l1 = toRadians(latitude[0]);
            double l2 = toRadians(latitude[i-1]);
            double g1 = toRadians(longitude[0]);
            double g2 = toRadians(longitude[i-1]);

            dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
            if(dist < 0) {
                dist = dist + Math.PI;
            }
            finalDist = dist * 6378100;
            return finalDist;
        }

        // Calculating user's moving speed
        public double getSpeed(){
            speed = location.getSpeed();
            return speed;
        }
    };
}





