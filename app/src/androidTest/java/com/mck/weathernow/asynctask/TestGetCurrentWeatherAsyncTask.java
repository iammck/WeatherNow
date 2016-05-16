package com.mck.weathernow.asynctask;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.mck.weathernow.model.CurrentWeatherData;

import org.junit.Test;

/**
 * Created by Michael on 5/16/2016.
 */
public class TestGetCurrentWeatherAsyncTask extends ApplicationTestCase<Application> {
    public TestGetCurrentWeatherAsyncTask() {
        super(Application.class);
    }

    public class Callback implements GetCurrentWeatherAsyncTask.callback {

        private CurrentWeatherData result;

        @Override
        public void onCurrentWeatherResult(CurrentWeatherData data) {
            result = data;
        }
    }

    public void testDoesReturnExpectedResult() throws Exception {
        // set up the MockOpenWeatherMapService
        MockOpenWeatherMapService mockService = new MockOpenWeatherMapService();
        CurrentWeatherData expectedResult = new CurrentWeatherData();
        mockService.setCurrentResult(expectedResult);
        MockOpenWeatherMapService.setup(mockService);
        // handle this as the callback
        Callback callback = new Callback();
        // need lat and long, any will do
        Double lat = 42.0;
        Double lon = -122.6;
        // create and execute
        GetCurrentWeatherAsyncTask task = new GetCurrentWeatherAsyncTask();
        task.execute(callback,lat, lon);
        // wait
        Thread.sleep(500);
        // assert it happened
        assertEquals("The result does not equal the expected. ",
                expectedResult, callback.result);
        mockService.tearDown();
    }

}