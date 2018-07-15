package com.vovik.weatherforcast;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.vovik.weatherforcast.DarkSkyWeather.Daily;
import com.vovik.weatherforcast.DarkSkyWeather.DailyData;
import com.vovik.weatherforcast.DarkSkyWeather.Hourly;
import com.vovik.weatherforcast.DarkSkyWeather.HourlyData;
import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM  PlaceWeather;")
    List<PlaceWeather> selectPlaceWeather();

    @Query("Select * from Hourly where parentDayId = :parentDayId")
    Hourly selectHourly(long parentDayId);

    @Query("Select * from Daily where parentPlaceId = :parentId")
    Daily selectDaily(long parentId);

//    @Query("Select * from HourlyData where (time between :from and :to) and parentHourlyId = :parentHourlyId")
//    List<HourlyData> selectHourlyDataTimeRange(long parentHourly, long from, long to);
//
//    @Query("Select * from DailyData where (time between :from and :to) and parentDailyId = :parentDailyId")
//    List<DailyData> selectDailyDataTimeRange(long parentDailyId, long from, long to);

    @Query("Select * from HourlyData where parentDayId = :parentDayId")
    List<HourlyData> selectHourlyData(long parentDayId);

    @Query("Select * from DailyData where parentPlaceId = :parentPlaceId")
    List<DailyData> selectDailyData(long parentPlaceId);

//    @Update
//    void updatePlaceWeather(PlaceWeather data);
//
//    @Update
//    void updateDaily(Hourly data);
//
//    @Update
//    void updateHourlyData(HourlyData data);
//
//    @Update
//    void updateDaily(Daily data);
//
//    @Update
//    void updateDailyData(DailyData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPlaceWeather(PlaceWeather place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHourly(Hourly place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHourlyData(HourlyData place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDaily(Daily place);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDailyData(DailyData place);

    @Delete
    int deletePlaceWeather(PlaceWeather weather);

    @Query("Select Count(id) - 1 from DailyData where time < :time")
    long selectObsoleteDayCount(long time);

    @Query("Select Count(id) from DailyData")
    long selectDayCount();

    @Query("Select * from DailyData where time < :time")
    List<DailyData> selectObsoleteDays(long time);

    @Delete
    int deleteDailyEntry(DailyData day);


//    @Query("Select Count(id) - 1 from DailyData where time < :time")
//    long selectObsoleteHourCount(long time);
//
//    @Query("Select Count(id) from DailyData")
//    long selectHourCount();

    @Query("Select * from HourlyData where time < :time")
    List<HourlyData> selectObsoleteHours(long time);

    @Delete
    int deleteHourlyEntry(HourlyData day);


}
