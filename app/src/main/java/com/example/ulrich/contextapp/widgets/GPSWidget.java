package com.example.ulrich.contextapp.widgets;

/**
 * Created by Anders on 13-12-2017.
 */


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;


public class GPSWidget implements LocationListener  {

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;
    private final static boolean forceNetwork = false;


    private LocationManager locationManager;
    public Location location;
    public Location lastLocation;
    public double longitude;
    public double latitude;
    public double currentSpeed = 0;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean locationServiceAvailable;


    public GPSWidget( Context context )     {

        initLocationService(context);
        Log.d("GPSWIDGET","LocationService createdd");
    }

    /**
     * Sets up location service after permissions is granted
     */
    private void initLocationService(Context context) {


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        try   {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
            }
            else
            {
                this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null)   {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }
                }

                if (isGPSEnabled)  {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)  {
                        String bestProvider = String.valueOf(locationManager.getBestProvider(new Criteria(), true)).toString();

                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            Log.e("TAG", "GPS is on");
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            lastLocation = location;

                        }
                        else{
                            // If location is null: request a location
                            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                        }
                    }
                }
            }
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location)     {

        double kph = 0;
        if(lastLocation!=null)
        {
            float distanceMeters = location.distanceTo(lastLocation);
            long timeSeconds = (location.getTime() - lastLocation.getTime()) * 1000;
            kph = (distanceMeters) * 1000 / ( timeSeconds/3600.0f );
        }
        currentSpeed = kph;
        Log.d("LOC CHANGED:", "Km/h: " + kph);
        lastLocation = location;
    }

    // Aggregators use this to get a current Speed update
    public double getCurrentSpeed(){return currentSpeed;}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}