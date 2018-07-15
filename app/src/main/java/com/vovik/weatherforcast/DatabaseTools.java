package com.vovik.weatherforcast;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.vovik.weatherforcast.DarkSkyWeather.DailyData;
import com.vovik.weatherforcast.DarkSkyWeather.HourlyData;
import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTools {

    private DatabaseWeather databaseInstance;
    private Context context;

    DatabaseTools(Context context){

        this.context = context;

        databaseInstance =
                Room.databaseBuilder(context,
                        DatabaseWeather.class,
                        "WeatherDatabase.db")
                        .fallbackToDestructiveMigration()
                        .build();
    }


    protected void finalize(){ databaseInstance.close(); }

    List<PlaceWeather> loadFromDb(){
        WeatherDao dao = databaseInstance.weatherDao();

        List<PlaceWeather> ret = dao.selectPlaceWeather();

        for(int i = 0; i < ret.size(); ++i){

            //get hourly data object
            ret.get(i).setHourly(
                    dao.selectHourly(
                            ret.get(i).getId()
                    )
            );

            //get daily data object
            ret.get(i).setDaily(
                    dao.selectDaily(
                            ret.get(i).getId()
                    )
            );

            //get daily data
            ret.get(i).getDaily().setData(
                dao.selectDailyData(ret.get(i).getId())
            );

            ret.get(i).getHourly().setData(
                dao.selectHourlyData(
                        ret.get(i).getDaily().getData().get(0).getId()
                )
            );
        }

        return ret;
    }

    void updateFromDb(PlaceWeather weather){
        long now = Calendar.getInstance().getTimeInMillis()/1000;

        deleteObsoleteEntries(now);
        WeatherDao dao = databaseInstance.weatherDao();

        //update daily data
        weather.getDaily().setData(
                dao.selectDailyData(weather.getId())
        );

        //get hourly
        weather.setHourly(
                dao.selectHourly(weather.getId())
        );

        //get hourlydata
        weather.getHourly().setData(
                dao.selectHourlyData(
                        weather.getDaily()
                                .getData().get(0).getId()
                )
        );
    }

    //called on adding new fragment
    void getForecastInitially(DarkSkyApi api, PlaceWeather weather){

        long newWeatherPlaceId = databaseInstance.weatherDao().insertPlaceWeather(weather);

        weather.getDaily().setParentPlaceId(newWeatherPlaceId);
        databaseInstance.weatherDao().insertDaily(weather.getDaily());

        List<DailyData> dailyData = weather.getDaily().getData();

        for(int i = 0; i < dailyData.size(); ++i){
            dailyData.get(i).setParentPlaceId(newWeatherPlaceId);
            dailyData.get(i).setId(
                    databaseInstance.weatherDao().insertDailyData(dailyData.get(i))
            );
        }

        long currentDayId = dailyData.get(0).getId();

        weather.getHourly().setParentDayId(currentDayId);
        databaseInstance.weatherDao().insertHourly(weather.getHourly());

        List<HourlyData> hourlyData = weather.getHourly().getData();

        for(int i = 0; i < hourlyData.size(); ++i){
            hourlyData.get(i).setParentDayId(currentDayId);
            databaseInstance.weatherDao().insertHourlyData(hourlyData.get(i));
        }

        //now load hours of next 7 days initially
        String apiKey = context.getString(R.string.api_key);
        Map<String, String> avoid = new HashMap<>();
            avoid.put("units", "si");
            avoid.put("lang", "en");
            avoid.put("exclude", "alerts,daily");


        PlaceWeather dayWeather;
        for(int i = 1;  i < dailyData.size(); ++i){
            try{
                dayWeather = api.getTimeForecast(apiKey,
                        weather.getLatitude(),
                        weather.getLongitude(),
                        dailyData.get(i).getTime()+1,
                        avoid).execute().body();
            }catch (IOException e){
                break;
            }

            dayWeather.getHourly().setParentDayId(dailyData.get(i).getId());
            dayWeather.getHourly().setId(
                    databaseInstance.weatherDao().insertHourly(dayWeather.getHourly())
            );

            hourlyData = dayWeather.getHourly().getData();

            for(int j = 0; j < hourlyData.size(); ++j){
                hourlyData.get(j).setParentDayId(dailyData.get(i).getId());
                databaseInstance.weatherDao().insertHourlyData(hourlyData.get(j));
            }
        }

    }

    //very heavy method, use as rare as possible
    void updateForecast(DarkSkyApi api, PlaceWeather weather){

        long now = Calendar.getInstance().getTimeInMillis()/1000;

        deleteObsoleteEntries(now);

        long placeId = weather.getId();
        long currentDay = databaseInstance.weatherDao().selectObsoleteDayCount(now);
        long daysSaved = databaseInstance.weatherDao().selectDayCount();

        //check if need to load data
        if(daysSaved - currentDay < 7){

            List<HourlyData> hourlyData;

            String apiKey = context.getString(R.string.api_key);
            Map<String, String> avoid = new HashMap<>();
                avoid.put("units", "si");
                avoid.put("lang", "en");
                avoid.put("exclude", "alerts,daily");

            long currentTime = weather.getDaily().getData().get(
                    weather.getDaily().getData().size()-1
            ).getTime() + 1;

            //load days
            for(long day = daysSaved - currentDay; day < 7; ++day){
                currentTime += 3600 * 24;
                PlaceWeather nextDay;
                try{
                    nextDay = api.getTimeForecast(apiKey,
                            weather.getLatitude(),
                            weather.getLongitude(),
                            now, avoid).execute().body();
                }catch (IOException e){
                    //log network failure
                    break;
                }

                nextDay.getDaily().getData().get(0).setParentPlaceId(placeId);

                long nextDailyDataId =
                        databaseInstance.weatherDao().insertDailyData(
                                nextDay.getDaily().getData().get(0)
                        );

                nextDay.getHourly().setParentDayId(nextDailyDataId);
                nextDay.getHourly().setId(
                        databaseInstance.weatherDao().insertHourly(nextDay.getHourly())
                );

                hourlyData = nextDay.getHourly().getData();

                for(int j = 0; j < hourlyData.size(); ++j){
                    hourlyData.get(j).setParentDayId(nextDailyDataId);
                    databaseInstance.weatherDao().insertHourlyData(hourlyData.get(j));
                }
            }
        }
    }

    void deleteObsoleteEntries(long now){

        //select obsolete days
        List<HourlyData> hoursToDelete = databaseInstance.weatherDao().selectObsoleteHours(now);

        //delete obsolete hours except except current hour
        for(int i = 0; i < hoursToDelete.size()-1; --i){
            databaseInstance.weatherDao().deleteHourlyEntry(hoursToDelete.get(i));
        }

        //select obsolete hours
        List<DailyData> daysToDelete = databaseInstance.weatherDao().selectObsoleteDays(now);

        //delete obsolete days except except current day
        for(int i = 0; i < daysToDelete.size()-1; --i){
            databaseInstance.weatherDao().deleteDailyEntry(daysToDelete.get(i));
        }


    }

    void deletePlace(PlaceWeather place){
        databaseInstance.weatherDao().deletePlaceWeather(place);
    }
}
