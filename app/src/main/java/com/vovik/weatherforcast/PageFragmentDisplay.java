package com.vovik.weatherforcast;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vovik.weatherforcast.DarkSkyWeather.DailyData;
import com.vovik.weatherforcast.DarkSkyWeather.HourlyData;
import com.vovik.weatherforcast.DarkSkyWeather.MinutelyData;
import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vovik on 06.07.2018.
 */

//todo: add check for null

public class PageFragmentDisplay extends Fragment {

    PlaceWeather data;
    View rootView;
    Timer updateTimer;
    int minuteIndex;
    int hourIndex;
    int timeShift;

    public static PageFragmentDisplay instance(PlaceWeather data){
        PageFragmentDisplay f = new PageFragmentDisplay();
        f.data = data;

        return f;
    }

    public PageFragmentDisplay(){
        hourIndex = 0;
        minuteIndex = 0;
        updateTimer = new Timer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_city_weather, container, false);

        ImageButton deleteButton = rootView.findViewById(R.id.imageButtonRemoveItem);

        TextView v = rootView.findViewById(R.id.textViewDarkSky);
        timeShift = TimeZone.getTimeZone(data.getTimezone()).getRawOffset()/1000;
        //todo: add darksky link

        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateData();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUIMinutely();
                    }
                });
            }
        }, 60000, 60000);

        updateData();
        updateUi();

        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        updateTimer.cancel();
        updateTimer.purge();
    }

    void updateData(){
        long currentTime = System.currentTimeMillis() % 1000;

        if(data.getHourly() != null){
            List<HourlyData> hours = data.getHourly().getData();

            boolean hoursUpdate = false;

            while(hourIndex < hours.size()-1) {
                if (hours.get(hourIndex+1).getTime() - currentTime - timeShift > 0) {
                    hoursUpdate = true;
                    break;
                }
                ++hourIndex;
            }

            //todo: redo into callback
            //need update of hourly forecast data
            if(hoursUpdate && hourIndex > 22){
                updateHourly(data.getLatitude(), data.getLongitude());
            }
        }

        if(data.getMinutely() != null){
            List<MinutelyData> minutes = data.getMinutely().getData();
            boolean minutesUpdated = false;
            while(minuteIndex < minutes.size()-1){
                if(minutes.get(minuteIndex+1).getTime() - currentTime - timeShift > 0){
                    minutesUpdated = true;
                    break;
                }
                ++minuteIndex;
            }

            if(!minutesUpdated && minuteIndex == minutes.size()-1){
                //update of miute forecast required
                updateMinutely(data.getLatitude(), data.getLongitude());
            }
        }


    }

    void updateMinutely(PlaceWeather weather){
        data.setCurrently(weather.getCurrently());
        data.setMinutely(weather.getMinutely());

        minuteIndex = 0;

        updateUIMinutely();
        updateUIHourly();
    }

    void updatePlaceWeather(PlaceWeather weather){
        data = weather;

        updateUi();
    }

    void updateUi(){
        updateUIMinutely();
        updateUIHourly();
        updateUIDaily();
    }

    void updateUIMinutely(){

        //set background
        ConstraintLayout background = rootView.findViewById(R.id.constraintLayout);

        //set current data view
        ImageView currentWeather = rootView.findViewById(R.id.imageViewCurrentIcon);
        TextView
                currentTemperature = rootView.findViewById(R.id.textViewCurrentTemperature),
                currentHumidity = rootView.findViewById(R.id.textViewHumidity),
                currentWind = rootView.findViewById(R.id.textViewWindSpeed),
                currentPrecipitation = rootView.findViewById(R.id.textViewPrecipitation);

        HourlyData currentHour = data.getHourly().getData().get(hourIndex);

        currentWeather.setImageDrawable(getIcon(currentHour.getIcon()));
        currentTemperature.setText(getString(R.string.temperature_template, currentHour.getTemperature().intValue()));
        currentHumidity.setText(getString(R.string.humidity_template, currentHour.getHumidity()*100));
        currentWind.setText(getString(R.string.wind_speed_template, currentHour.getWindSpeed().intValue()));

        if(data.getMinutely()!=null){
            background.setBackground(getImage(data.getMinutely().getIcon()));

            MinutelyData currentMinute = data.getMinutely().getData().get(minuteIndex);
            currentPrecipitation.setText(getString(R.string.precipitation_template, currentMinute.getPrecipIntensity()));

        }else{
            background.setBackground(getImage(currentHour.getIcon()));

            currentPrecipitation.setText(getString(R.string.precipitation_template, currentHour.getPrecipIntensity()));
        }
    }

    void updateUIHourly(){

        Date tempDate = new Date();
        DateFormat
                sunRiseFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()),
                hourFormat =  new SimpleDateFormat("h a", Locale.getDefault());

        //populate hourly view
        LinearLayout hourView = rootView.findViewById(R.id.HourlyWeatherLinearLayout);
        TextView
                minDayTemp = rootView.findViewById(R.id.textViewHourlyTempMin),
                maxDayTemp = rootView.findViewById(R.id.textViewHourlyTempMax);


        DailyData thisDay = data.getDaily().getData().get(0);

        minDayTemp.setText(getString(R.string.temperature_template, thisDay.getTemperatureLow().intValue()));
        maxDayTemp.setText(getString(R.string.temperature_template, thisDay.getTemperatureHigh().intValue()));

        HourlyData currentHour;
        boolean sunrise = false;
        boolean sunset = false;

        for(int i = 0, cur = 0; i < 24; ++i, ++cur){
            currentHour = data.getHourly().getData().get(hourIndex + i);

            if(!sunrise && currentHour.getTime() > thisDay.getSunriseTime()){
                tempDate.setTime((thisDay.getSunriseTime()-timeShift)*1000);
                setHourView(hourView, cur,  sunRiseFormat.format(tempDate), "sunrise", "Sunrise");
                ++cur;
                sunrise = true;
            }else
            if(!sunset && currentHour.getTime() > thisDay.getSunsetTime()){
                tempDate.setTime((thisDay.getSunsetTime()-timeShift)*1000);
                setHourView(hourView, cur,  sunRiseFormat.format(tempDate), "sunset", "Sunset");
                ++cur;
                sunset = true;
            }

            tempDate.setTime((currentHour.getTime()-timeShift)*1000);
            if(i == 0){
                setHourView(hourView,
                        cur,
                        getString(R.string.now),
                        currentHour.getIcon(),
                        getString(R.string.temperature_template, currentHour.getTemperature().intValue()));
            }else{
                setHourView(hourView,
                        cur,
                        hourFormat.format(tempDate),
                        currentHour.getIcon(),
                        getString(R.string.temperature_template, currentHour.getTemperature().intValue()));
            }

        }

        while(hourView.getChildCount() > 26){
            hourView.removeViewAt(26);
        }
    }

    void updateUIDaily(){

        Date tempDate = new Date();
        DateFormat
                currentDayFormat = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()),
                weekDayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        //set current date
        TextView currentDay = rootView.findViewById(R.id.textViewCurrentDay);
        tempDate.setTime(System.currentTimeMillis());
        currentDay.setText(currentDayFormat.format(tempDate));


        //populate daily view
        LinearLayout dayViewList = rootView.findViewById(R.id.DailyWeatherLinearLayout);
        List<DailyData> dayList = data.getDaily().getData();

        for(int i = 1; i < dayList.size(); ++i){
            tempDate.setTime(dayList.get(i).getTime()*1000);
            setDayView(dayViewList, i,
                    weekDayFormat.format(tempDate),
                    dayList.get(i).getIcon(),
                    getString(R.string.temperature_template, dayList.get(i).getTemperatureLow().intValue()),
                    getString(R.string.temperature_template, dayList.get(i).getTemperatureHigh().intValue()));
        }

        while(dayViewList.getChildCount() > dayList.size()-1){
            dayViewList.removeViewAt(dayList.size()-1);
        }

    }

    View newHourView(String above, String iconWeather, String below){

        LinearLayout base = new LinearLayout(getContext());
        base.setOrientation(LinearLayout.VERTICAL);
        base.setLayoutParams(new LinearLayout.
                LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        base.setGravity(Gravity.CENTER | Gravity.TOP);

        TextView tv1 = new TextView(getContext());
        tv1.setText(above);
        tv1.setId(R.id.textViewAbove);
        tv1.setLayoutParams(new LinearLayout.
                LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        base.addView(tv1);

        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable( getIcon(iconWeather));
        iv.setId(R.id.imageViewMiddle);
        iv.setLayoutParams(new LinearLayout.
                LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        iv.setColorFilter(getResources().getColor(R.color.textColor));

        iv.getLayoutParams().width = (int)getResources().getDimension(R.dimen.image_size);
        iv.getLayoutParams().height = (int)getResources().getDimension(R.dimen.image_size);

        base.addView(iv);

        TextView tv2 = new TextView(getContext());
        tv2.setText(below);
        tv2.setId(R.id.textViewBelow);
        tv2.setLayoutParams(new LinearLayout.
                LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        base.addView(tv2);

        return base;
    }

    void setHourView(LinearLayout root, int index, String above, String iconWeather, String below){

        if(index < root.getChildCount()){

            View update = root.getChildAt(index);
            ((TextView)update.findViewById(R.id.textViewAbove)).setText(above);
            ((ImageView)update.findViewById(R.id.imageViewMiddle)).setImageDrawable(getIcon(iconWeather));
            ((TextView)update.findViewById(R.id.textViewBelow)).setText(below);

            root.addView(update, index);

        }else{
            View add = newHourView(above, iconWeather, below);
            root.addView(add);
        }
    }

    void setDayView(LinearLayout root, int index, String day, String iconWeather, String minTemp, String maxTemp){

        View dayView;

        if(index < root.getChildCount())
            dayView = root.getChildAt(index);
        else{
            dayView = getLayoutInflater().inflate(R.layout.daily_item, root, false);
            root.addView(dayView);
        }

        TextView weekDayName = dayView.findViewById(R.id.textViewWeekDay);
        weekDayName.setText(day);

        ImageView dayIcon = dayView.findViewById(R.id.imageViewDayIcon);
        dayIcon.setImageDrawable(getIcon(iconWeather));

        TextView weekDayMin = dayView.findViewById(R.id.textViewDayMin);
        weekDayMin.setText(minTemp);

        TextView weekDayMax = dayView.findViewById(R.id.textViewDayMax);
        weekDayMax.setText(maxTemp);
    }

    public Drawable getImage(String weather){

        //Drawable ret = null;
        switch(weather){
            case "clear-day":
                return rootView.getResources().getDrawable(R.drawable.background_clear_day);
            case "clear-night":
                return rootView.getResources().getDrawable(R.drawable.background_clear_night);
            case "rain":
                return rootView.getResources().getDrawable(R.drawable.background_rain);
            case "snow":
                return rootView.getResources().getDrawable(R.drawable.background_snow);
            case "sleet":
                return rootView.getResources().getDrawable(R.drawable.background_sleet);
            case "wind":
                return rootView.getResources().getDrawable(R.drawable.background_wind);
            case "fog":
                return rootView.getResources().getDrawable(R.drawable.background_fog);
            case "cloudy":
                return rootView.getResources().getDrawable(R.drawable.background_cloudy);
            case "partly-cloudy-day":
                 return rootView.getResources().getDrawable(R.drawable.background_cloudy_day);
            case "partly-cloudy-night":
                return rootView.getResources().getDrawable(R.drawable.background_cloudy_night);
            case "sunrise":
                return rootView.getResources().getDrawable(R.drawable.background_sunrise);
            case "sunset":
                return rootView.getResources().getDrawable(R.drawable.background_sunset);
            default:
                return rootView.getResources().getDrawable(R.drawable.ic_cloud_download);
        }

        //return ret;
    }

    public Drawable getIcon(String weather){
        switch(weather){
            case "clear-day":
                return rootView.getResources().getDrawable(R.drawable.clear_day);
            case "clear-night":
                return rootView.getResources().getDrawable(R.drawable.clear_night);
            case "rain":
                return rootView.getResources().getDrawable(R.drawable.rain);
            case "snow":
                return rootView.getResources().getDrawable(R.drawable.snow);
            case "sleet":
                return rootView.getResources().getDrawable(R.drawable.sleet);
            case "wind":
                return rootView.getResources().getDrawable(R.drawable.ic_wind);
            case "fog":
                return rootView.getResources().getDrawable(R.drawable.fog);
            case "cloudy":
                return rootView.getResources().getDrawable(R.drawable.cloudy);
            case "partly-cloudy-day":
                return rootView.getResources().getDrawable(R.drawable.partly_cloudy_day);
            case "partly-cloudy-night":
                return rootView.getResources().getDrawable(R.drawable.partly_cloudy_night);
            case "sunrise":
                return rootView.getResources().getDrawable(R.drawable.sunrise);
            case "sunset":
                return rootView.getResources().getDrawable(R.drawable.sunset);
            default:
                return rootView.getResources().getDrawable(R.drawable.ic_cloud_download);
        }
    }

    public void updateHourly(Double lattitude, Double longitude){

    }

    public void updateMinutely(Double lattitude, Double longitude){

    }
}
