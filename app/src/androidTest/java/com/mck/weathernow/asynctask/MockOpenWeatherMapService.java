package com.mck.weathernow.asynctask;

import android.content.Context;
import android.graphics.Bitmap;

import com.mck.weathernow.model.CurrentWeatherData;
import com.mck.weathernow.model.ForecastWeatherData;
import com.mck.weathernow.service.OpenWeatherMapService;

/**
 * Created by Michael on 5/16/2016.
 */
public class MockOpenWeatherMapService extends OpenWeatherMapService {
    private CurrentWeatherData currentResult;
    private ForecastWeatherData forecastResult;
    private Bitmap iconResult;

    public static void setup(MockOpenWeatherMapService service) {
        instance = service;
    }

    public static void tearDown(){
        instance = null;
    }

    public void setCurrentResult(CurrentWeatherData data) {
        currentResult = data;
    }
    public void setForecastResult(ForecastWeatherData data) {
        forecastResult = data;
    }
    public void setIconResult(Bitmap icon) {
        iconResult = icon;
    }

    @Override
    public CurrentWeatherData requestCurrentWeather(Double lat, Double lon) {
        return currentResult;
    }

    @Override
    public ForecastWeatherData requestForecastWeather(Double lat, Double lon, Integer periods) {
        return forecastResult;
    }

    @Override
    public Bitmap requestWeatherIcon(Context context, String iconId) {
        return iconResult;
    }

}