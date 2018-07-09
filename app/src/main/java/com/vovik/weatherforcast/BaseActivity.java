package com.vovik.weatherforcast;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private static CustomManager sectionPagerAdapter;
    private Retrofit retrofit;
    private DarkSkyApi api;
    private boolean uploadingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.darksky.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(DarkSkyApi.class);

        sectionPagerAdapter = new WeatherFragmentManager(getSupportFragmentManager());

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        uploadingData = false;
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    void addNewPlace(Address location){

        if(uploadingData){
            return;
        }else{
            uploadingData = true;
        }

        Map<String, String> map = new HashMap<String, String>();
            map.put("units", "si");
            map.put("lang", "en");
            map.put("exclude", "alerts");

        api.getForecast(getResources().getString(R.string.api_key),
                location.getLatitude(), location.getLongitude(), map)
                .enqueue(
                        new Callback<PlaceWeather>() {
                            @Override
                            public void onResponse(Call<PlaceWeather> call, Response<PlaceWeather> response) {

                                if(!response.isSuccessful()){
                                    return;
                                }
                                addPlaceFragment(response.body());
                                uploadingData = false;
                            }

                            @Override
                            public void onFailure(Call<PlaceWeather> call, Throwable t) {
                                uploadingData = false;
                                Toast.makeText(getApplicationContext(), "Failed loading weather " + call.request().url() + t.getCause(), Toast.LENGTH_LONG).show();
                            }
                        });
    }

    void addPlaceFragment(PlaceWeather weather){

        sectionPagerAdapter.addNewFragment(weather);
    }

    void removeItem(PlaceWeather f){
        sectionPagerAdapter.removeOldFragment(f);
    }

    void updateMinutely(final PageFragmentDisplay d, double lattitude, double longitude){
        api.getMinutely(getResources().getString(R.string.api_key),
                lattitude, longitude)
                .enqueue(
                        new Callback<PlaceWeather>() {
                            @Override
                            public void onResponse(Call<PlaceWeather> call, Response<PlaceWeather> response) {
                                if(!response.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Failed updating minutely weather", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                d.updateMinutely(response.body());
                            }

                            @Override
                            public void onFailure(Call<PlaceWeather> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Failed updating minutely weather", Toast.LENGTH_LONG).show();
                            }
                        });
    }

    void updateHourly(final PageFragmentDisplay d, double lattitude, double longitude){
        Map<String, String> map = new HashMap<>();
            map.put("exclude", "alerts");
            map.put("lang", "en");
            map.put("units", "si");

        api.getForecast(getResources().getString(R.string.api_key),
                lattitude, longitude, map)
                .enqueue(
                        new Callback<PlaceWeather>() {
                            @Override
                            public void onResponse(Call<PlaceWeather> call, Response<PlaceWeather> response) {
                                if(!response.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Failed updating weather", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                d.updatePlaceWeather(response.body());
                            }

                            @Override
                            public void onFailure(Call<PlaceWeather> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "Failed updating weather", Toast.LENGTH_LONG).show();
                            }
                        });
    }

    public class WeatherFragmentManager extends CustomManager{
        public WeatherFragmentManager(FragmentManager fm) {
            super(fm);
            weatherPages = new LinkedList<>();
            adddPage = WeatherPageAdd.instance();
        }
        @Override
        public Fragment getItem(int position) {

            if(position < weatherPages.size()){
                return WeatherDisplaypage.instance(weatherPages.get(position));
            }else{
                return adddPage;
            }
        }
    }


    public static class WeatherDisplaypage extends PageFragmentDisplay {
        @Override
        public void updateHourly(Double lattitude, Double longitude){
            ((BaseActivity)getActivity()).updateHourly(this, lattitude, longitude);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUi();
                }
            });
        }

        @Override
        public void updateMinutely(Double lattitude, Double longitude){
            ((BaseActivity)getActivity()).updateMinutely(this, lattitude, longitude);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUIHourly();
                }
            });
        }

        public static WeatherDisplaypage instance(PlaceWeather data){
            WeatherDisplaypage f = new WeatherDisplaypage();
            f.data = data;
            f.timeShift = TimeZone.getTimeZone(data.getTimezone()).getRawOffset() - TimeZone.getDefault().getRawOffset();

            return f;
        }
    }

    public static class WeatherPageAdd extends PageFragmentAdd{
        @Override
        public void addPage(Address a){
            ((BaseActivity)getActivity()).addNewPlace(a);
        }

        public static WeatherPageAdd instance(){
            return new WeatherPageAdd();
        }
    }
}
