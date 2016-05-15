# JP Morgan Chase weekend code challenge.

This project is a weekend code challenge distributed by JP Morgan Chase.

## Challenge Description:

We would like you to make a weather application. The app should get the current GPS location of the device, make a web-service call to fetch the weather for that location (you can use any public API), parse the response and make a model, then display the relevant data on the UI.

It is up to the developer to decide how the data will be cached for the app (if necessary), but the app should work in portrait and landscape mode, have an intuitive UI following Android conventions, and the code should be documented and polished.

We would also like to see unit testing for the app. Feel free to use JUnit and Mockito or any open source software for mocking services. You should have full unit test coverage for the services in your app, as well as individual UI components. Before a build a user should be able to run a single script that performs all unit tests and reports results to the user.

Feel free to add any other features, like multi-day forecasts, weather of neighboring cities, or maybe even weather related news.

## Initial Considerations
### Basic use case

Users click the launcher icon and the application starts. The user is presented with the current weather conditions as well as with a list of forecasts for future days.

### Basic application flow and structures

* After the application is launched the MainActivity is started.
* The MainActivity checks for the necessary permissions and possibly starts a second activity to request any needed permissions.
* After permissions have been checked, MainActivity loads two fragments, CurrentWeatherFragment and ForecastWeatherFragment.
* CurrentWeatherFragment starts a CurrentWeatherRetrieverAsyncTask to acquire its CurrentWeatherData.
* ForecastWeatherFragment starts a ForecastWeatherRetrieverAsyncTask to acquire its ForecastWeatherData.
* CurrentWeatherRetrieverAsyncTask uses HttpURLConnection to retrieve current weather data from a weather service API.
* ForecastWeatherRetrieverAsyncTask uses HttpURLConnection to retrieve current weather data from a weather service API.
* CurrentWeatherFragment uses CurrentWeatherData to display current weather information to the user.
* ForecastWeatherFragment uses ForecastWeatherData to display forecast weather information to the user.

### Which weather API?

Having no experience with the different web-services for retrieving weather data, I started by comparing popular APIs. My main concerns for the API were documentation, price structure and features. Of the many varied possibilities, I seriously considered APIs from Yahoo Weather, National Oceanic and Atmospheric Administration (NOAA), OpenWeatherMap and Weather Underground.

Yahoo Weather has good pricing, but could not accept geo coordinates. NOAA has the best pricing, but results are in XML and I want something that returns json. OpenWeatherMap and WeatherUnderground both accept geo coordinates and return json objects, but their free tiers have low calls per a minute rate and price structure that gets expensive quickly. Still, results from WeatherUnderground are ripe with data and they have good documentation. OpenWeatherMap also has good documentation and even though the resulting data is not as robust as WeatherUnderground, getting the data from json to java objects will be technically easier. OpenWeatherMap also has a nice set of weather icons. As a result of all this I have decided to go with OpenWeatherMap.

##### How to use OpenWeatherMap API**

OpenWeatherMap requires an API key to make a request. For example to get the current weather conditions call: <code>http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&APPID=myKey</code><br>
uses myKey to request weather data for lat, long values of 35,139.

    Current Weather:
    http://api.openweathermap.org/data/2.5/weather?lat=35&lon=122&APPID=myKey
    Forecast for two days:
    http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&cnt=3&APPID=myKey


### Acquiring current and forecast weather conditions

Current and forecast weather data is aquired with the following urls. The number of resulting forecast periods as given is for three. An OpenWeatherMap period is three hours and there are eight periods in 24 hours. The period can be changed by changing the 3 in `cnt=3` to the desired value. There is a five day limit or 5*24/3 = 40 periods.

##### Typical current weather API call and response**

A call to <code>http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139</code> results in:

    {
        "coord":{"lon":138.93,"lat":34.97},
        "weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"02n"}],
        "base":"cmc stations",
        "main":{
            "temp":289.517,
            "pressure":1024.53,
            "humidity":100,
            "temp_min":289.517,
            "temp_max":289.517,
            "sea_level":1034.05,
            "grnd_level":1024.53
            },
        "wind":{"speed":1.88,"deg":273.507},
        "clouds":{"all":8},
        "dt":1463160491,
        "sys":{"message":0.003,"country":"JP","sunrise":1463082110,"sunset":1463132392},
        "id":1851632,
        "name":"Shuzenji","cod":200
    }

##### Typical forecast weather API call and response

To get the forecast over three hour periods, a call to <code>http://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&cnt=3</code> will result in:

    {
        "city":{
            "id":1851632,
            "name":"Shuzenji",
            "coord":{
                "lon":138.933334,
                "lat":34.966671
            },
            "country":"JP",
            "population":0,
            "sys":{"population":0}
        },
        "cod":"200",
        "message":0.0038,
        "cnt":3,
        "list":[{
                "dt":1463173200,
                "main":{
                    "temp":286.79,
                    "temp_min":284.563,
                    "temp_max":286.79,
                    "pressure":939.66,
                    "sea_level":1035.55,
                    "grnd_level":939.66,
                    "humidity":86,
                    "temp_kf":2.23
                },
                "weather":[{
                    "id":800,
                    "main":"Clear",
                    "description":"clear sky",
                    "icon":"01n"
                }],
                "clouds":{"all":0},
                "wind":{"speed":0.79,"deg":343},
                "sys":{"pod":"n"},
                "dt_txt":"2016-05-13 21:00:00"
        },{
                "dt":1463184000,
                "main":{
                    "temp":295.54,
                    "temp_min":293.429,
                    "temp_max":295.54,
                    "pressure":940.42,
                    "sea_level":1035.6,
                    "grnd_level":940.42,
                    "humidity":56,
                    "temp_kf":2.11
                },
                "weather":[{
                    "id":802,
                    "main":"Clouds",
                    "description":"scattered clouds",
                    "icon":"03d"
                }],
                "clouds":{"all":32},
                "wind":{"speed":0.91,"deg":86.5083},
                "sys":{"pod":"d"},
                "dt_txt":"2016-05-14 00:00:00"
        },{
                "dt":1463194800,
                "main":{
                    "temp":295.35,
                    "temp_min":293.354,
                    "temp_max":295.35,
                    "pressure":940.04,
                    "sea_level":1035.22,
                    "grnd_level":940.04,
                    "humidity":56,
                    "temp_kf":1.99
                },
                "weather":[{
                    "id":500,
                    "main":"Rain",
                    "description":"light rain",
                    "icon":"10d"}],
                "clouds":{"all":92},
                "wind":{"speed":0.89,"deg":96.5013},
                "rain":{"3h":0.035},
                "sys":{"pod":"d"},
                "dt_txt":"2016-05-14 03:00:00"
}]
    }

##### Error results

There may be a possibility of an error result.

    Errorneous or null gps in Url
    {"cod":"404","message":"Error: Not found city"}
    Erroneous API key
    {"cod":401, "message": "Invalid API key. Please see http://openweathermap.org/faq#error401 for more info."}
    Notice the cod is the first is a String and a Integer in the second

I am unable to find a complete list of error codes. A successful current weather result has a `"cod":200` value and a forecast has a value of "cod":"200""`, implying any other value is an error response. Although it would be nice to handle error responses differently.

#### Getting Data objects from service call results.

Results are in json and have many nested objects and each has an array. I shall define the java class for these as static inner from within CurrentWeatherData or ForecastWeatherData classes as appropriate. Both Rain and Snow data have a illegally named parameter of '3h'. These two classes have custom deserializers.

##### CurrentWeatherData Parameters

Show link here to


##### ForecastWeatherData Parameters

Show Link here

##### Testing the data
The application will need to be able to take a json string representing either current or forecast data (cod 200 or "200") and turn it into the appropriate object instance. The resulting instance should have the correct data.

The application will need to be able to take a json string representing an unsuccessful weather data retrieval (any cod other than 200 or "200") and turn it into an error result instance object.

#### Making a network service call to OpenWeatherMap.




### CurrentWeatherRetrieverAsyncTask

CurrentWeatherRetrieverAsyncTask is used to retrieve the current weather conditions and returns the retrieved data in a CurrentWeatherData object.



b

b

b

b