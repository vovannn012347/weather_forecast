
package com.vovik.weatherforcast.DarkSkyWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinutelyData {

    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("precipIntensity")
    @Expose
    private Double precipIntensity;
//    @SerializedName("precipIntensityError")
//    @Expose
//    private Double precipIntensityError;
//    @SerializedName("precipProbability")
//    @Expose
//    private Double precipProbability;
    @SerializedName("precipType")
    @Expose
    private String precipType;


    public MinutelyData() {
    }

    public MinutelyData(Integer time, Double precipIntensity, Double precipIntensityError, Double precipProbability, String precipType) {
        super();
        this.time = time;
        this.precipIntensity = precipIntensity;
//        this.precipIntensityError = precipIntensityError;
//        this.precipProbability = precipProbability;
        this.precipType = precipType;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(Double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

//    public Double getPrecipIntensityError() {
//        return precipIntensityError;
//    }
//
//    public void setPrecipIntensityError(Double precipIntensityError) {
//        this.precipIntensityError = precipIntensityError;
//    }
//
//    public Double getPrecipProbability() {
//        return precipProbability;
//    }
//
//    public void setPrecipProbability(Double precipProbability) {
//        this.precipProbability = precipProbability;
//    }

    public String getPrecipType() {
        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

}
