package com.rickie.ordercenter.esorder;

import com.rickie.ordercenter.esorder.entity.Order;
import com.rickie.ordercenter.esorder.service.OrderService;
import com.rickie.ordercenter.esorder.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsOrderApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void readJsonFile() {
        String path = this.getClass().getClassLoader()
                .getResource("order_template.json")
                .getPath();
        String jsonFile = JsonUtil.readJsonFile(path);
        System.out.println(jsonFile);
    }

    @Autowired
    private OrderService orderService;

    @Test
    public void importOrders() throws InterruptedException {
        Random random = new Random(10);
        Random rndOrders = new Random(100);

        // 分100次导入业务订单
        for (int k = 0; k < 100; k++) {
            int orderCount = rndOrders.nextInt(100);
            if(orderCount <= 0) {
                orderCount = 1;
            }

            // 模拟导入数量为orderCount的业务订单
            List<Order> orders = new ArrayList<>();
            for (int i = 0; i < orderCount; i++) {
                long orderId = random.nextInt(1000000);
                Order order = new Order();
                order.setOrderId(orderId);
                order.setCreateTime(new Date());
                order.setSenderAddr("上海自贸区临港新片区No." + orderId);
                order.setSenderName(getRandomString(6));
                order.setMemo(getRandomString(10));
                orders.add(order);
            }

            Map<String, String> responses = orderService.addOrders(orders);
            for(Map.Entry<String, String> entry : responses.entrySet()){
                System.out.println("Key = "+entry.getKey()+", value="+entry.getValue());
            }

            int sleep = (int)(Math.random()*1000*60);
            System.out.println("休息 " + sleep/1000 + " 秒 ...");
            Thread.sleep(sleep);
        }
    }

    /**
     * length表示生成字符串的长度
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
