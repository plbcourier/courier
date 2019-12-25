package com.test.entity;

/**
 * Created by Administrator on 2019/12/24.
 */

//-----------坐标参数对象实体类-----------
public class Coords {
    private Double longitude = 112.586483;//经度
    private Double latitude = 26.828654;//纬度
    private String address = "";//地址字符串

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
