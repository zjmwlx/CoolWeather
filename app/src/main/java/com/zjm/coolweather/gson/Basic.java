package com.zjm.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zjm on 2017/7/1.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherid;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }

}
