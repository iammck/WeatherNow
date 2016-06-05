package com.mck.weathernow;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mck.weathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.weathernow.model.Period;
import com.mck.weathernow.model.WeatherNowData;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * holds a list of cards representing weather data. Before receiving
 * weather data, the adapter has a size one and shows only a
 *
 * There is a static list of cards in
 * a static order: Current, later today, tomorrow, the day after.
 * Created by Michael on 5/28/2016.
 */
public class WeatherNowAdapter extends RecyclerView.Adapter<WeatherNowAdapter.ViewHolder>{
    public static final String TAG = "WeNowAdapter";
    private static final Integer AM_REQ_ID = 1;
    private static final Integer PM_REQ_ID = 2;
    private Context context;
    private WeatherNowData weatherNowData;
    private static final int CURRENT_VIEW_TYPE = 0;
    private static final int LATER_TODAY_VIEW_TYPE = 1;
    private static final int FORECAST_VIEW_TYPE = 2;
    private static final int NO_DATA_VIEW_TYPE = 45;

    public class ViewHolder extends RecyclerView.ViewHolder implements GetWeatherIconAsyncTask.callback {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onWeatherIconResult(Bitmap icon, Integer requestId) {
            if (itemView != null){
                ImageView ivIcon = (ImageView) itemView.findViewById(R.id.ivWeatherIcon);
                if (ivIcon != null){
                    ivIcon.setImageBitmap(icon);
                    return;
                }
                if (requestId.equals(AM_REQ_ID)) {
                    ivIcon = (ImageView) itemView.findViewById(R.id.ivForecastAMIcon);
                    if (ivIcon != null) {
                        ivIcon.setImageBitmap(icon);
                        return;
                    }
                } else if (requestId.equals(PM_REQ_ID)) {
                    ivIcon = (ImageView) itemView.findViewById(R.id.ivForecastPMIcon);
                    if (ivIcon != null) {
                        ivIcon.setImageBitmap(icon);
                        return;
                    }
                }
            }
        }
        @Override
        public Context getContext() {
            return WeatherNowAdapter.this.context;
        }
    }

    public void setWeatherNowData(WeatherNowData data){
        weatherNowData = data;
        notifyDataSetChanged();
    }

    public WeatherNowAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getItemCount() {
        if (weatherNowData == null){
            return 1;
        }
        else {
            return 4;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (weatherNowData == null){
            return NO_DATA_VIEW_TYPE;
        }
        if (position == 0){
            return CURRENT_VIEW_TYPE;
        } else if (position == 1) {
            return LATER_TODAY_VIEW_TYPE;
        } else {
            return FORECAST_VIEW_TYPE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View result;
        switch (viewType){
            case CURRENT_VIEW_TYPE:
                result = LayoutInflater.from(context).inflate(R.layout.card_current_weather, parent, false);
                break;
            case LATER_TODAY_VIEW_TYPE:
                result = LayoutInflater.from(context).inflate(R.layout.card_later_today_weather, parent, false);
                break;
            case FORECAST_VIEW_TYPE:
                result = LayoutInflater.from(context).inflate(R.layout.card_forecast_weather, parent, false);
                break;
            default:
                result = LayoutInflater.from(context).inflate(R.layout.card_no_weather_data, parent, false);
        }
        return new ViewHolder(result);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (weatherNowData == null) return;
        switch (position){
            case 0:
                bindCurrentWeather(holder);
                break;
            case 1:
                bindLaterTodayWeather(holder);
                break;
            case 2:
            case 3:
                bindForecastWeather(holder, position);
                break;
        }
    }

    private void bindCurrentWeather(ViewHolder holder) {
        TextView tvCurLocName = (TextView) holder.itemView.findViewById(R.id.tvCurLocName);
        TextView tvCurTemp = (TextView) holder.itemView.findViewById(R.id.tvCurTemp);
        TextView tvCurDesc = (TextView) holder.itemView.findViewById(R.id.tvCurDescription);
        TextView tvDate = (TextView) holder.itemView.findViewById(R.id.tvDate);

        tvCurLocName.setText(weatherNowData.currentWeatherData.name);
        String curTemp = String.valueOf(weatherNowData.currentWeatherData.main.temp.intValue()) +
                "\u00b0";
        tvCurTemp.setText(curTemp);
        tvCurDesc.setText(weatherNowData.currentWeatherData.weather[0].description);
        new GetWeatherIconAsyncTask().execute(holder, weatherNowData.currentWeatherData.weather[0].icon);
        tvDate.setText(getDate(weatherNowData.currentWeatherData.dt));

    }

    private void bindLaterTodayWeather(ViewHolder holder) {
        TextView tvLater3HTime = (TextView) holder.itemView.findViewById(R.id.tvLater3HTime);
        TextView tvLater3HTemp = (TextView) holder.itemView.findViewById(R.id.tvLater3HTemp);
        TextView tvLater6HTime = (TextView) holder.itemView.findViewById(R.id.tvLater6HTime);
        TextView tvLater6HTemp = (TextView) holder.itemView.findViewById(R.id.tvLater6HTemp);
        TextView tvLater9HTime = (TextView) holder.itemView.findViewById(R.id.tvLater9HTime);
        TextView tvLater9HTemp = (TextView) holder.itemView.findViewById(R.id.tvLater9HTemp);
        // periods are 3 hours
        Period period3H = getWeatherForecast(0);
        Period period6H = getWeatherForecast(1);
        Period period9H = getWeatherForecast(2);
        tvLater3HTime.setText(getPeriodHour(period3H));
        tvLater6HTime.setText(getPeriodHour(period6H));
        tvLater9HTime.setText(getPeriodHour(period9H));

        Log.v(TAG,getPeriodHour(getWeatherForecast(0)));
        Log.v(TAG,getPeriodHour(getWeatherForecast(1)));
        Log.v(TAG,getPeriodHour(getWeatherForecast(2)));
        Log.v(TAG,getPeriodHour(getWeatherForecast(3)));

        String temperature = (( Integer ) period3H.main.temp.intValue()).toString() + "\u00b0";
        tvLater3HTemp.setText(temperature);
        temperature = (( Integer ) period6H.main.temp.intValue()).toString() + "\u00b0";
        tvLater6HTemp.setText(temperature);
        temperature = (( Integer ) period9H.main.temp.intValue()).toString() + "\u00b0";
        tvLater9HTemp.setText(temperature);
    }

    private void bindForecastWeather(ViewHolder holder, int position) {
        int daysOut = position - 1; // minus the first two, zero initial index.
        Period amPeriod = getAMForecast(daysOut);
        Period pmPeriod = getPMForecast(daysOut);

        TextView tvDate = (TextView) holder.itemView.findViewById(R.id.tvDate);
        TextView tvForecastAMTemp = (TextView) holder.itemView.findViewById(R.id.tvForecastAMTemp);
        TextView tvForecastAMTime = (TextView) holder.itemView.findViewById(R.id.tvForecastAMTime);
        TextView tvForecastAMDesc = (TextView) holder.itemView.findViewById(R.id.tvForecastAMDesc);
        TextView tvForecastPMTemp = (TextView) holder.itemView.findViewById(R.id.tvForecastPMTemp);
        TextView tvForecastPMTime = (TextView) holder.itemView.findViewById(R.id.tvForecastPMTime);
        TextView tvForecastPMDesc = (TextView) holder.itemView.findViewById(R.id.tvForecastPMDesc);

        String date = getDate(amPeriod.dt);
        String amTemp = String.valueOf(amPeriod.main.temp.intValue()) + "\u00b0";
        String amTime = getHour(amPeriod.dt);
        String amDesc = amPeriod.weather[0].description;
        String pmTemp = String.valueOf(pmPeriod.main.temp.intValue()) + "\u00b0";
        String pmTime = getHour(pmPeriod.dt);
        String pmDesc = pmPeriod.weather[0].description;

        tvDate.setText(date);
        tvForecastAMTemp.setText(amTemp);
        tvForecastAMTime.setText(amTime);
        tvForecastAMDesc.setText(amDesc);

        tvForecastPMTemp.setText(pmTemp);
        tvForecastPMTime.setText(pmTime);
        tvForecastPMDesc.setText(pmDesc);


        new GetWeatherIconAsyncTask().execute(holder, amPeriod.weather[0].icon, AM_REQ_ID);
        new GetWeatherIconAsyncTask().execute(holder, pmPeriod.weather[0].icon, PM_REQ_ID);

    }

    private Period getAMForecast(int daysOut) {
        Period[] periods = weatherNowData.forecastWeatherData.list;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( periods[0].dt * 1000 );
        int currDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        for(Period period: periods){
            calendar.setTimeInMillis( period.dt * 1000 );
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth - currDayOfMonth == daysOut){
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                //hour = (hour == 0)? 12: hour;
                if (hour >= 9 && hour <= 11){
                    return period;
                }
            }
        }
        return null;
    }

    private Period getPMForecast(int daysOut) {
        Period[] periods = weatherNowData.forecastWeatherData.list;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( periods[0].dt * 1000 );
        int currDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        for(Period period: periods){
            calendar.setTimeInMillis( period.dt * 1000 );
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth - currDayOfMonth == daysOut){
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour > 12 && hour < 16){
                    return period;
                }
            }
        }
        return null;
    }

    private String getPeriodHour(Period period) {
        return getHour(period.dt);
    }

    public String getHour(Long time){
        StringBuilder resultTime = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( time * 1000 );
        int hour = calendar.get(Calendar.HOUR);
        hour = (hour == 0)? 12: hour;
        resultTime.append(hour);

        int amPM = calendar.get(Calendar.AM_PM);
        if (amPM == Calendar.AM) {
            resultTime.append(" AM");
        } else {
            resultTime.append(" PM");
        }
        return resultTime.toString();
    }

    private String getDate(Long time) {
        StringBuilder result = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( time * 1000 );
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String day = "";
        switch (dayOfWeek){
            case Calendar.MONDAY:
                day = "Monday";
                break;
            case Calendar.TUESDAY:
                day = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                day = "Wednesday";
                break;
            case Calendar.THURSDAY:
                day = "Thursday";
                break;
            case Calendar.FRIDAY:
                day = "Friday";
                break;
            case Calendar.SATURDAY:
                day = "Saturday";
                break;
            case Calendar.SUNDAY:
                day = "Sunday";
                break;
        }
        result.append(day);
        result.append(" ");
        result.append(month);
        result.append("/");
        result.append(dayOfMonth);

        return result.toString();
    }

    private Period getWeatherForecast(int periodNumber) {
        return weatherNowData.forecastWeatherData.list[periodNumber];
    }

}
