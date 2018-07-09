
package com.vovik.weatherforcast.DarkSkyWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Minutely {

    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("data")
    @Expose
    private List<MinutelyData> data = new ArrayList<MinutelyData>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Minutely() {
    }

    /**
     * 
     * @param summary
     * @param icon
     * @param data
     */
    public Minutely(String summary, String icon, List<MinutelyData> data) {
        super();
        this.summary = summary;
        this.icon = icon;
        this.data = data;
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

    public List<MinutelyData> getData() {
        return data;
    }

    public void setData(List<MinutelyData> data) {
        this.data = data;
    }

}
