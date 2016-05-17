package com.mck.weathernow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int MY_PERMISSIONS_REQUEST = 72;
    private boolean needsDeniedPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasRequiredPermissions()){
            requestRequiredPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasRequiredPermissions()){
            maybeShowRequestPermissions();
        }
    }

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
    }

    private void requestRequiredPermissions() {
        // need internet and location permission
        String[] perms = new String[2];
        perms[0] = "android.permission.ACCESS_FINE_LOCATION";
        perms[1] = "android.permission.INTERNET";
        ActivityCompat.requestPermissions(this, perms, MY_PERMISSIONS_REQUEST);
    }

    private boolean maybeShowRequestPermissions() {
        if (needsDeniedPermission ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            showNeedsPermissionDialog();
            return true;
        }
        return false;
    }

    public boolean hasRequiredPermissions() {
        final int curApiVersion = Build.VERSION.SDK_INT;
        if (curApiVersion < Build.VERSION_CODES.M){
            return true;
        }
        if (ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
            ContextCompat.checkSelfPermission(
                this,Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED ){
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission){
            needsDeniedPermission = true;
        }
    }

    private void showNeedsPermissionDialog() {
        RequiresPermissionsDialogFragment dialog = new RequiresPermissionsDialogFragment();
        dialog.show(getSupportFragmentManager(), "Permissions");
    }
}
