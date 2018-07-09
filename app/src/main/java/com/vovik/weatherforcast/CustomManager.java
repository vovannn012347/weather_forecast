package com.vovik.weatherforcast;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vovik.weatherforcast.DarkSkyWeather.PlaceWeather;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by vovik on 06.07.2018.
 */


public class CustomManager extends FragmentStatePagerAdapter {

    protected List<PlaceWeather> weatherPages;
    protected PageFragmentAdd adddPage;

    public CustomManager(FragmentManager fm) {
        super(fm);
        weatherPages = new LinkedList<>();
        adddPage = PageFragmentAdd.instance();
    }

    @Override
    public Fragment getItem(int position) {

        if(position < weatherPages.size()){
            return PageFragmentDisplay.instance(weatherPages.get(position));
        }else{
            return adddPage;
        }
    }

    @Override
    public int getCount() {
        return this.weatherPages.size()+1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void removeOldFragment(PlaceWeather f){

        if(weatherPages.contains(f)){
            weatherPages.remove(f);
            notifyDataSetChanged();
        }
    }

    /*
    returns index of newly inserted element
     */
    public void addNewFragment(PlaceWeather f){

        if(!weatherPages.contains(f)){
            weatherPages.add(f);
            notifyDataSetChanged();
        }

    }


}
