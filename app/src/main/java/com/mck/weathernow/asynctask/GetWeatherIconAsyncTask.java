package com.mck.weathernow.asynctask;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.mck.weathernow.service.OpenWeatherMapService;

/**
 * AsyncTask to get the current weather. requires the call, lat and long
 * to start.
 * Created by Michael on 5/16/2016.
 */
public class GetWeatherIconAsyncTask extends AsyncTask<Object,Integer,Bitmap>{
    public interface callback {
        void onWeatherIconResult(Bitmap icon);
    }

    GetWeatherIconAsyncTask.callback callback;

    @Override
    protected Bitmap doInBackground(Object... params) {
        callback = (GetWeatherIconAsyncTask.callback) params[0];
        String id = (String) params[1];
        return OpenWeatherMapService.instance().requestWeatherIcon(id);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (!isCancelled()){
            callback.onWeatherIconResult(bitmap);
        }
    }
}
