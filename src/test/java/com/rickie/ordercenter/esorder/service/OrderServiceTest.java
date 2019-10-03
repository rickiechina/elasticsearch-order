package com.rickie.ordercenter.esorder.service;

import com.rickie.ordercenter.esorder.entity.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void addOrder() throws ParseException {
        String ret = "";
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        ret = orderService.addOrder(new Order(100L, "rickie lee",
                "shanghai", "123456",
                sDateFormat.parse(sDateFormat.format(new Date())), "order 100 for test."));
        System.out.println(ret);

        ret = orderService.addOrder(new Order(101L, "tom lee",
                "beijing", "654321",
                sDateFormat.parse("2019-10-01T18:55:17.123+0800"), "order 101 for test."));

        System.out.println(ret);
    }

    @Test
    public void addOrders() throws ParseException {
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Order> list = new ArrayList<>();
        list.add(new Order(1000L, "Lucy",
                "上海市浦东新区", "13012345678",
                sDateFormat.parse(sDateFormat.format(new Date())), "order 1000 for test."));
        list.add(new Order(1001L, "Candy",
                "上海自贸区临港新片区", "13088888888",
                sDateFormat.parse("2019-10-08 11:12:18"), "order 1001 for test."));
        list.add(new Order(1002L, "Rose", "上海自贸区临港新片区", "13066668888",
                sDateFormat.parse("2019-10-10 11:12:18"), "to Jack"));
        Map<String, String> response = orderService.addOrders(list);
        for(Map.Entry<String, String> entry : response.entrySet()){
            System.out.println("Key = "+entry.getKey()+", value="+entry.getValue());
        }
    }



    @Test
    public void deleteByOrder() throws ParseException {
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Order order = new Order(1000L, "Lucy",
                "上海市浦东新区", "13012345678",
                sDateFormat.parse(sDateFormat.format(new Date())),
                "order 1000 for test.");
        long deleted = orderService.deleteByOrder(order);
        System.out.println("deleted: " + deleted);
        Assert.assertTrue(deleted<=1);
    }

    @Test
    public void deleteOrders() throws ParseException {
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Order> list = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderId(101);
        order1.setCreateTime(sDateFormat.parse("2019-08-10 11:12:13"));
        list.add(order1);
        Map<String, String> map = orderService.deleteOrders(list);

        for(Map.Entry<String, String> entry : map.entrySet()){
            System.out.println("Key = "+entry.getKey()+", value="+entry.getValue());
        }
        Assert.assertTrue(map.size()>=0);
    }

    @Test
    public void getByOrderId() {
        Order order = orderService.getByOrderId(100);
        if(order != null) {
            System.out.println(order.toString());
            Assert.assertTrue(order.getOrderId() == 100);
        }
    }

    @Test
    public void getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        orders.forEach(System.out::println);
        Assert.assertTrue(orders.size() > 0);
    }

    @Test
    public void searchByCreateTime() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date from = df.parse("2019-10-08 11:12:18");
        Date to = df.parse("2019-10-08 11:12:18");
        List<Order> res = orderService.searchByCreateTime(from, to);
        res.forEach(System.out::println);
    }

}