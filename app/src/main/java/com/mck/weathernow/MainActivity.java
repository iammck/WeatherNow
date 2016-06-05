package com.mck.weathernow;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mck.weathernow.dialog.LocationSettingsFailureDialogFragment;
import com.mck.weathernow.dialog.RequiresPermissionsDialogFragment;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";
    // looking for location with great accuracy,
    private static final float GREAT_ACCURACY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // if there is no saved instance state
        if (savedInstanceState == null) {
            // set up the weather fragment
            WeatherNowFragment weatherNowFragment = new WeatherNowFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.weather_fragment_container, weatherNowFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationHelperFragment locFrag = (LocationHelperFragment)
                getSupportFragmentManager().findFragmentByTag(LocationHelperFragment.TAG);
        if (locFrag == null) {
            locFrag = new LocationHelperFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(locFrag, LocationHelperFragment.TAG)
                    .commit();
        }
    }

    public void onLocationUpdate(Location location) {
        Log.v(TAG, "onLocationUpdate " + location.toString());
        // if the accuracy is great, remove location helper fragment.
        if (location.getAccuracy() < GREAT_ACCURACY){
            WeatherNowFragment weatherNowFragment = (WeatherNowFragment)
                    getSupportFragmentManager().findFragmentById(R.id.weather_fragment_container);
            weatherNowFragment.onLocationUpdate(location);
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentByTag(LocationHelperFragment.TAG))
                    .commitAllowingStateLoss();
        }
    }

    public void onPermissionsDenied() {
        RequiresPermissionsDialogFragment dialog = new RequiresPermissionsDialogFragment();
        dialog.show(getSupportFragmentManager(), "Permissions");
    }

    public void onLocationsSettingsFailure() {
        LocationSettingsFailureDialogFragment dialog = new LocationSettingsFailureDialogFragment();
        dialog.show(getSupportFragmentManager(), "Permissions");
    }

}
