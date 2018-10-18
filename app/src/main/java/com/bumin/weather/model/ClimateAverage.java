package com.bumin.weather.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClimateAverage {

@SerializedName("month")
@Expose
private List<Month> month = null;

public List<Month> getMonth() {
return month;
}

public void setMonth(List<Month> month) {
this.month = month;
}

}