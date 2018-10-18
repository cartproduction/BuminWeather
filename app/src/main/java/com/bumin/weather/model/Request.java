package com.bumin.weather.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {

@SerializedName("type")
@Expose
private String type;
@SerializedName("query")
@Expose
private String query;

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public String getQuery() {
return query;
}

public void setQuery(String query) {
this.query = query;
}

}