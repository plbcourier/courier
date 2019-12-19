package com.test.entity;

/**
 * Created by Administrator on 2019/12/18.
 */

public class Songwaybill {//待送达运单实体类
    private String id;
    private String orderId;//运单号
    private String freight;//配送费
    private String address;//送货地址
    private String date;//时间
    private String longitude;//经度
    private String latitude;//纬度
    private String phone;//手机号

    public Songwaybill(String id, String orderId, String freight, String address, String date, String longitude, String latitude, String phone) {
        this.id = id;
        this.orderId = orderId;
        this.freight = freight;
        this.address = address;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
