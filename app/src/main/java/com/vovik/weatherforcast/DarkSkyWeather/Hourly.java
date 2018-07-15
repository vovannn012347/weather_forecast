
package com.vovik.weatherforcast.DarkSkyWeather;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "Hourly",
        foreignKeys = @ForeignKey(entity = DailyData.class,
                parentColumns = "id",
                childColumns = "parentDayId",
                onDelete = CASCADE),
        indices = @Index("parentDayId"))
public class Hourly {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long parentDayId;

    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("data")
    @Expose
    @Ignore
    private List<HourlyData> data = new ArrayList<>();

    public Hourly() {
    }

    public Hourly(String summary, String icon, List<HourlyData> data) {
        super();
        this.summary = summary;
        this.icon = icon;
        this.data = data;
    }

    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }

    public long getParentDayId(){ return parentDayId; }

    public void setParentDayId(long parentDayId){ this.parentDayId = parentDayId; }

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

    public List<HourlyData> getData() {
        return data;
    }

    public void setData(List<HourlyData> data) {
        this.data = data;
    }

}
