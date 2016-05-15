package com.mck.weathernow.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Michael on 5/14/2016.
 */
public class CurrentWeatherData {

    public static class Coord {
        public Double lat;
        public Double lon;
        public Coord(){};
    }

    public static class Weather {
        public Integer id;
        public String main;
        public String description;
        public String icon;
        public Weather(){};
    }

    public static class Main {
        public Double temp;
        public Double pressure;
        public Double humidity;
        public Double temp_min;
        public Double temp_max;
        public Double sea_level;
        public Double grnd_level;
        public Main(){};
    }

    public static class Wind {
        //wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
        //wind.deg Wind direction, degrees (meteorological)
        public Double speed;
        public Double deg;
        public Wind(){};
    }

    public static class Clouds {
        public Double all; // coverage as a percent.
        public Clouds(){};
    }

    public static class Rain {
        public Double threeHour; // Rain volume for the last 3 hours
        public Rain(){};
    }

    /**
     * Rain has a data parameter name that is illegal in java so need to change it.
     */
    public static class RainDeserializer implements JsonDeserializer<Rain>{
        @Override
        public Rain deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Rain result = new Rain(); // the json should look like "3h"=10.3
            result.threeHour = json.getAsJsonObject().get("3h").getAsDouble();

            return result;

        }
    }

    public static class Snow {
        public Double threeHour; // Snow volume for the last 3 hours
        public Snow(){};
    }

    /**
     * Snow has a data parameter name that is illegal in java so need to change it.
     */
    public static class SnowDeserializer implements JsonDeserializer<Snow>{
        @Override
        public Snow deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Snow result = new Snow(); // the json should look like "3h"=10.3
            result.threeHour = json.getAsJsonObject().get("3h").getAsDouble();

            return result;

        }
    }

    public static class Sys {
        public Long sunrise;
        public Long sunset;
        public String country;
        public Sys(){};
    }


    public Coord coord;
    public Weather[] weather;
    public Main main;
    public Wind wind;
    public Clouds clouds;
    public Rain rain;
    public Snow snow;
    public Integer dt; // date in unix
    public Sys sys;
    public Integer id; // City id
    public String name; // City name
    public Integer cod; // should be
    public String message; // error message

    public CurrentWeatherData(){}



}
