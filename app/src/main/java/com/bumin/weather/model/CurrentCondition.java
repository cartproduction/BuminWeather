package com.bumin.weather.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentCondition {

@SerializedName("observation_time")
@Expose
private String observationTime;
@SerializedName("temp_C")
@Expose
private String tempC;
@SerializedName("temp_F")
@Expose
private String tempF;
@SerializedName("weatherCode")
@Expose
private String weatherCode;
@SerializedName("weatherIconUrl")
@Expose
private List<WeatherIconUrl> weatherIconUrl = null;
@SerializedName("weatherDesc")
@Expose
private List<WeatherDesc> weatherDesc = null;
@SerializedName("windspeedMiles")
@Expose
private String windspeedMiles;
@SerializedName("windspeedKmph")
@Expose
private String windspeedKmph;
@SerializedName("winddirDegree")
@Expose
private String winddirDegree;
@SerializedName("winddir16Point")
@Expose
private String winddir16Point;
@SerializedName("precipMM")
@Expose
private String precipMM;
@SerializedName("humidity")
@Expose
private String humidity;
@SerializedName("visibility")
@Expose
private String visibility;
@SerializedName("pressure")
@Expose
private String pressure;
@SerializedName("cloudcover")
@Expose
private String cloudcover;
@SerializedName("FeelsLikeC")
@Expose
private String feelsLikeC;
@SerializedName("FeelsLikeF")
@Expose
private String feelsLikeF;

public String getObservationTime() {
return observationTime;
}

public void setObservationTime(String observationTime) {
this.observationTime = observationTime;
}

public String getTempC() {
return tempC;
}

public void setTempC(String tempC) {
this.tempC = tempC;
}

public String getTempF() {
return tempF;
}

public void setTempF(String tempF) {
this.tempF = tempF;
}

public String getWeatherCode() {
return weatherCode;
}

public void setWeatherCode(String weatherCode) {
this.weatherCode = weatherCode;
}

public List<WeatherIconUrl> getWeatherIconUrl() {
return weatherIconUrl;
}

public void setWeatherIconUrl(List<WeatherIconUrl> weatherIconUrl) {
this.weatherIconUrl = weatherIconUrl;
}

public List<WeatherDesc> getWeatherDesc() {
return weatherDesc;
}

public void setWeatherDesc(List<WeatherDesc> weatherDesc) {
this.weatherDesc = weatherDesc;
}

public String getWindspeedMiles() {
return windspeedMiles;
}

public void setWindspeedMiles(String windspeedMiles) {
this.windspeedMiles = windspeedMiles;
}

public String getWindspeedKmph() {
return windspeedKmph;
}

public void setWindspeedKmph(String windspeedKmph) {
this.windspeedKmph = windspeedKmph;
}

public String getWinddirDegree() {
return winddirDegree;
}

public void setWinddirDegree(String winddirDegree) {
this.winddirDegree = winddirDegree;
}

public String getWinddir16Point() {
return winddir16Point;
}

public void setWinddir16Point(String winddir16Point) {
this.winddir16Point = winddir16Point;
}

public String getPrecipMM() {
return precipMM;
}

public void setPrecipMM(String precipMM) {
this.precipMM = precipMM;
}

public String getHumidity() {
return humidity;
}

public void setHumidity(String humidity) {
this.humidity = humidity;
}

public String getVisibility() {
return visibility;
}

public void setVisibility(String visibility) {
this.visibility = visibility;
}

public String getPressure() {
return pressure;
}

public void setPressure(String pressure) {
this.pressure = pressure;
}

public String getCloudcover() {
return cloudcover;
}

public void setCloudcover(String cloudcover) {
this.cloudcover = cloudcover;
}

public String getFeelsLikeC() {
return feelsLikeC;
}

public void setFeelsLikeC(String feelsLikeC) {
this.feelsLikeC = feelsLikeC;
}

public String getFeelsLikeF() {
return feelsLikeF;
}

public void setFeelsLikeF(String feelsLikeF) {
this.feelsLikeF = feelsLikeF;
}

}