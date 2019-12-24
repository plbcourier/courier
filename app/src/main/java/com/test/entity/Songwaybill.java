package com.test.entity;

/**
 * Created by Administrator on 2019/12/18.
 */

public class Songwaybill {//待送达运单实体类
    /*"id": 16,
        "deliverymanId": 3,
        "orderId": "100000003",
        "statusId": 1,
        "deliveryId": "20191223143948818",
        "date": "2019-12-23T06:39:48.000+0000",
        "marketName": "三泰市场",
        "pickCode": "4567",
        "freight": 10.0,
        "longitude": 112.57018,
        "latitude": 26.89071,
        "phone": "17623574133",
        "address": "湖南衡阳市雁峰区岳屏镇夕阳红公寓",
        "goodsName": "奥利奥",
        "number": 2,
        "goodUrl": "RetailManager/static/images/commodity/20190109-011.jpg"*/
    private String id;
    private String orderId;//运单号
    private String freight;//配送费
    private String address;//收货地址
    private String date;//时间
    private String longitude;//收货地址经度
    private String latitude;//收货地址纬度
    private String phone;//收货人手机号
    private String marketName;//发货地址
    private String goodsName;//货物名
    private String number;//货物数量
    private String goodUrl;//货物图片链接

    public Songwaybill(String id, String orderId, String freight, String address, String date, String longitude, String latitude, String phone, String marketName, String goodsName, String number, String goodUrl) {
        this.id = id;
        this.orderId = orderId;
        this.freight = freight;
        this.address = address;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
        this.marketName = marketName;
        this.goodsName = goodsName;
        this.number = number;
        this.goodUrl = goodUrl;
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

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGoodUrl() {
        return goodUrl;
    }

    public void setGoodUrl(String goodUrl) {
        this.goodUrl = goodUrl;
    }
}
