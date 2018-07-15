package com.vovik.weatherforcast;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.vovik.weatherforcast.DarkSkyWeather.Daily;
import com.vovik.weatherforcast.DarkSkyWeather.DailyData;
import com.vovik.weatherforcast.DarkSkyWeather.Hourly;
import com.vovik.weatherforcast.DarkSkyWeather.HourlyData;
import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

/**
 * Created by vovik on 11.07.2018.
 */
@Database(entities = {
        PlaceWeather.class,
        Hourly.class, HourlyData.class,
        Daily.class, DailyData.class}, version = 4)
public abstract class DatabaseWeather extends RoomDatabase{
    public abstract WeatherDao weatherDao();
}
