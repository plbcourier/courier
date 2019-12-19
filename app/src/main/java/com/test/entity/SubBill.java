package com.test.entity;

/**
 * Created by Administrator on 2019/12/19.
 */

public class SubBill {//提现信息实体类
    private String id;
    private String deliverymanId;//骑手id
    private String status;// "申请提现"
    private String time;//时间
    private String nowLeftMoney;//操作后余额
    private String orderMoney;//操作变动金额

    public SubBill(String id, String deliverymanId, String status, String time, String nowLeftMoney, String orderMoney) {
        this.id = id;
        this.deliverymanId = deliverymanId;
        this.status = status;
        this.time = time;
        this.nowLeftMoney = nowLeftMoney;
        this.orderMoney = orderMoney;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeliverymanId() {
        return deliverymanId;
    }

    public void setDeliverymanId(String deliverymanId) {
        this.deliverymanId = deliverymanId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNowLeftMoney() {
        return nowLeftMoney;
    }

    public void setNowLeftMoney(String nowLeftMoney) {
        this.nowLeftMoney = nowLeftMoney;
    }

    public String getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(String orderMoney) {
        this.orderMoney = orderMoney;
    }
}
