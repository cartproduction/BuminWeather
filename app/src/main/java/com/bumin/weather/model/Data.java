package com.bumin.weather.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("request")
@Expose
private List<Request> request = null;
@SerializedName("current_condition")
@Expose
private List<CurrentCondition> currentCondition = null;
@SerializedName("weather")
@Expose
private List<Weather> weather = null;
@SerializedName("ClimateAverages")
@Expose
private List<ClimateAverage> climateAverages = null;

public List<Request> getRequest() {
return request;
}

public void setRequest(List<Request> request) {
this.request = request;
}

public List<CurrentCondition> getCurrentCondition() {
return currentCondition;
}

public void setCurrentCondition(List<CurrentCondition> currentCondition) {
this.currentCondition = currentCondition;
}

public List<Weather> getWeather() {
return weather;
}

public void setWeather(List<Weather> weather) {
this.weather = weather;
}

public List<ClimateAverage> getClimateAverages() {
return climateAverages;
}

public void setClimateAverages(List<ClimateAverage> climateAverages) {
this.climateAverages = climateAverages;
}

}