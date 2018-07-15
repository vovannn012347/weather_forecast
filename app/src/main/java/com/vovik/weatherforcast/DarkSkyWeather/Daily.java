
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

@Entity(tableName = "Daily",
        foreignKeys = @ForeignKey(entity = PlaceWeather.class,
                parentColumns = "id",
                childColumns = "parentPlaceId",
                onDelete = CASCADE),
        indices = @Index("parentPlaceId"))
public class Daily {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long parentPlaceId;

    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("icon")
    @Expose
    private String icon;

    @SerializedName("data")
    @Expose
    @Ignore
    private List<DailyData> data = new ArrayList<>();

    public Daily() {
    }

    public Daily(String summary, String icon, List<DailyData> data) {
        super();
        this.summary = summary;
        this.icon = icon;
        this.data = data;
    }

    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }

    public long getParentPlaceId(){ return parentPlaceId; }

    public void setParentPlaceId(long parentPlaceId){ this.parentPlaceId = parentPlaceId; }

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

    public List<DailyData> getData() {
        return data;
    }

    public void setData(List<DailyData> data) {
        this.data = data;
    }

}
