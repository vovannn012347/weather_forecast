
package com.vovik.weatherforcast.DarkSkyWeather;

//import android.arch.persistence.room.Entity;
//import android.arch.persistence.room.ForeignKey;
//import android.arch.persistence.room.Index;
//import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Entity(tableName = "Currently",
//        foreignKeys = @ForeignKey(entity = PlaceWeather.class,
//        parentColumns = "id",
//        childColumns = "parentPlaceId"),
//        indices = @Index("time"))
public class Currently {

//    @PrimaryKey(autoGenerate = true)
//    private long id;
//
//    private long parentPlaceId;

    @SerializedName("time")
    @Expose
    private Integer time;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("nearestStormDistance")
    @Expose
    private Integer nearestStormDistance;

    @SerializedName("precipIntensity")
    @Expose
    private Double precipIntensity;

    @SerializedName("precipType")
    @Expose
    private String precipType;

    @SerializedName("temperature")
    @Expose
    private Double temperature;

    @SerializedName("apparentTemperature")
    @Expose
    private Double apparentTemperature;

    @SerializedName("humidity")
    @Expose
    private Double humidity;

    @SerializedName("pressure")
    @Expose
    private Double pressure;

    @SerializedName("windSpeed")
    @Expose
    private Double windSpeed;

//    @SerializedName("precipIntensityError")
//    @Expose
//    private Double precipIntensityError;
//    @SerializedName("precipProbability")
//    @Expose
//    private Double precipProbability;

//    @SerializedName("dewPoint")
//    @Expose
//    private Double dewPoint;

//    @SerializedName("windGust")
//    @Expose
//    private Double windGust;
//    @SerializedName("windBearing")
//    @Expose
//    private Integer windBearing;
//    @SerializedName("cloudCover")
//    @Expose
//    private Double cloudCover;
//    @SerializedName("uvIndex")
//    @Expose
//    private Integer uvIndex;
//    @SerializedName("visibility")
//    @Expose
//    private Double visibility;
//    @SerializedName("ozone")
//    @Expose
//    private Double ozone;

    public Currently() {
    }



    public Currently(Integer time,
                     String summary,
                     String icon,
                     Integer nearestStormDistance,
                     Double precipIntensity,
//                     Double precipIntensityError,
//                     Double precipProbability,
                     String precipType,
                     Double temperature,
                     Double apparentTemperature,
//                     Double dewPoint,
                     Double humidity,
                     Double pressure,
                     Double windSpeed
//                     Double windGust,
//                     Integer windBearing,
//                     Double cloudCover,
//                     Integer uvIndex,
//                     Double visibility,
//                     Double ozone
    ) {
        super();
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.nearestStormDistance = nearestStormDistance;
        this.precipIntensity = precipIntensity;
//        this.precipIntensityError = precipIntensityError;
//        this.precipProbability = precipProbability;
        this.precipType = precipType;
        this.temperature = temperature;
        this.apparentTemperature = apparentTemperature;
//        this.dewPoint = dewPoint;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
//        this.windGust = windGust;
//        this.windBearing = windBearing;
//        this.cloudCover = cloudCover;
//        this.uvIndex = uvIndex;
//        this.visibility = visibility;
//        this.ozone = ozone;
    }

//    public long getId(){ return id; }
//
//    public void setId(long id){ this.id = id; }
//
//    public long getParentPlaceId(){ return parentPlaceId; }
//
//    public void setParentPlaceId(long parentPlaceId){ this.parentPlaceId = parentPlaceId; }

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

    public Integer getNearestStormDistance() {
        return nearestStormDistance;
    }

    public void setNearestStormDistance(Integer nearestStormDistance) {
        this.nearestStormDistance = nearestStormDistance;
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

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getApparentTemperature() {
        return apparentTemperature;
    }

    public void setApparentTemperature(Double apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }

//    public Double getDewPoint() {
//        return dewPoint;
//    }
//
//    public void setDewPoint(Double dewPoint) {
//        this.dewPoint = dewPoint;
//    }

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
//    public Double getVisibility() {
//        return visibility;
//    }
//
//    public void setVisibility(Double visibility) {
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

}
