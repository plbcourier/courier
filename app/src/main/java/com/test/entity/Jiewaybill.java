package com.test.entity;

/**
 * Created by Administrator on 2019/12/17.
 */

public class Jiewaybill {//待接单运单实体类
    /*{
        "id": 1,
        "address": "湖南衡阳科学城B2栋",终点
        "consignee": "张三",收货人名字
        "freightInsurance": 0,货物保险
        "leaveMessage": "",离开消息
        "money": 200.00,订单金额
        "orderid": 100000001,订单id
        "orderTime": "2019-07-02T02:05:36.000+0000",订单时间
        "phone": "15773411484",发货人电话
        "state": 2,
        "freight": 10.0,配送费
        "userId": 1,
        "marketName": "三泰市场",
        "longitude": 112.57018,发货点经度
        "latitude": 26.89071,发货点纬度
        "pickCode": "1222",取货码
        "distance": 11900.7263,我的位置与发货点距离
        "storeName": null,店铺名
        "goodsName": "伊利牛奶",货物名
        "distanceEnd": 11903.2324,发货点与收货点距离
        "goodUrl": "http://193.112.29.26/RetailManager/static/images/commodity/20181228-008.jpg",图片
        "number": 4
    }*/
    private String address;//收货地址
    private String freightInsurance;//货物保险费
    private String leaveMessage;//消息
    private String money;//运单金额
    private String orderid;//运单id
    private String orderTime;//订单时间
    private String phone;//发货人电话
    private String freight;//配送费
    private String marketName;//发货地址
    private String longitude;//发货地址经度
    private String latitude;//发货地址纬度
    private String distance;//我的位置与发货点距离
    private String goodsName;//货物名
    private String distanceEnd;//发货点与收货点距离
    private String goodUrl;//图片链接
    private String number;//货物数量

    public Jiewaybill(String address, String freightInsurance, String leaveMessage, String money, String orderid, String orderTime, String phone, String freight, String marketName, String longitude, String latitude, String distance, String goodsName, String distanceEnd, String goodUrl, String number) {
        this.address = address;
        this.freightInsurance = freightInsurance;
        this.leaveMessage = leaveMessage;
        this.money = money;
        this.orderid = orderid;
        this.orderTime = orderTime;
        this.phone = phone;
        this.freight = freight;
        this.marketName = marketName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
        this.goodsName = goodsName;
        this.distanceEnd = distanceEnd;
        this.goodUrl = goodUrl;
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFreightInsurance() {
        return freightInsurance;
    }

    public void setFreightInsurance(String freightInsurance) {
        this.freightInsurance = freightInsurance;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getDistanceEnd() {
        return distanceEnd;
    }

    public void setDistanceEnd(String distanceEnd) {
        this.distanceEnd = distanceEnd;
    }

    public String getGoodUrl() {
        return goodUrl;
    }

    public void setGoodUrl(String goodUrl) {
        this.goodUrl = goodUrl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
