package com.mck.weathernow;

import com.mck.weathernow.asynctask.GetCurrentWeatherAsyncTask;
import com.mck.weathernow.asynctask.GetForecastWeatherAsyncTask;
import com.mck.weathernow.model.CurrentWeatherData;
import com.mck.weathernow.model.ForecastWeatherData;
import com.mck.weathernow.model.WeatherNowData;

/**
 * Helper class for acquiring current and forecast weather data.
 * Implement the Callback and when data is ready it will be called
 * with the resulting WeatherNowData object.
 *
 * Created by Michael on 5/28/2016.
 */
public class WeatherNowDataHelper implements GetCurrentWeatherAsyncTask.Callback, GetForecastWeatherAsyncTask.Callback{
    private GetCurrentWeatherAsyncTask getCurrentWeatherAsyncTask;
    private GetForecastWeatherAsyncTask getForecastWeatherAsyncTask;
    WeatherNowData weatherNowDataResult;
    private Callback callback;
    private boolean canceled;

    interface Callback {
        void onWeatherNowDataResult(WeatherNowData data);
    }

    public void requestWeatherNowData(Callback callback, Double lat, Double lon, Integer periods){
        this.callback = callback;
        cancelAsyncTasks();
        canceled = false;
        getCurrentWeatherAsyncTask = new GetCurrentWeatherAsyncTask();
        getCurrentWeatherAsyncTask.execute(
                this, lat, lon);
        getForecastWeatherAsyncTask = new GetForecastWeatherAsyncTask();
        getForecastWeatherAsyncTask.execute(
                this, lat, lon, periods);

    }

    public void cancel(){
        canceled = true;
        cancelAsyncTasks();
    }

    private void cancelAsyncTasks() {
        synchronized (this) {
            if (getCurrentWeatherAsyncTask != null){
                getCurrentWeatherAsyncTask.cancel(false);
            }
            if (getForecastWeatherAsyncTask != null){
                getForecastWeatherAsyncTask.cancel(false);
            }
            if (weatherNowDataResult != null){
                weatherNowDataResult = null;
            }
        }
    }

    @Override
    public void onForecastWeatherResult(ForecastWeatherData data) {
        synchronized (this){
            if (weatherNowDataResult == null){
                weatherNowDataResult = new WeatherNowData();
            }
            weatherNowDataResult.forecastWeatherData = data;
            possiblyReturnResults();
        }
    }

    @Override
    public void onCurrentWeatherResult(CurrentWeatherData data) {
        synchronized (this){
            if (weatherNowDataResult == null){
                weatherNowDataResult = new WeatherNowData();
            }
            weatherNowDataResult.currentWeatherData = data;
            possiblyReturnResults();
        }
    }

    private void possiblyReturnResults() {
        // all the data is available, return it.
        if ( !canceled && (weatherNowDataResult != null) &&
                (weatherNowDataResult.currentWeatherData != null) &&
                (weatherNowDataResult.forecastWeatherData != null) &&
                (callback != null)){
            callback.onWeatherNowDataResult(weatherNowDataResult);
        }
    }

}
