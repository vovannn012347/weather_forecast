package com.vovik.weatherforcast;

import android.support.annotation.Nullable;

import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by vovik on 05.07.2018.
 */

public interface DarkSkyApi {

    @GET("/forecast/{apiKey}/{lat},{long}")
    Call<PlaceWeather> getForecast(@Path("apiKey") String apiKey,
                               @Path("lat") double latitude,
                               @Path("long") double longitude,
                               @Nullable @QueryMap Map<String, String> options);

    @GET("/forecast/{apiKey}/{lat},{long}?exclude=hourly,daily,flags,alerts&lang=en&units=si")
    Call<PlaceWeather> getMinutely(@Path("apiKey") String apiKey,
                                     @Path("lat") double latitude,
                                     @Path("long") double longitude);


}

    /*
    @GET("/forecast/{apiKey}/{lat},{long},{time}")
    Call<PlaceWeather> getTimeMachineForecast(@Path("apiKey") String apiKey,
                                          @Path("lat") double latitude,
                                          @Path("long") double longitude,
                                          @Path("time") double timestamp,
                                         @Nullable @QueryMap Map<String, String> options);*/

//import android.util.Log;
//
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;

/*
public  class DarkSkyApi {

    private final static class Units{
        static final int
                AUTO = 1,
                CA = 2,
                UK2 = 3,
                US = 4,
                SI = 5;
    }

    private final static class ExcludeBlocks{
        static final String
                currently = "currently",
                minutely = "minutely",
                hourly = "hourly",
                daily = "daily",
                alerts = "alerts",
                flags = "flags";
    }

    private final static String language = "en";
    private final static String key = "c4f30e7ffd56f8a739fbf78c2ec2f22e";
    private final static String apiURL = "https://api.darksky.net/forecast";

    private static JSONObject GET(double longitude, double lattitude, String[] excludeBlocks, int units){

        StringBuilder request = new StringBuilder(apiURL + '/' + key + '/' + longitude +',' + lattitude + '?');

        boolean manyOptions = false;

        if(null != excludeBlocks && excludeBlocks.length > 0){
            request.append("exclude=");
            request.append(excludeBlocks[0]);

            for(int i = 1; i < excludeBlocks.length; ++i){
                request.append(',');
                request.append(excludeBlocks[i]);
            }
            manyOptions = true;
        }

        if(manyOptions)
            request.append('&');
        request.append("lang=");
        request.append(language);

        if(units != 0){
            request.append('&');
            request.append("units=");
            switch (units){
                case Units.CA:
                    request.append("ca");
                    break;
                case Units.UK2:
                    request.append("uk2");
                    break;
                case Units.US:
                    request.append("us");
                    break;
                case Units.SI:
                    request.append("si");
                    break;
                default:
                    request.append("auto");
            }
        }

        JSONObject result = null;
        String
                inputLine;

        try{

            URL url = new URL(request.toString());

            URLConnection con = url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) con;
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStreamReader
                    streamReader = new InputStreamReader(connection.getInputStream());

            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder resultString = new StringBuilder();

            while((inputLine = reader.readLine()) != null){
                resultString.append(inputLine);
                resultString.append('\n');
            }

            result = new JSONObject(resultString.toString());

        }catch(IOException e){
            Log.d("DarkSky", "inout exception: " + e.toString());
        }catch(Exception e){
            Log.d("DarkSky", "tried to upload: " + e.toString());
        }

        return result;
    }

    static JSONObject getWeatherData(double longitude, double lattitude){

        return GET(longitude, lattitude, new String[]{ ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
    }

    static JSONObject getDailyWeather(double longitude, double lattitude){

        return GET(longitude, lattitude, new String[]{ExcludeBlocks.currently, ExcludeBlocks.minutely, ExcludeBlocks.hourly, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
    }

    static JSONObject getMinutelyWeather(double longitude, double lattitude){

        return GET(longitude, lattitude, new String[]{ExcludeBlocks.currently, ExcludeBlocks.hourly, ExcludeBlocks.daily, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
    }

    static JSONObject getHourlyWeather(double longitude, double lattitude){

        return GET(longitude, lattitude, new String[]{ExcludeBlocks.currently, ExcludeBlocks.minutely, ExcludeBlocks.daily, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
    }

    public static JSONObject getCurrentWeather(double longitude, double lattitude){

        return GET(longitude, lattitude, new String[]{ExcludeBlocks.minutely, ExcludeBlocks.hourly, ExcludeBlocks.daily, ExcludeBlocks.flags, ExcludeBlocks.alerts }, Units.SI);
    }

}



*/