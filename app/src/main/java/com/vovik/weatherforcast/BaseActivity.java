package com.vovik.weatherforcast;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    private DatabaseTools databaseTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.darksky.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        databaseTools = new DatabaseTools(this);

        api = retrofit.create(DarkSkyApi.class);

        sectionPagerAdapter = new WeatherFragmentManager(getSupportFragmentManager());

        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(sectionPagerAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                final List<PlaceWeather> places = databaseTools.loadFromDb();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addFragments(places);
                    }
                });

                uploadingData = false;

            }
        }).start();
    }

    public void addFragments(List<PlaceWeather> places){
        for(int i = 0; i < places.size(); ++i){
            sectionPagerAdapter.addNewFragment(places.get(i));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    void addNewPlace(final Address location){

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
                                StringBuilder locationString = new StringBuilder();
                                for(int i = 0; i <= location.getMaxAddressLineIndex(); ++i){
                                    locationString.append(location.getAddressLine(i));
                                }

                                addPlaceFragment(response.body(), locationString.toString());
                                uploadingData = false;
                            }

                            @Override
                            public void onFailure(Call<PlaceWeather> call, Throwable t) {
                                uploadingData = false;
                                Toast.makeText(getApplicationContext(), "Failed loading weather " + call.request().url() + t.getCause(), Toast.LENGTH_LONG).show();
                            }
                        });
    }

    /*
     * this must run in another thread
     */
    void addPlaceFragment(final PlaceWeather weather, String location){

        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseTools.getForecastInitially(api, weather);
            }
        }).start();

        weather.setLocation(location);
        sectionPagerAdapter.addNewFragment(weather);
    }

    void removeItem(final PlaceWeather f){
        sectionPagerAdapter.removeOldFragment(f);
        new Thread(new Runnable() {
            @Override
            public void run() {
                databaseTools.deletePlace(f);
            }
        }).start();
    }

    //called on create
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

                                databaseTools.deleteObsoleteEntries(
                                        System.currentTimeMillis()/1000
                                );
                            }

                            @Override
                            public void onFailure(Call<PlaceWeather> call, Throwable t) {
                                databaseTools.deleteObsoleteEntries(
                                        System.currentTimeMillis()/1000
                                );
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

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        databaseTools.updateForecast(api, d.data);
                                    }
                                }).start();
                            }

                            @Override
                            public void onFailure(Call<PlaceWeather> call, Throwable t) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        databaseTools.updateFromDb(d.data);
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(), "Failed updating weather", Toast.LENGTH_LONG).show();
                            }
                        });
    }

    List<String> getFragmetnsList(){
        ArrayList<String> ret = new ArrayList<>(sectionPagerAdapter.weatherPages.size());

        for(int i = 0; i < sectionPagerAdapter.weatherPages.size(); ++i){
            ret.add(sectionPagerAdapter.weatherPages.get(i).getLocation());
        }

        return ret;
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
                return WeatherDisplayPage.instance(weatherPages.get(position));
            }else{
                return adddPage;
            }
        }
    }

    public static class WeatherDisplayPage extends PageFragmentDisplay {
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

        @Override
        public void removeFragment(){
            ((BaseActivity)getActivity()).removeItem(this.data);
        }

        @Override
        public void  onPopupMenuClick(MenuItem item){
            ((BaseActivity)getActivity()).viewPager.setCurrentItem(item.getItemId());
        }

        @Override
        public PopupMenu getPopupMenu(View root){
            List<String> menuItems = ((BaseActivity)getActivity()).getFragmetnsList();
            ContextThemeWrapper darkColorApply = new ContextThemeWrapper(getContext(), R.style.popupMenuStyle);

            PopupMenu ret = new PopupMenu(darkColorApply, root);

            for(int i = 0; i < menuItems.size(); ++i){
                ret.getMenu().add(Menu.NONE, i, i, menuItems.get(i));
            }

            ret.getMenu().add(Menu.NONE, menuItems.size(), menuItems.size(), getResources().getString(R.string.menu_add_button));

            return ret;
        }

        public static WeatherDisplayPage instance(PlaceWeather data){
            WeatherDisplayPage f = new WeatherDisplayPage();
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
