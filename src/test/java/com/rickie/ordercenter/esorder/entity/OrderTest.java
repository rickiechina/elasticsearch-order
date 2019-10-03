package com.rickie.ordercenter.esorder.entity;

import com.alibaba.fastjson.JSON;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class OrderTest {
    @Test
    public void jsonTest() throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Order order = new Order(
                10000, "Rickie", "上海自贸区临港新片区",
                "13900000000", sdf.parse(sdf.format(new Date())),
                "unit test"
        );

        String jsonStr = JSON.toJSONString(order);
        System.out.println(jsonStr);

        Order newOrder = JSON.parseObject(jsonStr, Order.class);
        System.out.println(newOrder.toString());
    }

    @Test
    public void orderIndexName() throws ParseException {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Order order = new Order(
                10000, "Rickie", "上海自贸区临港新片区",
                "13900000000", sdf.parse("2019-10-08 12:13:16"),
                "unit test"
        );

        String indexName = order.getIndexName();
        System.out.println(indexName);
        Assert.assertTrue(indexName.equals("order_201910"));
    }

    @Test
    public void testDate() {
        String FULL_FORMAT="yyyy-MM-dd'T'HH:mm:ssZ";
        Date now=new Date();
        System.out.println(new SimpleDateFormat(FULL_FORMAT).format(now));
    }
}