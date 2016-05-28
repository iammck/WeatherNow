package com.mck.weathernow;

import com.mck.weathernow.asynctask.MockOpenWeatherMapService;
import com.mck.weathernow.model.CurrentWeatherData;
import com.mck.weathernow.model.ForecastWeatherData;
import com.mck.weathernow.model.WeatherNowData;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test to be sure that one can use the helper to get current weather data
 *
 * Created by Michael on 5/28/2016.
 */
public class WeatherNowDataHelperTest {

    public class Callback implements WeatherNowDataHelper.Callback {
        private WeatherNowData result;
        @Override
        public void onWeatherNowDataResult(WeatherNowData data) {
            result = data;
        }
    }

    @Test
    public void testRequestWeatherNowData() throws Exception {
        // setup a MockOpenWeatherMapService to return expected results.
        // set up the MockOpenWeatherMapService
        MockOpenWeatherMapService mockService = new MockOpenWeatherMapService();
        CurrentWeatherData expectedCurrentResult = new CurrentWeatherData();
        mockService.setCurrentResult(expectedCurrentResult);
        ForecastWeatherData expectedForecastResult = new ForecastWeatherData();
        mockService.setForecastResult(expectedForecastResult);
        MockOpenWeatherMapService.setup(mockService);

        // handle this as the Callback
        Callback callback = new Callback();
        // need lat and long, any will do
        Double lat = 42.0;
        Double lon = -122.6;
        Integer periods = 3;

        // create and execute
        WeatherNowDataHelper helper = new WeatherNowDataHelper();
        helper.requestWeatherNowData(callback, lat, lon, periods);
        // wait
        Thread.sleep(500);
        // assert it happened
        assertEquals("The result does not equal the expected. ",
                expectedCurrentResult, callback.result.currentWeatherData);
        assertEquals("The result does not equal the expected. ",
                expectedForecastResult, callback.result.forecastWeatherData);
        MockOpenWeatherMapService.tearDown();
    }

    @Test
    public void testCancelRequestWeatherNowData() throws Exception {
        // setup a MockOpenWeatherMapService to return expected results.
        // set up the MockOpenWeatherMapService
        MockOpenWeatherMapService mockService = new MockOpenWeatherMapService();
        CurrentWeatherData expectedCurrentResult = new CurrentWeatherData();
        mockService.setCurrentResult(expectedCurrentResult);
        ForecastWeatherData expectedForecastResult = new ForecastWeatherData();
        mockService.setForecastResult(expectedForecastResult);
        mockService.setSleepTime(700); // sleep for .7 second.
        MockOpenWeatherMapService.setup(mockService);

        // handle this as the Callback
        Callback callback = new Callback();
        // need lat and long, any will do
        Double lat = 42.0;
        Double lon = -122.6;
        Integer periods = 3;

        // create, execute and cancel
        WeatherNowDataHelper helper = new WeatherNowDataHelper();
        helper.requestWeatherNowData(callback, lat, lon, periods);
        helper.cancel();
        Thread.sleep(1000); // sleep for one second.
        // assert helper was canceled
        assertNull("Should not have callback result", callback.result);

        MockOpenWeatherMapService.tearDown();
    }
}