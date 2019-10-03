package com.rickie.ordercenter.esorder.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Order {
    @JSONField(name = "orderid")
    private long orderId;

    @JSONField(name = "sender_name")
    private String senderName;

    @JSONField(name = "sender_addr")
    private String senderAddr;

    @JSONField(name = "sender_mobile")
    private String senderMobile;

    @JSONField(name = "create_time", format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date createTime;
    private String memo;

    private static final String orderIndexPrefix = "order_";

    public String getIndexName() {
        try{
            if(createTime == null) {
                throw new Exception("createTime can not be NULL.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return orderIndexPrefix + "300001";
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(createTime);
        String indexName = orderIndexPrefix + cal.get(Calendar.YEAR);
        String month=String.valueOf(cal.get(Calendar.MONTH) + 1);
        if(month.length()==1) {
            month = "0" + month;
        }
        indexName += month;

        return indexName;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddr() {
        return senderAddr;
    }

    public void setSenderAddr(String senderAddr) {
        this.senderAddr = senderAddr;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Order() {

    }

    public Order(long orderId, String senderName, String senderAddr, String senderMobile, Date createTime, String memo) {
        this.orderId = orderId;
        this.senderName = senderName;
        this.senderAddr = senderAddr;
        this.senderMobile = senderMobile;
        this.createTime = createTime;
        this.memo = memo;
    }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return "Order{" +
                "orderId=" + orderId +
                ", senderName='" + senderName + '\'' +
                ", senderAddr='" + senderAddr + '\'' +
                ", senderMobile='" + senderMobile + '\'' +
                ", createTime=" + df.format(createTime) +
                ", memo='" + memo + '\'' +
                '}';
    }
}
