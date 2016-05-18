package com.mck.weathernow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST = 72; // for when making permissions
    private boolean needsDeniedPermission = false; // if was denied a necessary permission.
    private CurrentWeatherFragment currentWeatherFragment;
    private ForecastWeatherFragment forecastWeatherFragment;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // if there is no saved instance state
        if (savedInstanceState == null) {
            currentWeatherFragment = new CurrentWeatherFragment();
            forecastWeatherFragment = new ForecastWeatherFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.current_weather_fragment_container, currentWeatherFragment)
                    .add(R.id.forecast_weather_fragment_container, forecastWeatherFragment)
                    .commit();
        } else {
            currentWeatherFragment = (CurrentWeatherFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.current_weather_fragment_container);
            forecastWeatherFragment = (ForecastWeatherFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.forecast_weather_fragment_container);
        }
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
        if (needsDeniedPermission || !isNetworkConnection()) {
            showNeedsPermissionDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiClient.disconnect();
    }

    /*// no menu yet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /**
     * Checks to see if there is a network connection.
     * @return true if there is a network connection and is connected.
     */
    private boolean isNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //
    // GOOGLE PLAY SERVICES
    //

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            requestRequiredPermissions();
            return;
        }
        // update fragments with last location.
        lastLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        if (lastLocation != null) {
            currentWeatherFragment.updateLocation(lastLocation);
            forecastWeatherFragment.updateLocation(lastLocation);
        }

        // create location request instance
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setSmallestDisplacement(100);// in meters.
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // get a location settings builder
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        // get the settings and if not able to use location show dialog.
        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, settingsBuilder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                if (!status.isSuccess()) {
                    showNeedsPermissionDialog();
                }
            }
        });
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, MainActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        showNeedsPermissionDialog();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showNeedsPermissionDialog();
    }

    // PERMISSIONS

    private void requestRequiredPermissions() {
        // need internet and location permission
        String[] perms = new String[2];
        perms[0] = "android.permission.ACCESS_FINE_LOCATION";
        perms[1] = "android.permission.ACCESS_COARSE_LOCATION";
        ActivityCompat.requestPermissions(this, perms, MY_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            needsDeniedPermission = true;
        }
    }

    private void showNeedsPermissionDialog() {
        RequiresPermissionsDialogFragment dialog = new RequiresPermissionsDialogFragment();
        dialog.show(getSupportFragmentManager(), "Permissions");
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (lastLocation != null) {
            currentWeatherFragment.updateLocation(lastLocation);
            forecastWeatherFragment.updateLocation(lastLocation);
        }
    }
}
