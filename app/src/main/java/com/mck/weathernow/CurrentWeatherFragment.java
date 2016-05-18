package com.mck.weathernow;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mck.weathernow.asynctask.GetCurrentWeatherAsyncTask;
import com.mck.weathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.weathernow.model.CurrentWeatherData;

/**
 * Displays the current area, weather, its description, and an icon.
 * Created by Michael on 5/16/2016.
 */
public class CurrentWeatherFragment extends Fragment
        implements GetCurrentWeatherAsyncTask.callback, GetWeatherIconAsyncTask.callback{

    private GetCurrentWeatherAsyncTask getCurrentWeatherAsyncTask;
    private GetWeatherIconAsyncTask getWeatherIconAsyncTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_weather_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelAsyncTasks();
    }

    public void updateLocation(Location lastLocation) {
        cancelAsyncTasks();
        getCurrentWeatherAsyncTask = new GetCurrentWeatherAsyncTask();
        getCurrentWeatherAsyncTask.execute(
                this, lastLocation.getLatitude(), lastLocation.getLongitude());

    }

    private void cancelAsyncTasks() {
        if (getCurrentWeatherAsyncTask != null){
            getCurrentWeatherAsyncTask.cancel(false);
            getCurrentWeatherAsyncTask = null;
        }
        if (getWeatherIconAsyncTask != null){
            getWeatherIconAsyncTask.cancel(false);
            getWeatherIconAsyncTask = null;
        }
    }

    @Override
    public void onCurrentWeatherResult(CurrentWeatherData data) {
        Log.v("Current", "current data result " + data);
        if(data == null || data.cod != 200){
            // TODO Handle error result from service.
            return;
        }
        final String DEGREE  = "\u00b0";
        View view = getView();
        if (view != null){
            TextView tvCurLocName = (TextView) view.findViewById(R.id.tvCurLocName);
            TextView tvCurTemp = (TextView) view.findViewById(R.id.tvCurTemp);
            TextView tvDesc = (TextView) view.findViewById(R.id.tvDescription);
            tvCurLocName.setText(String.valueOf(data.name));
            tvCurTemp.setText(String.valueOf(data.main.temp.intValue()) + DEGREE);
            tvDesc.setText(String.valueOf(data.weather[0].description));
            new GetWeatherIconAsyncTask().execute(this, data.weather[0].icon);
        }
    }

    @Override
    public void onWeatherIconResult(Bitmap icon, Integer requestId) {
        View view = getView();
        if (view != null){
            ImageView ivIcon = (ImageView) view.findViewById(R.id.ivWeatherIcon);
            ivIcon.setImageBitmap(icon);
        }
    }
}
