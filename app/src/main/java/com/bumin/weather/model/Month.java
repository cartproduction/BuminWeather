package com.bumin.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Month {

@SerializedName("index")
@Expose
private String index;
@SerializedName("name")
@Expose
private String name;
@SerializedName("avgMinTemp")
@Expose
private String avgMinTemp;
@SerializedName("avgMinTemp_F")
@Expose
private String avgMinTempF;
@SerializedName("absMaxTemp")
@Expose
private String absMaxTemp;
@SerializedName("absMaxTemp_F")
@Expose
private String absMaxTempF;
@SerializedName("avgDailyRainfall")
@Expose
private String avgDailyRainfall;

public String getIndex() {
return index;
}

public void setIndex(String index) {
this.index = index;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getAvgMinTemp() {
return avgMinTemp;
}

public void setAvgMinTemp(String avgMinTemp) {
this.avgMinTemp = avgMinTemp;
}

public String getAvgMinTempF() {
return avgMinTempF;
}

public void setAvgMinTempF(String avgMinTempF) {
this.avgMinTempF = avgMinTempF;
}

public String getAbsMaxTemp() {
return absMaxTemp;
}

public void setAbsMaxTemp(String absMaxTemp) {
this.absMaxTemp = absMaxTemp;
}

public String getAbsMaxTempF() {
return absMaxTempF;
}

public void setAbsMaxTempF(String absMaxTempF) {
this.absMaxTempF = absMaxTempF;
}

public String getAvgDailyRainfall() {
return avgDailyRainfall;
}

public void setAvgDailyRainfall(String avgDailyRainfall) {
this.avgDailyRainfall = avgDailyRainfall;
}

}