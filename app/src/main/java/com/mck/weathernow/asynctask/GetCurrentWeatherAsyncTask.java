package com.mck.weathernow.asynctask;

import android.os.AsyncTask;

import com.mck.weathernow.model.CurrentWeatherData;
import com.mck.weathernow.service.OpenWeatherMapService;

/**
 * AsyncTask to get the current weather. requires the call, lat and long
 * to start.
 * Created by Michael on 5/16/2016.
 */
public class GetCurrentWeatherAsyncTask extends AsyncTask<Object,Integer,CurrentWeatherData>{
    public interface callback {
        void onCurrentWeatherResult(CurrentWeatherData data);
    }

    GetCurrentWeatherAsyncTask.callback callback;

    @Override
    protected CurrentWeatherData doInBackground(Object... params) {
        callback = (GetCurrentWeatherAsyncTask.callback) params[0];
        return OpenWeatherMapService.instance().requestCurrentWeather((Double)params[1],(Double)params[2]);
    }

    @Override
    protected void onPostExecute(CurrentWeatherData data) {
        super.onPostExecute(data);
        if (!isCancelled()){
            callback.onCurrentWeatherResult(data);
        }
    }
}
