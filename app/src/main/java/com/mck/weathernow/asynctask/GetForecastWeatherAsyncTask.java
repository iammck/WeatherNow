package com.mck.weathernow.asynctask;

import android.os.AsyncTask;

import com.mck.weathernow.model.ForecastWeatherData;
import com.mck.weathernow.service.OpenWeatherMapService;

/**
 * Gets the forecast for the lat and lon over a period and returns the result to a callback.
 *
 * Created by Michael on 5/17/2016.
 */
public class GetForecastWeatherAsyncTask extends AsyncTask<Object,Integer,ForecastWeatherData> {
    public interface callback {
        void onForecastWeatherResult(ForecastWeatherData data);
    }

    GetForecastWeatherAsyncTask.callback callback;

    @Override
    protected ForecastWeatherData doInBackground(Object... params) {
        if (isCancelled()) return null;
        callback = (GetForecastWeatherAsyncTask.callback) params[0];
        return OpenWeatherMapService.instance().requestForecastWeather(
                (Double)params[1],(Double)params[2], (Integer) params[3]);
    }

    @Override
    protected void onPostExecute(ForecastWeatherData data) {
        super.onPostExecute(data);
        if (!isCancelled()){
            callback.onForecastWeatherResult(data);
        }
    }
}
