package android.kuva.com.recomsearch;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by KuVa on 21.05.2015.
 */
public class Locator
{
    private static Locator locator;

    private static final String TAG ="MyLogs";

    private static LocationManager locationManager;
    private Context context;
    private static Location currentLocation;
    private static LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {


            if (location.getProvider().equals(LocationManager.GPS_PROVIDER))
            {
                currentLocation = location;
                return;
            }
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
                    currentLocation = location;

            Log.d(TAG, "Location changed " + location.getProvider() + " " +currentLocation.getLatitude() + " " + currentLocation.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }
    };

    public Location getCurrentLocation()
    {
        return currentLocation;
    }

    public static Locator getInstance(Context context)
    {
        if(locator==null)
            locator = new Locator(context);

        return locator;
    }

    public static Locator getInstance()
    {
        return locator;
    }

    private Locator(Context context )
    {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 3, 5, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 3, 5, locationListener);

    }






}
