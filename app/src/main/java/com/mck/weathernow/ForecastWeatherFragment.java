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

import com.mck.weathernow.asynctask.GetForecastWeatherAsyncTask;
import com.mck.weathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.weathernow.model.ForecastWeatherData;

import java.util.Hashtable;

/**
 * Responsible for showing the forecast for later today, tomorrow and the day after to the user.
 * Created by Michael on 5/16/2016.
 */
public class ForecastWeatherFragment extends Fragment
        implements GetForecastWeatherAsyncTask.callback, GetWeatherIconAsyncTask.callback{

    private GetForecastWeatherAsyncTask getForecastWeatherAsyncTask;
    private final Hashtable<Integer,GetWeatherIconAsyncTask> getWeatherIconAsyncTasks = new Hashtable<>();
    final String DEGREE  = "\u00b0";
    // values for data to be displayed.
    private Double afterTemp;
    private String afterDesc;
    private String afterIcon;
    private Double tomTemp;
    private String tomDesc;
    private String tomIcon;
    private Double laterTemp;
    private String laterDesc;
    private String laterIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forecast_weather_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelAsyncTasks();
    }

    public void updateLocation(Location lastLocation) {
        Log.v("Forecast", "location change " + lastLocation);
        cancelAsyncTasks();
        getForecastWeatherAsyncTask = new GetForecastWeatherAsyncTask();
        getForecastWeatherAsyncTask.execute(
                this, lastLocation.getLatitude(), lastLocation.getLongitude(), 16);
    }

    private void cancelAsyncTasks() {
        if (getForecastWeatherAsyncTask != null){
            getForecastWeatherAsyncTask.cancel(false);
            getForecastWeatherAsyncTask = null;
        }
        synchronized (getWeatherIconAsyncTasks) {
            for (Integer key : getWeatherIconAsyncTasks.keySet()) {
                GetWeatherIconAsyncTask getWeatherIconAsyncTask =
                        getWeatherIconAsyncTasks.get(key);
                getWeatherIconAsyncTask.cancel(false);
            }
            getWeatherIconAsyncTasks.clear();
        }
    }


    @Override
    public void onWeatherIconResult(Bitmap icon, Integer requestId) {
        synchronized (getWeatherIconAsyncTasks){
            getWeatherIconAsyncTasks.remove(requestId);
        }
        View view = getView();
        if (view != null){
            int rId;
            switch (requestId){
                case (2):
                    rId = R.id.ivLaterIcon;
                    break;
                case (8):
                    rId = R.id.ivTomorrowIcon;
                    break;
                case (16):
                    rId = R.id.ivAfterTomorrowIcon;
                    break;
                default:
                    rId = -1;
            }
            if (rId != -1){
                ImageView ivIcon = (ImageView) view.findViewById(rId);
                ivIcon.setImageBitmap(icon);
            }
        }
    }

    @Override
    public void onForecastWeatherResult(ForecastWeatherData data) {
        if(data == null || !data.cod.equals("200")){
            // TODO Handle error result from service.
            return;
        }
        // get the list of periods
        ForecastWeatherData.Period[] periods = data.list;
        // if there are at least 16 periods, get after tomorrow
        if (periods.length >= 16){
            ForecastWeatherData.Period period = periods[15];
            afterTemp = period.main.temp;
            if (period.weather.length > 0) {
                afterDesc = period.weather[0].description;
                afterIcon = period.weather[0].icon;
            }
        }
        // if there are at least 8 periods, get after tomorrow
        if (periods.length >= 8){
            ForecastWeatherData.Period period = periods[7];
            tomTemp = period.main.temp;
            if (period.weather.length > 0) {
                tomDesc = period.weather[0].description;
                tomIcon = period.weather[0].icon;
            }
        }
        // if there are at least 2 periods, get after tomorrow
        if (periods.length >= 2){
            ForecastWeatherData.Period period = periods[1];
            laterTemp = period.main.temp;
            if (period.weather.length > 0) {
                laterDesc = period.weather[0].description;
                laterIcon = period.weather[0].icon;
            }
        }
        // get the views and either show them with data or hide them. Then get icons.
        View view = getView();
        if (view != null){
            TextView tvTomTemp = (TextView) view.findViewById(R.id.tvTomorrowTemp);
            TextView tvTomDesc = (TextView) view.findViewById(R.id.tvTomorrowDescription);
            TextView tvLaterTemp = (TextView) view.findViewById(R.id.tvLaterTemp);
            TextView tvLaterDesc = (TextView) view.findViewById(R.id.tvLaterDescription);
            TextView tvAfterTemp = (TextView) view.findViewById(R.id.tvAfterTomorrowTemp);
            TextView tvAfterDesc = (TextView) view.findViewById(R.id.tvAfterTomorrowDescription);
            if (tomTemp != null){
                tvTomTemp.setVisibility(View.VISIBLE);
                tvTomTemp.setText(tomTemp.toString());
            } else {
                tvTomTemp.setVisibility(View.GONE);
            }
            if (tomDesc != null){
                tvTomDesc.setVisibility(View.VISIBLE);
                tvTomDesc.setText(tomDesc);
            } else {
                tvTomDesc.setVisibility(View.GONE);
            }
            if (laterTemp != null){
                tvLaterTemp.setVisibility(View.VISIBLE);
                tvLaterTemp.setText(laterTemp.toString());
            } else {
                tvLaterTemp.setVisibility(View.GONE);
            }
            if (laterDesc != null){
                tvLaterDesc.setVisibility(View.VISIBLE);
                tvLaterDesc.setText(laterDesc);
            } else {
                tvLaterDesc.setVisibility(View.GONE);
            }
            if (afterTemp != null){
                tvAfterTemp.setVisibility(View.VISIBLE);
                tvAfterTemp.setText(afterTemp.toString());
            } else {
                tvAfterTemp.setVisibility(View.GONE);
            }
            if (afterDesc != null){
                tvAfterDesc.setVisibility(View.VISIBLE);
                tvAfterDesc.setText(afterDesc);
            } else {
                tvAfterDesc.setVisibility(View.GONE);
            }
            // get the icons for tom later after, with keys 8, 2, 16
            cancelAsyncTasks();
            if (tomIcon != null){
                Integer key = 8;
                GetWeatherIconAsyncTask value = new GetWeatherIconAsyncTask();
                synchronized (getWeatherIconAsyncTasks) {
                    getWeatherIconAsyncTasks.put(key, value);
                }
                value.execute(this, tomIcon, key);
            }
            if (tomIcon != null){
                Integer key = 2;
                GetWeatherIconAsyncTask value = new GetWeatherIconAsyncTask();
                synchronized (getWeatherIconAsyncTasks) {
                    getWeatherIconAsyncTasks.put(key, value);
                }
                value.execute(this, laterIcon, key);
            }
            if (laterIcon != null){
                Integer key = 16;
                GetWeatherIconAsyncTask value = new GetWeatherIconAsyncTask();
                synchronized (getWeatherIconAsyncTasks) {
                    getWeatherIconAsyncTasks.put(key, value);
                }
                value.execute(this, tomIcon, key);
            }
        }
    }
}