package com.zjm.coolweather.util;

import android.text.TextUtils;

import com.zjm.coolweather.db.City;
import com.zjm.coolweather.db.County;
import com.zjm.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析返回的省、市、县数据，并保存到数据库
 */

public class Untility {
    /**
     * 解析返回的省份数据
     * @param response
     * @return
     */
    public static boolean handleProcinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allprovince = new JSONArray(response);
                for (int i = 0; i < allprovince.length(); i++) {
                    JSONObject provinceObject = allprovince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceNmae(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析返回的城市数据
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析返回的县区数据
     * @param response
     * @param cityId
     */
    public static boolean handleCountiesResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

