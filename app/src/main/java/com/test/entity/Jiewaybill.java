package com.test.entity;

/**
 * Created by Administrator on 2019/12/17.
 */

public class Jiewaybill {//待接单运单实体类
    private String orderid;//订单号
    private String freight;//配送费
    private String distance;//距离A
    private String goodsName;//货品名
    private String marketName;//起点，取货点
    private String address;//终点，送货点

    public Jiewaybill(String orderid, String freight, String distance, String goodsName, String marketName, String address) {
        this.orderid = orderid;
        this.freight = freight;
        this.distance = distance;
        this.goodsName = goodsName;
        this.marketName = marketName;
        this.address = address;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
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

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
