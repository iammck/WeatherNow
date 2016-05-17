package com.mck.weathernow;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Michael on 5/16/2016.
 */
public class ForecastWeatherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.forecast_weather_fragment, container, false);
        return result;
    }

    public void updateLocation(Location lastLocation) {

    }
}