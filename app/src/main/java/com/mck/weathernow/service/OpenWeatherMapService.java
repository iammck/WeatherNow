package com.mck.weathernow.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mck.weathernow.Constants;
import com.mck.weathernow.model.CurrentWeatherData;
import com.mck.weathernow.model.ForecastWeatherData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


/**
 * Uses HttpURLConnection to connect with OpenWeatherMap API.
 *
 * Created by Michael on 5/15/2016.
 */
public class OpenWeatherMapService {
    protected static OpenWeatherMapService instance;
    protected static final String lockKey = "instance_lock";
    public static OpenWeatherMapService instance(){
        if (instance == null){
            synchronized (lockKey){
                if (instance == null){
                    instance = new OpenWeatherMapService();
                }
            }
        }
        return instance;
    }

    private static final String USER_AGENT = "Mozilla/5.0";

    /**
     * Uses HttpUrlConnection to get the current weather conditions
     * for the lat and lon.
     * @return the resulting CurrentWeatherData.
     */
    public CurrentWeatherData requestCurrentWeather(Double lat, Double lon){
        try {
            String request = String.format(Locale.US,
                    "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&APPID=%s",
                    lat, lon, Constants.API_ID);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(
                        CurrentWeatherData.Rain.class, new CurrentWeatherData.RainDeserializer())
                    .registerTypeAdapter(
                        CurrentWeatherData.Snow.class, new CurrentWeatherData.SnowDeserializer())
                    .create();
            return (gson.fromJson(response.toString(), CurrentWeatherData.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ForecastWeatherData requestForecastWeather(Double lat, Double lon,Integer periods){
        try {
            String request = String.format( Locale.US,
                    "http://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&cnt=%d&APPID=%s",
                    lat, lon,periods, Constants.API_ID);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(
                            ForecastWeatherData.Rain.class, new ForecastWeatherData.RainDeserializer())
                    .registerTypeAdapter(
                            ForecastWeatherData.Snow.class, new ForecastWeatherData.SnowDeserializer())
                    .create();
            return (gson.fromJson(response.toString(), ForecastWeatherData.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap requestWeatherIcon(String id){
        try {
            String request = String.format( Locale.US,
                    "http://openweathermap.org/img/w/%s.png", id);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            if (connection.getResponseCode() == 200){
                return BitmapFactory.decodeStream(connection.getInputStream());
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
