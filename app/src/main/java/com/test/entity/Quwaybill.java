package com.test.entity;

/**
 * Created by Administrator on 2019/12/18.
 */

public class Quwaybill {//待取货运单实体类
    private String id;
    private String orderid;//订单号
    private String date;//时间
    private String marketName;//取货点
    private String pickCode;//取货码
    private String freight;//配送费
    private String longitude;//经度
    private String latitude;//纬度
    private String phone;//手机号

    public Quwaybill(String id, String orderid, String date, String marketName, String pickCode, String freight, String longitude, String latitude, String phone) {
        this.id = id;
        this.orderid = orderid;
        this.date = date;
        this.marketName = marketName;
        this.pickCode = pickCode;
        this.freight = freight;
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

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getPickCode() {
        return pickCode;
    }

    public void setPickCode(String pickCode) {
        this.pickCode = pickCode;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
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
