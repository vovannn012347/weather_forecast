
package com.vovik.weatherforcast.DarkSkyWeather;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "DailyData",
        foreignKeys =
        @ForeignKey(entity = PlaceWeather.class,
                parentColumns = "id",
                childColumns = "parentPlaceId",
                onDelete = CASCADE),
        indices = {@Index("time"), @Index("parentPlaceId")})
public class DailyData {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long parentPlaceId;

    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("sunriseTime")
    @Expose
    private Integer sunriseTime;
    @SerializedName("sunsetTime")
    @Expose
    private Integer sunsetTime;
    @SerializedName("moonPhase")
    @Expose
    private Double moonPhase;
    @SerializedName("precipIntensity")
    @Expose
    private Double precipIntensity;
    @SerializedName("precipType")
    @Expose
    private String precipType;
    @SerializedName("temperatureHigh")
    @Expose
    private Double temperatureHigh;
    @SerializedName("temperatureLow")
    @Expose
    private Double temperatureLow;
    @SerializedName("apparentTemperatureHigh")
    @Expose
    private Double apparentTemperatureHigh;
    @SerializedName("dewPoint")
    @Expose
    private Double dewPoint;
    @SerializedName("humidity")
    @Expose
    private Double humidity;
    @SerializedName("pressure")
    @Expose
    private Double pressure;
    @SerializedName("windSpeed")
    @Expose
    private Double windSpeed;
    @SerializedName("temperatureMin")
    @Expose
    private Double temperatureMin;
    @SerializedName("temperatureMax")
    @Expose
    private Double temperatureMax;
    @SerializedName("apparentTemperatureMin")
    @Expose
    private Double apparentTemperatureMin;
//    @SerializedName("precipIntensityMax")
//    @Expose
//    private Double precipIntensityMax;
//    @SerializedName("precipIntensityMaxTime")
//    @Expose
//    private Integer precipIntensityMaxTime;
//    @SerializedName("precipProbability")
//    @Expose
//    private Double precipProbability;
//    @SerializedName("temperatureHighTime")
//    @Expose
//    private Integer temperatureHighTime;
//    @SerializedName("temperatureLowTime")
//    @Expose
//    private Integer temperatureLowTime;
//    @SerializedName("apparentTemperatureHighTime")
//    @Expose
//    private Integer apparentTemperatureHighTime;
//    @SerializedName("apparentTemperatureLow")
//    @Expose
//    private Double apparentTemperatureLow;
//    @SerializedName("apparentTemperatureLowTime")
//    @Expose
//    private Integer apparentTemperatureLowTime;
//    @SerializedName("windGust")
//    @Expose
//    private Double windGust;
//    @SerializedName("windGustTime")
//    @Expose
//    private Integer windGustTime;
//    @SerializedName("windBearing")
//    @Expose
//    private Integer windBearing;
//    @SerializedName("cloudCover")
//    @Expose
//    private Double cloudCover;
//    @SerializedName("uvIndex")
//    @Expose
//    private Integer uvIndex;
//    @SerializedName("uvIndexTime")
//    @Expose
//    private Integer uvIndexTime;
//    @SerializedName("visibility")
//    @Expose
//    private Integer visibility;
//    @SerializedName("ozone")
//    @Expose
//    private Double ozone;
//    @SerializedName("temperatureMinTime")
//    @Expose
//    private Integer temperatureMinTime;
//    @SerializedName("temperatureMaxTime")
//    @Expose
//    private Integer temperatureMaxTime;
//    @SerializedName("apparentTemperatureMinTime")
//    @Expose
//    private Integer apparentTemperatureMinTime;
//    @SerializedName("apparentTemperatureMax")
//    @Expose
//    private Double apparentTemperatureMax;
//    @SerializedName("apparentTemperatureMaxTime")
//    @Expose
//    private Integer apparentTemperatureMaxTime;


    public DailyData() {
    }

    @Ignore
    public DailyData(
            Integer time,
            String summary,
            String icon,
            Integer sunriseTime,
            Integer sunsetTime,
            Double moonPhase,
            Double precipIntensity,
//            Double precipIntensityMax,
//            Integer precipIntensityMaxTime,
//            Double precipProbability,
            String precipType,
            Double temperatureHigh,
//            Integer temperatureHighTime,
            Double temperatureLow,
//            Integer temperatureLowTime,
            Double apparentTemperatureHigh,
//            Integer apparentTemperatureHighTime,
//            Double apparentTemperatureLow,
//            Integer apparentTemperatureLowTime,
            Double dewPoint,
            Double humidity,
            Double pressure,
            Double windSpeed,
//            Double windGust,
//            Integer windGustTime,
//            Integer windBearing,
//            Double cloudCover,
//            Integer uvIndex,
//            Integer uvIndexTime,
//            Integer visibility,
//            Double ozone,
            Double temperatureMin,
//            Integer temperatureMinTime,
            Double temperatureMax,
//            Integer temperatureMaxTime,
            Double apparentTemperatureMin
//            Integer apparentTemperatureMinTime,
//            Double apparentTemperatureMax,
//            Integer apparentTemperatureMaxTime
    ) {
        super();
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.moonPhase = moonPhase;
        this.precipIntensity = precipIntensity;
//        this.precipIntensityMax = precipIntensityMax;
//        this.precipIntensityMaxTime = precipIntensityMaxTime;
//        this.precipProbability = precipProbability;
        this.precipType = precipType;
        this.temperatureHigh = temperatureHigh;
//        this.temperatureHighTime = temperatureHighTime;
        this.temperatureLow = temperatureLow;
//        this.temperatureLowTime = temperatureLowTime;
        this.apparentTemperatureHigh = apparentTemperatureHigh;
//        this.apparentTemperatureHighTime = apparentTemperatureHighTime;
//        this.apparentTemperatureLow = apparentTemperatureLow;
//        this.apparentTemperatureLowTime = apparentTemperatureLowTime;
        this.dewPoint = dewPoint;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
//        this.windGust = windGust;
//        this.windGustTime = windGustTime;
//        this.windBearing = windBearing;
//        this.cloudCover = cloudCover;
//        this.uvIndex = uvIndex;
//        this.uvIndexTime = uvIndexTime;
//        this.visibility = visibility;
//        this.ozone = ozone;
        this.temperatureMin = temperatureMin;
//        this.temperatureMinTime = temperatureMinTime;
        this.temperatureMax = temperatureMax;
//        this.temperatureMaxTime = temperatureMaxTime;
        this.apparentTemperatureMin = apparentTemperatureMin;
//        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
//        this.apparentTemperatureMax = apparentTemperatureMax;
//        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }


    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }

    public long getParentPlaceId(){ return parentPlaceId; }

    public void setParentPlaceId(long parentPlaceId){ this.parentPlaceId = parentPlaceId; }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(Integer sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public Integer getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(Integer sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public Double getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(Double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public Double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(Double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

//    public Double getPrecipIntensityMax() {
//        return precipIntensityMax;
//    }
//
//    public void setPrecipIntensityMax(Double precipIntensityMax) {
//        this.precipIntensityMax = precipIntensityMax;
//    }
//
//    public Integer getPrecipIntensityMaxTime() {
//        return precipIntensityMaxTime;
//    }
//
//    public void setPrecipIntensityMaxTime(Integer precipIntensityMaxTime) {
//        this.precipIntensityMaxTime = precipIntensityMaxTime;
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

    public Double getTemperatureHigh() {
        return temperatureHigh;
    }

    public void setTemperatureHigh(Double temperatureHigh) {
        this.temperatureHigh = temperatureHigh;
    }

//    public Integer getTemperatureHighTime() {
//        return temperatureHighTime;
//    }
//
//    public void setTemperatureHighTime(Integer temperatureHighTime) {
//        this.temperatureHighTime = temperatureHighTime;
//    }

    public Double getTemperatureLow() {
        return temperatureLow;
    }

    public void setTemperatureLow(Double temperatureLow) {
        this.temperatureLow = temperatureLow;
    }

//    public Integer getTemperatureLowTime() {
//        return temperatureLowTime;
//    }
//
//    public void setTemperatureLowTime(Integer temperatureLowTime) {
//        this.temperatureLowTime = temperatureLowTime;
//    }

    public Double getApparentTemperatureHigh() {
        return apparentTemperatureHigh;
    }

    public void setApparentTemperatureHigh(Double apparentTemperatureHigh) {
        this.apparentTemperatureHigh = apparentTemperatureHigh;
    }

//    public Integer getApparentTemperatureHighTime() {
//        return apparentTemperatureHighTime;
//    }
//
//    public void setApparentTemperatureHighTime(Integer apparentTemperatureHighTime) {
//        this.apparentTemperatureHighTime = apparentTemperatureHighTime;
//    }
//
//    public Double getApparentTemperatureLow() {
//        return apparentTemperatureLow;
//    }
//
//    public void setApparentTemperatureLow(Double apparentTemperatureLow) {
//        this.apparentTemperatureLow = apparentTemperatureLow;
//    }
//
//    public Integer getApparentTemperatureLowTime() {
//        return apparentTemperatureLowTime;
//    }
//
//    public void setApparentTemperatureLowTime(Integer apparentTemperatureLowTime) {
//        this.apparentTemperatureLowTime = apparentTemperatureLowTime;
//    }

    public Double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(Double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

//    public Double getWindGust() {
//        return windGust;
//    }
//
//    public void setWindGust(Double windGust) {
//        this.windGust = windGust;
//    }
//
//    public Integer getWindGustTime() {
//        return windGustTime;
//    }
//
//    public void setWindGustTime(Integer windGustTime) {
//        this.windGustTime = windGustTime;
//    }
//
//    public Integer getWindBearing() {
//        return windBearing;
//    }
//
//    public void setWindBearing(Integer windBearing) {
//        this.windBearing = windBearing;
//    }
//
//    public Double getCloudCover() {
//        return cloudCover;
//    }
//
//    public void setCloudCover(Double cloudCover) {
//        this.cloudCover = cloudCover;
//    }
//
//    public Integer getUvIndex() {
//        return uvIndex;
//    }
//
//    public void setUvIndex(Integer uvIndex) {
//        this.uvIndex = uvIndex;
//    }
//
//    public Integer getUvIndexTime() {
//        return uvIndexTime;
//    }
//
//    public void setUvIndexTime(Integer uvIndexTime) {
//        this.uvIndexTime = uvIndexTime;
//    }
//
//    public Integer getVisibility() {
//        return visibility;
//    }
//
//    public void setVisibility(Integer visibility) {
//        this.visibility = visibility;
//    }
//
//    public Double getOzone() {
//        return ozone;
//    }
//
//    public void setOzone(Double ozone) {
//        this.ozone = ozone;
//    }

    public Double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(Double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

//    public Integer getTemperatureMinTime() {
//        return temperatureMinTime;
//    }
//
//    public void setTemperatureMinTime(Integer temperatureMinTime) {
//        this.temperatureMinTime = temperatureMinTime;
//    }

    public Double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(Double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

//    public Integer getTemperatureMaxTime() {
//        return temperatureMaxTime;
//    }
//
//    public void setTemperatureMaxTime(Integer temperatureMaxTime) {
//        this.temperatureMaxTime = temperatureMaxTime;
//    }

    public Double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public void setApparentTemperatureMin(Double apparentTemperatureMin) {
        this.apparentTemperatureMin = apparentTemperatureMin;
    }

//    public Integer getApparentTemperatureMinTime() {
//        return apparentTemperatureMinTime;
//    }
//
//    public void setApparentTemperatureMinTime(Integer apparentTemperatureMinTime) {
//        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
//    }
//
//    public Double getApparentTemperatureMax() {
//        return apparentTemperatureMax;
//    }
//
//    public void setApparentTemperatureMax(Double apparentTemperatureMax) {
//        this.apparentTemperatureMax = apparentTemperatureMax;
//    }
//
//    public Integer getApparentTemperatureMaxTime() {
//        return apparentTemperatureMaxTime;
//    }
//
//    public void setApparentTemperatureMaxTime(Integer apparentTemperatureMaxTime) {
//        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
//    }

}
