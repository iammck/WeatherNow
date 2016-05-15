package com.mck.weathernow.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Michael on 5/15/2016.
 */
public class ForecastWeatherData {
    public static class City {
        Long id;
        String name;
        Coord coord;
        String country;
        public City(){}
    }

    public static class Coord {
        Double lat;
        Double lon;
        public Coord(){}
    }

    public static class Main {
        Double temp;
        Double temp_min;
        Double temp_max;
        Double pressure;
        Double sea_level;
        Double grnd_level;
        Double humidity;
        public Main(){}
    }

    public static class Weather {
        Integer id;
        String main;
        String description;
        String icon;
        public Weather(){}
    }


    public static class Wind {
        //wind.speed Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
        //wind.deg Wind direction, degrees (meteorological)
        Double speed;
        Double deg;
        public Wind(){}
    }

    public static class Clouds {
        Double all; // coverage as a percent.
        public Clouds(){}
    }

    public static class Rain {
        Double threeHour; // Rain volume for the last 3 hours
        public Rain(){}
    }

    /**
     * Rain has a data parameter name that is illegal in java so need to change it.
     */
    public static class RainDeserializer implements JsonDeserializer<Rain> {
        @Override
        public Rain deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Rain result = new Rain(); // the json should look like "3h"=10.3
            result.threeHour = json.getAsJsonObject().get("3h").getAsDouble();

            return result;

        }
    }

    public static class Snow {
        Double threeHour; // Snow volume for the last 3 hours
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

    public static class Period {
        Integer dt;
        Main main;
        Weather[] weather;
        Clouds clouds;
        Wind wind;
        Rain rain;
        Snow snow;
        public Period(){}
    }


    City city;
    String cod;
    String message;
    Integer cnt;
    Period[] list;

    public ForecastWeatherData(){

    }
}
