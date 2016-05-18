package com.mck.weathernow.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mck.weathernow.Constants;
import com.mck.weathernow.model.CurrentWeatherData;
import com.mck.weathernow.model.ForecastWeatherData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    protected static final String units = "imperial";

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

        if (lat == null || lon == null) return null;

        try {
            String request = String.format(Locale.US,
                    "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=%s&APPID=%s",
                    lat, lon,units, Constants.API_ID);
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
                    "http://api.openweathermap.org/data/2.5/forecast?lat=%f&lon=%f&cnt=%d&units=%s&APPID=%s",
                    lat, lon,periods,units, Constants.API_ID);
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

    public Bitmap requestWeatherIcon(Context context, String iconId){
        if (context == null) return null;
        if (!fileExists(context, iconId)){
            getAndSaveIconFromNetwork(context, iconId);
            if (!fileExists(context, iconId)){
                return getAndReturnIconFromNetwork(iconId);
            }
        } else {
            Log.v("cache", "alrea exists.using file..." + iconId);
        }

        return BitmapFactory.decodeFile(context.getFilesDir().getPath() + "/icon" + iconId + ".png" );
    }

    private boolean fileExists(Context context, String iconId) {
        File file = new File(context.getFilesDir(), "icon" + iconId + ".png");
        if(file.exists())
            return true;
        else
            return false;
    }

    private Bitmap getAndReturnIconFromNetwork(String id) {
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

    public void getAndSaveIconFromNetwork(Context context, String iconId){
        try {
            // get the image and save as a file.
            String request = String.format( Locale.US,
                    "http://openweathermap.org/img/w/%s.png", iconId);
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
            if (connection.getResponseCode() == 200){
                // creating the input stream from icon
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                File file = new File(context.getFilesDir(), "icon" + iconId + ".png");
                // my local file writer, output stream
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                // until end of data, keep writing to file.
                int i;
                while ((i = in.read()) != -1){
                    out.write(i);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
