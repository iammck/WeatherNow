package com.mck.weathernow;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.weathernow.model.WeatherNowData;


/**
 * WeatherNowFragment houses the recyclerView which displays weather
 * information.
 */
public class WeatherNowFragment extends Fragment implements WeatherNowDataHelper.Callback {
    private static final Integer PERIODS = 24;
    private static final String TAG = "WeatherNowFragment";
    private WeatherNowDataHelper weatherNowDataHelper;
    private WeatherNowAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_now, container, false);

        // get the recyclerView from the inflated view
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.weather_now_recycler_view);

        Log.v(TAG, "recyclerView is " + recyclerView);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new WeatherNowAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return view;

    }

    public void onLocationUpdate(Location location){
        Log.v(TAG, "onLocationUpdate() " + location.toString());
        if (weatherNowDataHelper == null){
            weatherNowDataHelper = new WeatherNowDataHelper();
        }
        weatherNowDataHelper.requestWeatherNowData(
                this, location.getLatitude(), location.getLongitude(), PERIODS);
    }

    @Override
    public void onWeatherNowDataResult(WeatherNowData data) {
        Log.v(TAG, "onWeatherNowDataResult " + data.toString());
        adapter.setWeatherNowData(data);
    }
}
