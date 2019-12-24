package com.test.entity;

/**
 * Created by Administrator on 2019/12/18.
 */

public class Quwaybill {//待取货运单实体类
    /*"id": 17,
        "deliverymanId": 3,
        "orderId": "100000004",
        "statusId": 0,
        "deliveryId": "20191223144255813",
        "date": "2019-12-23T06:42:55.000+0000",
        "marketName": "五一市场",
        "pickCode": "65453",
        "freight": 10.0,
        "longitude": 112.6188755035,
        "latitude": 26.9196592391,
        "phone": "111111111",
        "address": "湖南省衡阳市雁峰区岳屏镇夕阳红公寓",
        "goodsName": "娃哈哈AD钙奶",
        "number": 6,
        "goodUrl": "http://193.112.29.26/RetailManager/static/images/commodity/002.jpg"*/
    private String id;
    private String orderid;//订单号
    private String date;//时间
    private String marketName;//取货地址
    private String pickCode;//取货码
    private String freight;//配送费
    private String longitude;//发货地址经度
    private String latitude;//发货地址纬度
    private String phone;//发货人手机号
    private String address;//送货地址
    private String goodsName;//货物名
    private String number;//货物数量
    private String goodUrl;//货物图片链接

    public Quwaybill(String id, String orderid, String date, String marketName, String pickCode, String freight, String longitude, String latitude, String phone, String address, String goodsName, String number, String goodUrl) {
        this.id = id;
        this.orderid = orderid;
        this.date = date;
        this.marketName = marketName;
        this.pickCode = pickCode;
        this.freight = freight;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phone = phone;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
