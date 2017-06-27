package com.zjm.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zjm on 2017/6/27.
 */

public class Province extends DataSupport{
    private int id;
    private String provinceNmae;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceNmae() {
        return provinceNmae;
    }

    public void setProvinceNmae(String provinceNmae) {
        this.provinceNmae = provinceNmae;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
