package com.vovik.weatherforcast.DarkSkyWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vovik on 06.07.2018.
 */

public class PlaceWeather {

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longitude")
    @Expose
    private Double longitude;

    @SerializedName("timezone")
    @Expose
    private String timezone;

    @SerializedName("currently")
    @Expose
    private Currently currently;

    @SerializedName("minutely")
    @Expose
    private Minutely minutely;

    @SerializedName("hourly")
    @Expose
    private Hourly hourly;

    @SerializedName("daily")
    @Expose
    private Daily daily;

    /**
     * No args constructor for use in serialization
     *
     */
    public PlaceWeather() {
    }

    /**
     *
     * @param timezone
     * @param currently
     * @param longitude
     * @param latitude
     * @param hourly
     * @param daily
     * @param minutely
     */
    public PlaceWeather(Double latitude, Double longitude, String timezone, Currently currently, Minutely minutely, Hourly hourly, Daily daily) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.currently = currently;
        this.minutely = minutely;
        this.hourly = hourly;
        this.daily = daily;
    }

    public String getLocation(){ return location; }

    public void setLocation(String location){ this.location = location; }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Currently getCurrently() {
        return currently;
    }

    public void setCurrently(Currently currently) {
        this.currently = currently;
    }

    public Minutely getMinutely() {
        return minutely;
    }

    public void setMinutely(Minutely minutely) {
        this.minutely = minutely;
    }

    public Hourly getHourly() {
        return hourly;
    }

    public void setHourly(Hourly hourly) {
        this.hourly = hourly;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

}

/*
 import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
class PlaceWeather{

    long shift;
    double lattitude, longitude;

    String place, timezone;

    BaseActivity.WeatherData currentData;

    List<BaseActivity.WeatherData>
            hourlyData,
            dailyData;

    List<BaseActivity.MinuteWeatherData>
            minutelyData;

    static final int
            REQUIRE_MINUTELY = 1,
            REQUIRE_HOURLY = 2,
    //REQUIRE_DAILY = 3,
    //UPDATE_ERROR = -1,
    UPDATE_SUCCESS = 0;
    //UPDATE_REQUIRED = -2;

    int
            currentMinute,
            currentHour;
    //currentDay;

    PlaceWeather(){
        currentMinute = 0;
        currentHour = 0;
        //currentDay = 0;
    }

    void Update(long time){

        boolean
                minutelyUpdate = false,
                hourlyUpdate = false;

        if(minutelyData != null){
            for(; currentMinute < minutelyData.size() &&
                    minutelyData.get(currentMinute).time - time - shift > 0;
                ++currentMinute){minutelyUpdate = true;}

            if(minutelyUpdate && currentMinute < minutelyData.size()){
                BaseActivity.MinuteWeatherData minute = minutelyData.get(currentMinute);

                currentData.icon = minute.icon;
                currentData.precipIntensity = minute.precipIntensity;
                currentData.time = minute.time;
            }
        }


        if(minutelyData == null || (minutelyData != null && currentMinute >= minutelyData.size()))
            if(hourlyData != null) {
                for(; currentHour < hourlyData.size() &&
                        hourlyData.get(currentHour).time - time - shift > 0;
                    ++currentHour){hourlyUpdate = true;}

                if(hourlyUpdate && currentHour < hourlyData.size()){
                    BaseActivity.WeatherData hour = hourlyData.get(currentHour);

                    currentData.icon = hour.icon;
                    currentData.precipIntensity = hour.precipIntensity;
                    currentData.time = hour.time;
                    currentData.humidity = hour.humidity;
                    currentData.windSpeed = hour.windSpeed;
                    currentData.temperature = hour.temperature;
                }
            }

        if(dailyData != null && currentHour >= hourlyData.size()){
            currentData.sunriseTime = dailyData.get(0).sunriseTime;
            currentData.sunsetTime = dailyData.get(0).sunsetTime;
        }
    }

    int RequireUpload(){

        if(minutelyData != null && currentMinute >= minutelyData.size())
            return REQUIRE_MINUTELY;

        if(hourlyData != null && (currentHour >= hourlyData.size() || currentHour >= 24))
            return REQUIRE_HOURLY;

        return UPDATE_SUCCESS;
    }

    void LoadFromJson(JSONObject data){

        if(data == null){
            Log.d("null", "data was null");
            return;
        }
        try{

            if(data.has("timezone")){
                timezone = data.getString("timezone");
                shift = TimeZone.getTimeZone(timezone).getRawOffset() - TimeZone.getDefault().getRawOffset();
            }

            if(data.has("daily"))
                UpdateDailyJson(data.getJSONObject("daily"));

            if(data.has("hourly"))
                UpdateHourlyJson(data.getJSONObject("hourly"));

            if(data.has("minutely"))
                UpdateMinutelyJson(data.getJSONObject("minutely"));

            //load current
            if(data.has("currently")){
                JSONObject currently = data.getJSONObject("currently");

                currentData = new BaseActivity.WeatherData(currently);
            }

        }catch(JSONException e){
            Log.d("JSON", "invalid (full) response message");
        }
    }

    void UpdateDailyJson(JSONObject data){

        if(data == null){

            return;
        }
        try{
            if(data.has("data")){

                JSONArray array = data.getJSONArray("data");

                dailyData = new ArrayList<>(array.length());

                JSONObject current;
                for(int i = 0; i < array.length(); ++i){
                    current = array.getJSONObject(i);

                    if(i<dailyData.size()){

                        dailyData.get(i).loadFromJson(current);
                    }else{
                        dailyData.add(new BaseActivity.WeatherData(current));
                    }
                }

            }
        }catch(JSONException e){
            Log.d("JSON", "invalid (day) response message");
        }
    }

    void UpdateHourlyJson(JSONObject data){
        if(data == null) return;
        try{
            if(data.has("data")){

                JSONArray array = data.getJSONArray("data");
                hourlyData = new ArrayList<>(array.length());

                JSONObject current;
                for(int i = 0; i < array.length(); ++i){
                    current = array.getJSONObject(i);

                    if(i<hourlyData.size()){

                        hourlyData.get(i).loadFromJson(current);
                    }else{
                        hourlyData.add(new BaseActivity.WeatherData(current));
                    }
                }

            }


        }catch(JSONException e){
            Log.d("JSON", "invalid (hour) response message");
        }
    }

    void UpdateMinutelyJson(JSONObject data){

        Log.d("JSON", "entered minutely");
        if(data == null){
            Log.d("JSON", "null data minutely");
            return;
        }

        try{
            if(data.has("data")){

                JSONArray array = data.getJSONArray("data");
                minutelyData = new ArrayList<>(array.length());

                currentMinute = 0;

                JSONObject current;
                for(int i = 0; i < array.length(); ++i){
                    current = array.getJSONObject(i);

                    if(i<minutelyData.size()){
                        minutelyData.get(i).loadFromJson(current);
                    }else{
                        minutelyData.add(new BaseActivity.MinuteWeatherData(current));
                    }
                }

            }

        }catch(JSONException e){
            Log.d("JSON", "invalid (minute) response message");
        }
    }
}*/
